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

import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.bsf.BSFException;
import org.contract4j5.configurator.Configurator;
import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.enforcer.ContractEnforcer;
import org.contract4j5.enforcer.defaultimpl.DefaultContractEnforcer;
import org.contract4j5.errors.ContractError;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.bsf.BSFExpressionInterpreterAdapter;
import org.contract4j5.interpreter.bsf.jexl.JexlBSFExpressionInterpreter;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.Severity;
import org.contract4j5.reporter.WriterReporter;

/**
 * Test DefaultContractEnforcerImpl, except for test invocation, which is tested in a separate
 * TestCase.
 */
public class DefaultContractEnforcerTest extends TestCase {
	Contract4J c4j;
	ContractEnforcer contractEnforcer;
	ExpressionInterpreter interpreter;
	Reporter              reporter;
	protected void setUp() throws Exception {
		super.setUp();
		reporter = new WriterReporter(Severity.WARN, new StringWriter(1024));
		Configurator c = new ConfiguratorForTesting();
		c.configure();
		c4j = Contract4J.getInstance();
		c4j.setReporter(reporter);
		contractEnforcer = c4j.getContractEnforcer();
		contractEnforcer.setIncludeStackTrace(true);
		interpreter = contractEnforcer.getExpressionInterpreter();
	}

	public void testConstructor1() {
		ContractEnforcer ce1 = new DefaultContractEnforcer(interpreter, true);
		assertTrue(ce1.getIncludeStackTrace());
		assertSame(interpreter, ce1.getExpressionInterpreter());
		ContractEnforcer ce2 = new DefaultContractEnforcer(interpreter, false);
		assertFalse(ce2.getIncludeStackTrace());
	}

	public void testConstructor2() {
		ContractEnforcer ce = new DefaultContractEnforcer();
		assertFalse(ce.getIncludeStackTrace());
		assertTrue(BSFExpressionInterpreterAdapter.class == ce.getExpressionInterpreter().getClass());
	}

	public void testSetGetExpressionInterpreter() throws BSFException {
		ExpressionInterpreter ei = new JexlBSFExpressionInterpreter();
		contractEnforcer.setExpressionInterpreter(ei);
		assertSame(ei, contractEnforcer.getExpressionInterpreter());
	}

	public void testSetGetIncludeStackTrace1() {
		assertTrue(contractEnforcer.getIncludeStackTrace());
		try {
			ContractError ce = new ContractError();
			ce.fillInStackTrace();
			contractEnforcer.fail("test", "Invar", "Stack Trace Included Test",	TestContextImpl.EmptyTestContext, ce);
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			// Empty: expected
		}
		WriterReporter wr = (WriterReporter) reporter;
		String str = wr.getWriter(Severity.FATAL).toString();
		assertTrue(str, str.contains("Stack Trace:"));
	}

	public void testSetGetIncludeStackTrace2() {
		contractEnforcer.setIncludeStackTrace(false);
		assertFalse(contractEnforcer.getIncludeStackTrace());
		try {
			contractEnforcer.fail("test", "Invar", "Stack Trace Not Included Test",	TestContextImpl.EmptyTestContext, new Throwable());
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			// Empty: expected
		}
		WriterReporter wr = (WriterReporter) reporter;
		String str = wr.getWriter(Severity.FATAL).toString();
		assertFalse(str.contains("Stack Trace:"));
	}

	public void testHandleFailureStringThrowable() {
		try {
			contractEnforcer.fail("test", "Invar", "A message",	TestContextImpl.EmptyTestContext, new Throwable());
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			// Empty: expected
		}
	}
}
