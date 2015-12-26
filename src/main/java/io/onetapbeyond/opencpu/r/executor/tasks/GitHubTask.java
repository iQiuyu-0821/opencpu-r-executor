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

public class GitHubTask extends BaseTask {

	GitHubTask() {}

	public GitHubTask(String user, String pkg,
					  String function, boolean script,
					  String input, String output) {
		this.user = (user != null) ? user : UNDEFINED;	
		this.pkg = (pkg != null) ? pkg  : UNDEFINED;	
		this.function = (function != null) ? function  : UNDEFINED;	
		this.script = script;
		this.input = input;
		this.output = output;
		this.endpoint = super.endpoint(GITHUB_CALL_BASE);
	}

}
