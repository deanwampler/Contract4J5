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

package org.contract4j5.enforcer.test;

import junit.framework.TestCase;

import org.contract4j5.configurator.Configurator;
import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.enforcer.ContractEnforcer;
import org.contract4j5.enforcer.ContractEnforcerHelper;
import org.contract4j5.errors.ContractError;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.TestResult;

/**
 * Test adding a custom subclass of ContractEnforcerHelper, except for test invocation, which is tested in a separate
 * TestCase.
 */
public class NewContractEnforcerHelperSubclassTest extends TestCase {
	static class SpecialContractEnforcer extends ContractEnforcerHelper {
		static class MyError extends ContractError {
			private static final long serialVersionUID = 1L;
		}
		protected void finishFailureHandling(TestResult testResult, String msg)
				throws ContractError {
			throw new MyError();
		}
		public SpecialContractEnforcer(ExpressionInterpreter expressionInterpreter) {
			super(expressionInterpreter, true);
		}
		
	}
	Contract4J c4j;
	ContractEnforcer contractEnforcer;

	protected void setUp() throws Exception {
		super.setUp();
		Configurator c = new ConfiguratorForTesting();
		c.configure();
		c4j = Contract4J.getInstance();
		contractEnforcer = new SpecialContractEnforcer(c4j.getContractEnforcer().getExpressionInterpreter());
		c4j.setContractEnforcer(contractEnforcer);
	}

	public void testSpecialContractEnforcerThrowsCustomizedExceptionOnFailure() {
		try {
			contractEnforcer.fail("", "pre", "", TestContextImpl.EmptyTestContext, null);
			fail();
		} catch (SpecialContractEnforcer.MyError me) {
		}
	}

}
