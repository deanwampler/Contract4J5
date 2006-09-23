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
import org.contract4j5.instance.Instance;

/**
 * Make default test expressions for field invariant tests.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class DefaultFieldInvarTestExpressionMaker extends DefaultTestExpressionMakerHelper {
	/**
	 * Require the "target", which represents the field value, to be non-null.
	 * @see org.contract4j5.testexpression.DefaultTestExpressionMaker#makeDefaultTestExpression(org.contract4j5.context.TestContext)
	 */
	public String makeDefaultTestExpression(TestContext context) {
		Instance i = context.getField();
		Class clazz = i != null ? i.getClazz() : null;
		return isNotPrimitive(clazz) ?
				"$target != null" : "";
	}
}
