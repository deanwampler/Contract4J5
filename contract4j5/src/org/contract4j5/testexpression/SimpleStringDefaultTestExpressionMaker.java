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
 * A default test expression maker that returns a simple string, specified for the maker instance.
 * The string defaults to "".
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class SimpleStringDefaultTestExpressionMaker extends DefaultTestExpressionMakerHelper {
	private String expression = "";
	/**
	 * @return the fixed expression string, which is never null.
	 */
	public String getExpression() {
		return expression;
	}
	/**
	 * @param expression to return for all requests for the default test expression. If null
	 * is specified, "" is used instead.
	 */
	public void setExpression(String expression) {
		this.expression = expression;
		if (this.expression == null) {
			this.expression = "";
		}
	}
	/**
	 * No default is defined; return an empty (not null) string.
	 * @see org.contract4j5.testexpression.DefaultTestExpressionMaker#makeDefaultTestExpression(org.contract4j5.context.TestContext)
	 */
	public String makeDefaultTestExpression(TestContext context) {
		return getExpression();
	}
	
	/**
	 * Use "" as the test string.
	 */
	public SimpleStringDefaultTestExpressionMaker() {}

	/**
	 * @param expression to return for all default expression requests. If null
	 * is specified, "" is used instead.
	 */
	public SimpleStringDefaultTestExpressionMaker(String expression) {
		setExpression(expression);
	}
	
}
