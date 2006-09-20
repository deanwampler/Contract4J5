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

import junit.framework.TestCase;

import org.contract4j5.ContractError;
import org.contract4j5.interpreter.TestResult;

public class TestResultTest extends TestCase {
	TestResult result = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		result = new TestResult(true, "");
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.isPassed()'
	 */
	public void testIsPassed() {
		assertTrue (result.isPassed());
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.setPassed(boolean)'
	 */
	public void testSetPassed() {
		result.setPassed(false);
		assertFalse (result.isPassed());
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.getMessage()'
	 */
	public void testGetMessage() {
		assertEquals ("", result.getMessage());
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.setMessage(String)'
	 */
	public void testSetMessage() {
		result.setMessage("foo");
		assertEquals ("foo", result.getMessage());
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.getFailureCause()'
	 */
	public void testGetFailureCause() {
		assertNull (result.getFailureCause());
		TestResult r2 = new TestResult (false, "bad", new ContractError());
		assertNotNull (r2.getFailureCause());
		assertEquals  (ContractError.class, r2.getFailureCause().getClass());
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.toString()'
	 */
	public void testToString() {
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.equals(Object)'
	 */
	public void testEqualsObject() {
		TestResult r2 = new TestResult();
		assertEquals (result, r2);
		r2 = new TestResult(false, "");
		assertFalse (result.equals(r2));
		r2 = new TestResult(true, "foo");
		assertFalse (result.equals(r2));
		r2 = new TestResult(false, "foo");
		assertFalse (result.equals(r2));
		assertFalse (result.equals(null));
		assertFalse (result.equals(new String("foo)")));
	}
}
