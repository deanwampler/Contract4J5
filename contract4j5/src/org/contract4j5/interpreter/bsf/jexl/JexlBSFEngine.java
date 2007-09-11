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
package org.contract4j5.interpreter.bsf.jexl;

import java.util.Map;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;
import org.contract4j5.interpreter.bsf.BSFEngineAdapter;

public class JexlBSFEngine extends BSFEngineAdapter {

	public static void registerWithBSF() {
		BSFManager.registerScriptingEngine(
				"jexl", 
				JexlBSFEngine.class.getName(),
				new String[] { "jexl", "jl" }
			);
	}
	
	private JexlContext jexlContext = null;

	@SuppressWarnings("unchecked")
	protected Map<String, Object> getVariableMap() {
		if (jexlContext == null)
			jexlContext = JexlHelper.createContext();
		return (Map<String, Object>) jexlContext.getVars();
	}

	public Object eval(String source, int lineNo, int columnNo, Object script)
			throws BSFException {
	   try {
		   return ExpressionFactory.createExpression ((String) script).evaluate(jexlContext);
	   } catch (Exception e) {
		   throw new BSFException(-1, e.toString(), e);
	   }
	}
}
