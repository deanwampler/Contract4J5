/*
 * Copyright 2005 2006 Dean Wampler. All rights reserved.
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

package org.contract4j5.interpreter;

import java.util.HashMap;
import java.util.Map;

import org.contract4j5.context.TestContext;

/**
 * Uses the "Null Object Pattern" to define an interpreter that does nothing. It
 * effectively disables all tests because {@link #invokeTest(String, TestContext)}
 * always succeeds. This object is primarily useful for eliminating the need for 
 * clients to always test for null.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class NullExpressionInterpreter implements ExpressionInterpreter {
	
	public boolean getTreatEmptyTestExpressionAsValidTest() { return false; }
	public void setTreatEmptyTestExpressionAsValidTest(boolean emptyOK) {}

	public Map<String, String> getOptionalKeywordSubstitutions() { return null; }
	public void setOptionalKeywordSubstitutions(
			Map<String, String> optionalKeywordSubstitutions) {}

	public Map<String, Object> determineOldValues(String testExpression,
			TestContext context) { 
		return new HashMap<String, Object>(); 
	}

	public TestResult validateTestExpression(String testExpression, TestContext context) {
		return new TestResult(true);
	}
	
	public TestResult invokeTest(String testExpression, TestContext context) {
		return new TestResult(true);
	}

	public void registerContextObject(String name, Object object) {}
	public void registerGlobalContextObject(String name, Object object) {}
	public void unregisterContextObject(String name) {}
	public void unregisterGlobalContextObject(String name) {}
}
