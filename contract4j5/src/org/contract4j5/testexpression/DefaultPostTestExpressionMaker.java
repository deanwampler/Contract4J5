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
 * Make default test expressions for postcondition tests for methods that don't return void.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class DefaultPostTestExpressionMaker extends DefaultTestExpressionMakerHelper {
	/**
	 * Require the return value, if not a primitive, to be non-null.
	 * @see org.contract4j5.testexpression.DefaultTestExpressionMaker#makeDefaultTestExpression(org.contract4j5.context.TestContext)
	 */
	public String makeDefaultTestExpression(TestContext context) {
		Object r = context.getMethodResult();
		Class<?> clazz = r != null ? r.getClass() : null;
		return isNotPrimitive(clazz) ?
				"$return != null" : "";
	}
}
