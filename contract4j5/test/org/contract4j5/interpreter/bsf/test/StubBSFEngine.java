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
package org.contract4j5.interpreter.bsf.test;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.bsf.BSFDeclaredBean;
import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.CodeBuffer;

public class StubBSFEngine implements BSFEngine {
	public String result = "result";
	
	public static void registerWithBSF() {
		BSFManager.registerScriptingEngine(
				"stubbsfengine", 
				StubBSFEngine.class.getName(),
				new String[] { "stubbsfengine", "sbe" }
			);
	}
	
	public Object apply(String arg0, int arg1, int arg2, Object arg3,
			Vector arg4, Vector arg5) throws BSFException {
		return result;
	}

	public Object call(Object arg0, String arg1, Object[] arg2)
			throws BSFException {
		return result;
	}

	public void compileApply(String arg0, int arg1, int arg2, Object arg3,
			Vector arg4, Vector arg5, CodeBuffer arg6) throws BSFException {
	}

	public void compileExpr(String arg0, int arg1, int arg2, Object arg3,
			CodeBuffer arg4) throws BSFException {
	}

	public void compileScript(String arg0, int arg1, int arg2, Object arg3,
			CodeBuffer arg4) throws BSFException {
	}

	public Map<String, Object> beanMap = new HashMap<String, Object>();
	public void declareBean(BSFDeclaredBean arg0) throws BSFException {
		beanMap.put(arg0.name, arg0.bean);
	}
	public void undeclareBean(BSFDeclaredBean arg0) throws BSFException {
		beanMap.remove(arg0.name);
	}

	public Object eval(String arg0, int arg1, int arg2, Object arg3)
			throws BSFException {
		String str = (String) arg3;
		if (str.contains("=="))
			return true;
		if (str.contains("!="))
			return false;
		return result;
	}

	public void exec(String arg0, int arg1, int arg2, Object arg3)
			throws BSFException {
	}

	public void iexec(String arg0, int arg1, int arg2, Object arg3)
			throws BSFException {
	}

	public void initialize(BSFManager arg0, String arg1, Vector arg2)
			throws BSFException {
	}

	public void terminate() {
	}

	public void propertyChange(PropertyChangeEvent evt) {
	}		
}
