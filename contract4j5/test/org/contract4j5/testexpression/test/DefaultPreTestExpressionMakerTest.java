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

import org.contract4j5.Instance;
import org.contract4j5.TestContext;
import org.contract4j5.TestContextImpl;
import org.contract4j5.testexpression.DefaultPreTestExpressionMaker;

public class DefaultPreTestExpressionMakerTest extends TestCase {
	DefaultPreTestExpressionMaker maker = null;
	TestContext context = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		maker = new DefaultPreTestExpressionMaker();
		context = new TestContextImpl();
		Instance[] args = new Instance[] {
				new Instance ("arg0", String.class,  new String("arg0")), 
				new Instance ("one",  Integer.class, new Integer(1)), 
				new Instance ("twof", Float.class,   new Float(2f)) 
			}; 
		context.setMethodArgs(args);
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.DefaultPreTestExpressionMaker.makeDefaultTestExpression(TestContext)'
	 */
	public void testMakeDefaultTestExpression() {
		assertEquals ("$args[0] != null && $args[1] != null && $args[2] != null", 
				maker.makeDefaultTestExpression(context));
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.DefaultPreTestExpressionMaker.makeDefaultTestExpressionIfEmpty(String, TestContext)'
	 */
	public void testMakeDefaultTestExpressionIfEmpty() {
		assertEquals ("foo", maker.makeDefaultTestExpressionIfEmpty("foo", context));
	}

}
