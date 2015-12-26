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
package io.onetapbeyond.opencpu.r.executor;

import java.util.Map;

/**
 * Executable R task targeting the OpenCPU server. Tasks can be
 * instantiated using the {@link OCPU} task builder.
 * An instance of {@link OCPUTask} can be executed as follows:
 * <pre> 
 * {@code
 * OCPUResult oResult = rTask.execute();
 * }
 * </pre>
 * <p>
 * All {@link OCPUTask} are serializable making them compatible
 * with Java cluster computing solutions, such as <i>Apache Spark</i>. 
 */
public interface OCPUTask extends java.io.Serializable {

	/**
	 * Execute the R task on the OpenCPU server at the default endpoint.
	 * The OpenCPU server default endpoint is http://localhost:8004/ocpu.
	 * @return the result of the R task
	 */
	public OCPUResult execute();

	/**
	 * Execute the R task on the OpenCPU server at the provided endpoint.
	 * @param endpoint the OpenCPU server endpoint
	 * @return the result of the R task
	 */
	public OCPUResult execute(String endpoint);

}
