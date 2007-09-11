/*
 * Copyright 2005, 2006 Dean Wampler. All rights reserved.
 * http://www.contract4j.org
 *
 * Licensed under the Eclipse Public License - v 1.0; you may not use this
 * software except in compliance with the License. You may obtain a copy of the 
 * License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * A copy is also included with this distribution. See the "LICENSE" file.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
package org.contract4j5.interpreter.bsf.stub;

import java.util.HashMap;
import java.util.Map;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.contract4j5.interpreter.bsf.BSFEngineAdapter;
import org.contract4j5.interpreter.bsf.jexl.JexlBSFEngine;

public abstract class StubBSFEngine extends BSFEngineAdapter {

	public static void registerWithBSF() {
		BSFManager.registerScriptingEngine(
				"stub", 
				JexlBSFEngine.class.getName(),
				new String[] { "stub" }
			);
	}

	private Map<String, Object> stubMap = new HashMap<String, Object>();
	
	protected Map<String, Object> getVariableMap() {
		return stubMap;
	}

	public Object eval(String source, int lineNo, int columnNo, Object script)
			throws BSFException {
		return script;
	}
}
