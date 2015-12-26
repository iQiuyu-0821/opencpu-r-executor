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
package io.onetapbeyond.opencpu.r.executor.util;

/*
 * OpenCPU API call constants.
 */
public interface OCPUConstants {

	// OpenCPU POST Method function call endpoints. 
	public static final String LIBRARY_CALL_BASE = "/library/";
	public static final String GITHUB_CALL_BASE = "/github/";
	public static final String GIST_CALL_BASE = "/gist/";
	public static final String CRAN_CALL_BASE = "/cran/";
	public static final String BIOC_CALL_BASE = "/bioc/";

	// OpenCPU POST Method function v script call. 
	public static final String FUNCTION = "R";
	public static final String SCRIPT = "scripts";

	public static final String SLASH = "/";
	public static final String JSON = "/json";
	public static final String UNDEFINED = "UNDEFINED";

}