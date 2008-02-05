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
package org.contract4j5.testexpression;

import org.contract4j5.context.TestContext;

/**
 * Helper class that partically implements the expression maker interface.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
abstract public class DefaultTestExpressionMakerHelper implements
		DefaultTestExpressionMaker {

	abstract public String makeDefaultTestExpression(TestContext context);

	/* (non-Javadoc)
	 * @see org.contract4j5.testexpression.DefaultTestExpressionMaker#makeDefaultTestExpressionIfEmpty(java.lang.String, java.lang.String, org.contract4j5.TestContext)
	 */
	public String makeDefaultTestExpressionIfEmpty (
			String testExpression, 
			TestContext context) {
		if (testExpression != null && testExpression.length() > 0) {
			return testExpression;
		}
		return makeDefaultTestExpression(context);
	}
	
	/* (non-Javadoc)
	 * @see org.contract4j5.testexpression.DefaultTestExpressionMaker#makeArgsNotNullExpression(org.contract4j5.TestContext)
	 */
	public String makeArgsNotNullExpression (TestContext context) {
		StringBuffer test = new StringBuffer();
		Object[] args = context.getMethodArgs();
		if (args != null && args.length > 0) {
			boolean first = true;
			for (int i=0; i<args.length; i++) {
				Class<?> argClass = args[i] != null ? args[i].getClass() : null;
				if (isNotPrimitive (argClass)) {
					if (first) { 
						first = false; 
					} else {
						test.append(" && ");
					}
					test.append("$args["+i+"]");
					test.append(" != null");
				}
			}
		}
		return test.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.contract4j5.testexpression.DefaultTestExpressionMaker#isNotPrimitive(java.lang.Class)
	 */
	public boolean isNotPrimitive (Class<?> clazz) {
		return (clazz == null || clazz.isPrimitive() == false) ?
				true: false;
	}
}
