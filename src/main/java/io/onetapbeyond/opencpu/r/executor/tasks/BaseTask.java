/*
 * Copyright 2015 David Russell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.onetapbeyond.opencpu.r.executor.tasks;

import static io.onetapbeyond.opencpu.r.executor.util.OCPUConstants.*;
import io.onetapbeyond.opencpu.r.executor.*;
import io.onetapbeyond.opencpu.r.executor.results.OCPUResultImpl;
import java.io.*;
import java.net.*;
import java.util.*;
import com.google.gson.*;

public abstract class BaseTask implements OCPUTask {

	protected String user; 
	protected String pkg;
	protected String function;
	protected boolean script;
	protected String input;
	protected String output;
	protected String endpoint;

	private String[] resultData = null;

	/*
	 * Execute the R task.
	 * @return the result of the R task
	 */
	public OCPUResult execute() {
		return execute(OCPU_DEFAULT_ENDPOINT);	
	}

	/*
	 * Execute the R task.
	 * @param endpoint the OpenCPU server endpoint
	 * @return the result of the R task
	 */
	public OCPUResult execute(String serverEndpoint) {

		OCPUResult oResult = null;

		try {

			long execstart = System.currentTimeMillis();

			/*
			 * Validate R task specification ahead of
			 * execution attempt on the OCPU server.
			 */
			validate();

			String apiEndpoint = serverEndpoint + endpoint;
	        HttpURLConnection conn = connect(apiEndpoint,
	        								 input,
	        								 JSON_REQ_TYPE,
	        								 METHOD_POST);

	        int respCode = conn.getResponseCode();
			if(respCode == HttpURLConnection.HTTP_OK ||
				respCode == HttpURLConnection.HTTP_CREATED) {

				String ocpuSession = conn.getHeaderField(OCPU_X_SESSION);

	        	if(endpoint.endsWith(JSON)) {

		        	/*
		        	 * On OpenCPU function call, capture R function
		        	 * return value on directly on response.
		        	 */

			        BufferedReader respBuf = new BufferedReader(
			        	new InputStreamReader(conn.getInputStream(),"utf-8"));  
			 
			        StringBuilder respData = new StringBuilder();  
		            String text = null;
		            while ((text = respBuf.readLine()) != null) {  
			            respData.append(text + "\n");  
		            }
					respBuf.close();

					String objAsJson = respData.toString();

					/*
					 * Function call has single JSON response. Capture
					 * in serializable form: ["function", "jsonValue"].
					 */
					resultData = new String[2];
					resultData[0] = function;
					resultData[1] = objAsJson;

					/*
					 * Build execution result for successful function call.
					 */
					oResult =
						new OCPUResultImpl(true, resultData, null,
							null, (System.currentTimeMillis()-execstart));

				} else {

		        	/*
		        	 * On OpenCPU script call, capture R script
		        	 * return values on request.
		        	 */

					if(output != null) {

						resultData = new String[2];

						/*
						 * For the requested object, capture in
						 * serializable form: ["output", "jsonValue"].
						 */
						int objectIndex = 0;

						try {

							String objAsJson = fetchOutput(output,
														   ocpuSession,
														   serverEndpoint);
							/*
							 * Capture in serializable form:
							 * ["output", "jsonValue"].
							 */
							resultData[0] = output;
							resultData[1] = objAsJson;

						} catch(Exception dex) {
							// Swallow, store null, continue.
							resultData[0] = output;
							resultData[1] = null;
						}

						/*
						 * Build execution result for successful script call.
						 */
						oResult =
							new OCPUResultImpl(true, resultData, null,
								null, (System.currentTimeMillis()-execstart));

					} // output != null

				}

			} else {

	            StringBuffer causeMsg = new StringBuffer()
	            						.append("HTTP ")
	            						.append(conn.getResponseMessage())
	            						.append(", error code ")
	            						.append(conn.getResponseCode())
	            						.append(".");
	            StringBuffer errMsg = new StringBuffer(toString())
	            						.append(": ")
	            						.append(causeMsg);
	            OCPUException oEx = new OCPUException(causeMsg.toString());

	            oResult =
	            	new OCPUResultImpl(false, null, errMsg.toString(), oEx, 0L);
	        }

		} catch(Exception ex) {
			String msg = "Task execution failed.";
            oResult = new OCPUResultImpl(false, null, msg, ex, 0L);
		}

		return oResult;

	}

	public String toString() {
		return endpoint;
	}

	protected String endpoint(String base) {

		String type = script ? SCRIPT : FUNCTION;
		StringBuffer sb = new StringBuffer(base);

		if(user != null)
			sb.append(user).append(SLASH);

		sb.append(pkg).append(SLASH).append(type)
					  .append(SLASH).append(function);

		if(!script)
			sb.append(SLASH).append(JSON);

	    return sb.toString();
	}

	private void validate() throws OCPUException {
		if(endpoint.contains(UNDEFINED))
			throw new OCPUException("Task specification incomplete.");
	}

	private HttpURLConnection connect(String apiEndpoint,
									  String reqData,
									  String reqType,
									  String reqMethod) throws OCPUException {

        HttpURLConnection conn = null;

		try {

			URL apiCall = new URL(apiEndpoint);
	        conn = (HttpURLConnection)apiCall.openConnection();

	        conn.setRequestProperty("Content-Type", reqType);
	        conn.setRequestProperty("Accept", ACCEPT_REQ_TYPE);
	        conn.setRequestMethod(reqMethod);

	        conn.setDoInput(true);

	        if(reqData != null) {

		        conn.setDoOutput(true);
		        OutputStream os = conn.getOutputStream();
		        os.write(reqData.getBytes("UTF-8"));
	        	os.close();	
	        }

		} catch(Exception ex) {
			throw new OCPUException(apiEndpoint + " execution failed.", ex);
		}

		return conn;
	}

	/*
	 * Fetch workspace object data as JSON from OCPU session.
	 */
	private String fetchOutput(String objectName,
							   String ocpuSession,
							   String ocpuEndpoint)
									throws OCPUException {

        String objAsJson = null;

		try {

			String fetchEndpoint =
				ocpuEndpoint +
				OCPU_SESSION_DATA(ocpuSession, objectName);

	        HttpURLConnection dataConn = connect(fetchEndpoint,
		        								 null,
		        								 JSON_REQ_TYPE,
		        								 METHOD_GET);

	        int respCode = dataConn.getResponseCode();

			if(respCode == HttpURLConnection.HTTP_OK) {

		        BufferedReader respBuf = new BufferedReader(
		        	new InputStreamReader(dataConn.getInputStream(), "utf-8"));  
		 
		        StringBuilder respData = new StringBuilder();  
	            String text = null;
	            while ((text = respBuf.readLine()) != null) {  
		            respData.append(text + "\n");  
	            }
				respBuf.close();

				objAsJson = respData.toString();
			} else {

			}

		} catch(Exception fex) {
			throw new OCPUException("OCPU fetch " +
										objectName + " failed.", fex);
		}

		return objAsJson;
	}

	private static Gson gson = new Gson();
    private static final String METHOD_POST = "POST";
    private static final String METHOD_GET = "GET";
	private static final String JSON = "json";
    private static final String JSON_REQ_TYPE = "application/json";
    private static final String ACCEPT_REQ_TYPE =
									"application/json,text/plain";
	private static final String OCPU_X_SESSION = "X-ocpu-session";
	public static final String OCPU_SESSION_BASE = "/tmp/";
	public static final String OCPU_WORKSPACE = "/R/";

	public static final String OCPU_SESSION_DATA(String session,
												 String objectName) {

		StringBuffer sb = new StringBuffer(OCPU_SESSION_BASE)
							  .append(session)
							  .append(OCPU_WORKSPACE)
							  .append(objectName)
							  .append("/json");

	    return sb.toString();
	}

	private static final String OCPU_DEFAULT_ENDPOINT =
							"http://localhost:8004/ocpu";

}
