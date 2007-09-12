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
package org.contract4j5.interpreter.bsf.groovy;

import java.util.Map;

import org.apache.bsf.BSFException;
import org.contract4j5.interpreter.bsf.BSFExpressionInterpreterAdapter;

/**
 * A convenience subclass of {@link BSFExpressionInterpreterAdapter} that specifies
 * Groovy as the scripting language. This class isn't strictly necessary, as you 
 * could just instantiate the parent class with "groovy" as the first argument to the
 * constructor, but this wrapper is very convenient for Spring-based configurations and 
 * Property file configurations, etc. 
 * @note Unlike the Jexl and JRuby wrappers, we don't need to register the engine with BSF.
 */
public class GroovyBSFExpressionInterpreter extends BSFExpressionInterpreterAdapter {

	public GroovyBSFExpressionInterpreter() throws BSFException {
		super("groovy");
	}
	
	public GroovyBSFExpressionInterpreter(
			boolean treatEmptyTestExpressionAsValid) throws BSFException {
		super("groovy", treatEmptyTestExpressionAsValid);
	}
	
	public GroovyBSFExpressionInterpreter(
			boolean treatEmptyTestExpressionAsValid, 
			Map<String, String> optionalKeywordSubstitutions) throws BSFException {
		super("groovy", treatEmptyTestExpressionAsValid, optionalKeywordSubstitutions);
	}
	
}
