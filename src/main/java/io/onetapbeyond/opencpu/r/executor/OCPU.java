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

import io.onetapbeyond.opencpu.r.executor.tasks.*;
import java.util.Map;
import com.google.gson.*;

/**
 * Builder of executable R tasks targeting the OpenCPU server
 * for scientific computing, reproducible research and data analysis
 * based on R. To create an instance of an {@link OCPUTask} capable
 * of executing any function or script within CRAN, Bioconductor
 * or Github R packages, use the following builder pattern:
 * <pre> 
 * {@code
 * OCPUTask oTask = OCPU.R().pkg(pName).function(fName).input(fInput).library();
 * }
 * </pre>
 */
public class OCPU {

	private static Gson gson = new Gson();

	private String user; 
	private String pkg; 
	private String function; 
	private boolean script;
	private String input;
	private String output;

	private OCPU() {}

	/**
	 * Initialize {@link OCPUTask} builder instance.
	 * @return initialized {@link OCPUTask} builder instance.
	 */
	public static OCPU R() {
		return new OCPU();
	}

	/**
	 * Specify user on the {@link OCPUTask}.
	 *
	 * Users are optional. When specified, the user indicates
	 * the private library, github, gist R package owner of the
	 * function or script being executed by the task.
	 *
	 * @param user the user on the {@link OCPUTask}.
	 * @return {@link OCPUTask} builder instance.
	 */
	public OCPU user(String user) {
		this.user = user;
		return this;
	}

	/**
	 * Specify R package name on the {@link OCPUTask}.
	 *
	 * @param pkg the package on the {@link OCPUTask}.
	 * @return {@link OCPUTask} builder instance.
	 */
	public OCPU pkg(String pkg) {
		this.pkg = pkg;
		return this;
	}

	/**
	 * Specify R function name on the {@link OCPUTask}.
	 *
	 * @param function the function on the {@link OCPUTask}.
	 * @return {@link OCPUTask} builder instance.
	 */
	public OCPU function(String function) {
		this.function = function;
		return this;
	}

	/**
	 * Specify R script name on the {@link OCPUTask}.
	 *
	 * @param script the script on the {@link OCPUTask}.
	 * @param output named R object to be returned on the {@link OCPUTask}.
	 * @return {@link OCPUTask} builder instance.
	 */
	public OCPU script(String script, String output) {
		this.function = script;
		this.script = true;
		this.output = output;
		return this;
	}

	/**
	 * Specify input data on the {@link OCPUTask}.
	 *
	 * @param input a Map representing the JSON data input
	 * @return {@link OCPUTask} builder instance
	 * @throws OCPUException if input can not be converted to valid JSON
	 */
	public OCPU input(Map input) throws OCPUException {

		try {
			this.input = gson.toJson(input);	
		} catch(Exception gex) {
			throw new OCPUException("Task input data invalid.", gex);
		}
		return this;
	}

	/**
	 * Build an {@link OCPUTask} using an R package on the OpenCPU server.
	 * 
	 * @return an executable {@link OCPUTask}.
	 */
	public OCPUTask library() {
		return new LibraryTask(user, pkg, function, script, input, output);
	}

	/**
	 * Build an {@link OCPUTask} using an R package on CRAN.
	 *
	 * @return an executable {@link OCPUTask}.
	 */
	public OCPUTask cran() {
		return new CRANTask(user, pkg, function, script, input, output);
	}

	/**
	 * Build an {@link OCPUTask} using an R package on Bioconductor.
	 *
	 * @return an executable {@link OCPUTask}.
	 */
	public OCPUTask bioc() {
		return new BIOCTask(user, pkg, function, script, input, output);
	}

	/**
	 * Build an {@link OCPUTask} using an R package on GitHub.
	 *
	 * @return an executable {@link OCPUTask}.
	 */
	public OCPUTask github() {
		return new GitHubTask(user, pkg, function, script, input, output);
	}

	/**
	 * Build an {@link OCPUTask} using an R package on Gist.
	 *
	 * @return an executable {@link OCPUTask}.
	 */
	public OCPUTask gist() {
		return new GistTask(user, pkg, function, script, input, output);
	}

}
