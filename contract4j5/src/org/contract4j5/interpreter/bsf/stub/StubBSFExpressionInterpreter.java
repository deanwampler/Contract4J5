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

import java.util.Map;

import org.apache.bsf.BSFException;
import org.contract4j5.interpreter.bsf.BSFExpressionInterpreterAdapter;

/**
 * A "stub" subclass of {@link BSFExpressionInterpreterAdapter} is useful for
 * testing situations. To really stub out behavior, don't build with Contract4J
 * at all, to eliminate all overhead. 
 */
public class StubBSFExpressionInterpreter extends BSFExpressionInterpreterAdapter {

	static {
		StubBSFEngine.registerWithBSF();
	}
	
	public StubBSFExpressionInterpreter() throws BSFException {
		super("stub");
	}
	
	public StubBSFExpressionInterpreter(
			boolean treatEmptyTestExpressionAsValid) throws BSFException {
		super("stub", treatEmptyTestExpressionAsValid);
	}
	
	public StubBSFExpressionInterpreter(
			boolean treatEmptyTestExpressionAsValid, 
			Map<String, String> optionalKeywordSubstitutions) throws BSFException {
		super("stub", treatEmptyTestExpressionAsValid, optionalKeywordSubstitutions);
	}
	
}
