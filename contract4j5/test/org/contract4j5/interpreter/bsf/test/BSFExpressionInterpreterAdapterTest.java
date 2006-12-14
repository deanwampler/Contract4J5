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

package org.contract4j5.interpreter.bsf.test;


import java.util.Map;

import junit.framework.TestCase;

import org.apache.bsf.BSFException;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.interpreter.bsf.BSFExpressionInterpreterAdapter;

public class BSFExpressionInterpreterAdapterTest extends TestCase {
	BSFExpressionInterpreterAdapter interpreter;
	private StubBSFEngine bsfEngine;
	
	protected void setUp() throws Exception {
		super.setUp();
//		Configurator c = new ConfiguratorForTesting();
//		c.configure();
		StubBSFEngine.registerWithBSF();
		interpreter = new BSFExpressionInterpreterAdapter ("stubbsfengine");
		bsfEngine   = (StubBSFEngine) interpreter.getBSFEngine();
	}

	public void testStubBSFEngineAndManagerFound() {
		assertNotNull(bsfEngine);
		assertNotNull(interpreter.getBSFManager());
	}
	
	public void testUnknownBSFEngineThrowsException() {
			try {
				new BSFExpressionInterpreterAdapter ("unknownengine");
				fail();
			} catch (BSFException e) {
			}
	}
	public void testDetermineOldValue() {
		Map<String,Object> map = interpreter.determineOldValues("$old(result)", new TestContextImpl());
		assertEquals(1, map.size());
		assertEquals(bsfEngine.result, map.get(bsfEngine.result));
	}
	
	public void testInvokeTestFailsWithNonBooleanTestExpression() {
		assertFalse(interpreter.invokeTest("1+1", new TestContextImpl()).isPassed());
	}
	public void testInvokeTestPassesWithTrueBooleanTestExpression() {
		TestResult result = interpreter.invokeTest("1+1==2", new TestContextImpl());
		assertTrue(result.isPassed());
	}
	public void testInvokeTestFailsWithFalseBooleanTestExpression() {
		TestResult result = interpreter.invokeTest("1+1!=2", new TestContextImpl());
		assertFalse(result.isPassed());
	}
}
