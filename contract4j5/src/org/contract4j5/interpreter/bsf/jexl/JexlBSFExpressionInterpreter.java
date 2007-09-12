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
import org.contract4j5.interpreter.bsf.BSFExpressionInterpreterAdapter;

/**
 * A Jexl subclass of {@link BSFExpressionInterpreterAdapter} is useful since we
 * need to register the engine with the bean scripting framework, which we do with
 * a static initializer. This isn't necessary for other languages, such as Groovy. 
 */
public class JexlBSFExpressionInterpreter extends BSFExpressionInterpreterAdapter {

	static {
		JexlBSFEngine.registerWithBSF();
	}
	
	public JexlBSFExpressionInterpreter() throws BSFException {
		super("jexl");
	}
	
	public JexlBSFExpressionInterpreter(
			boolean treatEmptyTestExpressionAsValid) throws BSFException {
		super("jexl", treatEmptyTestExpressionAsValid);
	}
	
	public JexlBSFExpressionInterpreter(
			boolean treatEmptyTestExpressionAsValid, 
			Map<String, String> optionalKeywordSubstitutions) throws BSFException {
		super("jexl", treatEmptyTestExpressionAsValid, optionalKeywordSubstitutions);
	}
	
}
