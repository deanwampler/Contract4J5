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

package org.contract4j5.testexpression.test;

import junit.framework.TestCase;

import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.instance.Instance;
import org.contract4j5.testexpression.DefaultPostTestExpressionMaker;

public class DefaultPostTestExpressionMakerTest extends TestCase {
	DefaultPostTestExpressionMaker maker = null;
	TestContext context = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		maker = new DefaultPostTestExpressionMaker();
		context = TestContextImpl.emptyTestContext;
	}

	/*
	 * Test method for 'org.contract4j5.testExpression.DefaultPostTestExpressionMaker.makeDefaultTestExpression(TestContext)'
	 */
	public void testMakeDefaultTestExpression() {
		assertEquals ("$return != null", maker.makeDefaultTestExpression(context));
		context.setMethodResult(new Instance("1", Integer.class, 1));
		// Still thinks it's not a primitive!
		assertEquals ("$return != null", maker.makeDefaultTestExpression(context));
		context.setMethodResult(new Instance("foo", String.class, new String ("foo")));
		assertEquals ("$return != null", maker.makeDefaultTestExpression(context));
	}

	/*
	 * Test method for 'org.contract4j5.testExpression.DefaultPostTestExpressionMaker.makeDefaultTestExpressionIfEmpty(String, TestContext)'
	 */
	public void testMakeDefaultTestExpressionIfEmpty() {
		assertEquals ("foo", maker.makeDefaultTestExpressionIfEmpty("foo", context));
		context.setMethodResult(new Instance("1", Integer.class, 1));
		assertEquals ("foo", maker.makeDefaultTestExpressionIfEmpty("foo", context));
		context.setMethodResult(new Instance("foo", String.class, new String ("foo")));
		assertEquals ("foo", maker.makeDefaultTestExpressionIfEmpty("foo", context));
	}

}
