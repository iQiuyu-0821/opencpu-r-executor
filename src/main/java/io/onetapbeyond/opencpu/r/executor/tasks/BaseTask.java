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
import java.util.*;
import okhttp3.*;
import com.google.gson.*;

public abstract class BaseTask implements OCPUTask {

	private static final OkHttpClient okClient = new OkHttpClient();

	protected String user; 
	protected String pkg;
	protected String function;
	protected boolean script;
	protected String input;
	protected String output;
	protected String endpoint;

	private String[] outputData = null;

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
	        Response response = request(apiEndpoint,
	        							input,
	        							METHOD_POST);

	        if(!response.isSuccessful()) {
				throw new OCPUException(apiEndpoint + 
					" execution failed, " + response.message());
			}

			String ocpuSession = response.header(OCPU_X_SESSION);

        	if(endpoint.endsWith(JSON)) {

	        	/*
	        	 * On OpenCPU function call, capture R function
	        	 * return value on directly on response.
	        	 */
				String objAsJson = response.body().string();

				/*
				 * Function call has single JSON response. Capture
				 * in serializable form: ["function", "jsonValue"].
				 */
				outputData = new String[2];
				outputData[0] = function;
				outputData[1] = objAsJson;

				/*
				 * Build execution result for successful function call.
				 */
				oResult =
					new OCPUResultImpl(true, input, outputData, null,
						null, (System.currentTimeMillis()-execstart));

			} else {

	        	/*
	        	 * On OpenCPU script call, capture R script
	        	 * return values on request.
	        	 */

				if(output != null) {

					outputData = new String[2];

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
						outputData[0] = output;
						outputData[1] = objAsJson;

					} catch(Exception dex) {
						// Swallow, store null, continue.
						outputData[0] = output;
						outputData[1] = null;
					}

					/*
					 * Build execution result for successful script call.
					 */
					oResult =
						new OCPUResultImpl(true, input, outputData, null,
							null, (System.currentTimeMillis()-execstart));

				} // output != null

			}

		} catch(Exception ex) {
			String msg = "Task execution failed.";
            oResult = new OCPUResultImpl(false, input, null, msg, ex, 0L);
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

	private Response request(String apiEndpoint,
							 String reqData,
						     String reqMethod) throws OCPUException {

        Response response = null;

		Request.Builder rb = new Request.Builder().url(apiEndpoint);
		Request request = null;

		if(reqMethod.equals(METHOD_POST)) {
			if(reqData == null)
				reqData = EMPTY_JSON;
			request = rb.post(RequestBody.create(MEDIA_TYPE_JSON,
												 reqData)).build();
		} else
		if(reqMethod.equals(METHOD_GET)) {
			request = rb.build();
		}

		try {
			response = okClient.newCall(request).execute();
			if(!response.isSuccessful()) {
				throw new OCPUException(apiEndpoint +
					" execution failed, " + response.message());
			}
		} catch(OCPUException oex) {
			throw oex;
		}catch(Exception ex) {
			throw new OCPUException(apiEndpoint + " request failed." + ex);
		}



		return response;
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

			String fetchEndpoint = ocpuEndpoint +
				OCPU_SESSION_DATA(ocpuSession, objectName);

	        Response response =
	        	request(fetchEndpoint, null, METHOD_GET);

	        if(!response.isSuccessful()) {
				throw new OCPUException("OCPU fetch " +
					objectName + " failed, " + response.message());
			} else {
				objAsJson = response.body().string();
			}

		} catch(OCPUException oex) {
			throw oex;
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
	private static final String EMPTY_JSON = "{}";
	public static final MediaType MEDIA_TYPE_JSON
      = MediaType.parse("application/json; charset=utf-8");
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
