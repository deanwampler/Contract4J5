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

package org.contract4j5.interpreter.test;

import java.util.HashMap;
import java.util.Map;

import org.contract4j5.context.TestContext;
import org.contract4j5.interpreter.ExpressionInterpreterHelper;
import org.contract4j5.interpreter.TestResult;

/**
 * "Stub" for testing. Among other limitations, it can only handle "$old($this)" and "$old($target)"
 * expressions, but not "$old($this.foo)", "$old($this.getFoo())", etc.
 */
public class ExpressionInterpreterStub extends ExpressionInterpreterHelper {

	public ExpressionInterpreterStub() {
		super();
	}

	@Override
	protected Object doDetermineOldValue(String exprStr, TestContext context) {
		exprStr = exprStr.trim();
		Object o = cmap.get(exprStr);
		if (o != null) {
			return o;
		}
		if (exprStr.equals("c4jOldThis")) {
			return context.getInstance();
		}
		if (exprStr.equals("c4jOldTarget")) {
			return context.getField();
		}
		return null;
	}
	
	@Override
	protected TestResult doTest(
			String testExpression, 
			TestContext context) {
		String     itemName = context.getItemName();
//		OldNewPair instance = context.getInstanceOldNewPair();
//		OldNewPair target   = context.getTargetOldNewPair();
//		Object[]   args     = context.getMethodArgs();
//		Object     result   = context.getMethodResult();
		if (testExpression == null || testExpression.length() == 0 ||
				itemName == null || itemName.length() == 0) {
			return new TestResult (false, "Test expression or item name empty.");
		}
		// May have "<null>." prepended to the textExpression
		if (testExpression.equals(itemName) || testExpression.equals("<null>."+itemName)) {
			return new TestResult (true);
		}
		return new TestResult (false, "textExpression \""+testExpression+"\" != itemName \""+itemName+"\".");
	}

	Map<String, Object> cmap = new HashMap<String, Object>();
	@Override
	protected void doRecordContextChange(String newSymbolName, Object newObject) {
		cmap.put(newSymbolName, newObject);
	}

	@Override
	protected void doRemoveContextChange(String oldSymbolName) {
		cmap.remove(oldSymbolName);
	}
}
