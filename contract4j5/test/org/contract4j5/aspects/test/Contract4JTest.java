/*
 * Copyright 2005, 2006 Dean Wampler. All rights reserved.
 * http://www.contract4j.org
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
package org.contract4j5.aspects.test;

import junit.framework.TestCase;

import org.contract4j5.controller.Contract4J;

public class Contract4JTest extends TestCase {
	Contract4J c4j;
	
	protected void setUp() throws Exception {
		super.setUp();
		c4j = new Contract4J();
	}
	protected void tearDown() throws Exception {
		super.tearDown();
		// Leave test with all enabled statically.
		c4j.setEnabled(Contract4J.TestType.Pre,   true);
		c4j.setEnabled(Contract4J.TestType.Post,  true);
		c4j.setEnabled(Contract4J.TestType.Invar, true);
	}
	
	public void testEnableAll() {
		doSetCheckOldStyle(true, true, true);
		doSetCheckNewStyle(true, true, true);
	}
	
	public void testDisableAll() {
		doSetCheckOldStyle(false, false, false);
		doSetCheckNewStyle(false, false, false);
	}
	
	public void testEnablePre() {
		doSetCheckOldStyle(true, false, false);
		doSetCheckNewStyle(true, false, false);
	}
	
	public void testEnablePost() {
		doSetCheckOldStyle(false, true, false);
		doSetCheckNewStyle(false, true, false);
	}
	
	public void testEnableInvar() {
		doSetCheckOldStyle(false, false, true);
		doSetCheckNewStyle(false, false, true);
	}
	
	private void doSetCheckOldStyle(boolean pre, boolean post, boolean invar) {
		c4j.setEnabled(Contract4J.TestType.Pre,   pre);
		c4j.setEnabled(Contract4J.TestType.Post,  post);
		c4j.setEnabled(Contract4J.TestType.Invar, invar);
		assertEquals(pre, c4j.isEnabled(Contract4J.TestType.Pre));
		assertEquals(post, c4j.isEnabled(Contract4J.TestType.Post));
		assertEquals(invar, c4j.isEnabled(Contract4J.TestType.Invar));
	}
	private void doSetCheckNewStyle(boolean pre, boolean post, boolean invar) {
		c4j.setPreTestsEnabled(pre);
		c4j.setPostTestsEnabled(post);
		c4j.setInvarTestsEnabled(invar);
		assertEquals(pre, c4j.isPreTestsEnabled());
		assertEquals(post, c4j.isPostTestsEnabled());
		assertEquals(invar, c4j.isInvarTestsEnabled());
	}
}
