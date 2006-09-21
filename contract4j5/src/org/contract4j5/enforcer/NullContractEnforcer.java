/*
 * Copyright 2005, 2006 Dean Wampler. All rights reserved.
 * http://www.aspectprogramming.com
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
package org.contract4j5.enforcer;

import org.contract4j5.Contract4J;
import org.contract4j5.ContractError;
import org.contract4j5.TestContext;
import org.contract4j5.Contract4J.TestType;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.NullExpressionInterpreter;

/**
 * Uses the "Null Object Pattern" to define a contract enforcer that does nothing,
 * effectively disabling all tests, although it is more transparent to users to 
 * disable the tests with the {@link Contract4J#isEnabled(TestType)} flags instead. 
 * This object is primarily useful for eliminating the need for clients to always 
 * test for null.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class NullContractEnforcer implements ContractEnforcer {

	public void invokeTest(String testExpression, String testPrefix,
			String extraMessage, TestContext context) throws ContractError {}
	public void fail(String testExpression, String testPrefix, 
			String extraMessage, TestContext context, Throwable th) throws ContractError {
		throw new ContractError(testExpression + ": " + extraMessage, th); 
	}
	
	boolean includeStackTrace = false;
	public void setIncludeStackTrace(boolean onOff) { includeStackTrace = onOff; }
	public boolean getIncludeStackTrace() { return includeStackTrace; }

	private ExpressionInterpreter expressionInterpreter = 
		new NullExpressionInterpreter();
	public void setExpressionInterpreter(
			ExpressionInterpreter expressionInterpreter) {
		this.expressionInterpreter = expressionInterpreter;
	}
	public ExpressionInterpreter getExpressionInterpreter() {
		return expressionInterpreter;
	}
}