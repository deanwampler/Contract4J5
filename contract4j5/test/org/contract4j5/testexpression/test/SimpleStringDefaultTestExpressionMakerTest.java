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
import org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker;

public class SimpleStringDefaultTestExpressionMakerTest extends TestCase {
	public static class Foo {
		private String name;
		public String getName() { return name; }
		public void   setName(String n) { name = n; }
		
		public String doIt(int i) {
			return i == 0 ? null : Integer.toString(i);
		}
		public Foo (String name) { setName(name); }
	}
	
	SimpleStringDefaultTestExpressionMaker maker = null;
	TestContext context = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		maker = new SimpleStringDefaultTestExpressionMaker("default");
		context = TestContextImpl.emptyTestContext;
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker.makeDefaultTestExpression(TestContext)'
	 */
	public void testMakeDefaultTestExpression() {
		assertEquals ("default", maker.makeDefaultTestExpression(context));
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker.getExpression()'
	 */
	public void testGetExpression() {
		assertEquals ("default", maker.getExpression());
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker.setExpression(String)'
	 */
	public void testSetExpression() {
		maker.setExpression("foo");
		assertEquals ("foo", maker.getExpression());
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker.SimpleStringDefaultTestExpressionMaker()'
	 */
	public void testSimpleStringDefaultTestExpressionMaker() {
		maker = new SimpleStringDefaultTestExpressionMaker();
		assertEquals ("", maker.getExpression());
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker.SimpleStringDefaultTestExpressionMaker(String)'
	 */
	public void testSimpleStringDefaultTestExpressionMakerString() {
		assertEquals ("default", maker.getExpression());
	}
}
