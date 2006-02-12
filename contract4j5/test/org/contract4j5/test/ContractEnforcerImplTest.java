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

package org.contract4j5.test;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.contract4j5.ContractEnforcer;
import org.contract4j5.ContractEnforcerImpl;
import org.contract4j5.ContractError;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.jexl.JexlExpressionInterpreter;
import org.contract4j5.util.reporter.Reporter;
import org.contract4j5.util.reporter.Severity;
import org.contract4j5.util.reporter.WriterReporter;

/**
 * Test ContractEnforcerImpl, except for test invocation, which is tested in a separate
 * TestCase.
 */
public class ContractEnforcerImplTest extends TestCase {
	ContractEnforcer contractEnforcer;

	ExpressionInterpreter interpreter;
	Reporter              reporter;
	protected void setUp() throws Exception {
		super.setUp();
		interpreter = new JexlExpressionInterpreter();
		contractEnforcer = new ContractEnforcerImpl(interpreter, true);
		reporter = new WriterReporter(Severity.WARN, new StringWriter(1024));
		ManualSetup.wireC4J(interpreter, contractEnforcer, reporter);
	}

	public void testConstructor1() {
		assertTrue(contractEnforcer.getIncludeStackTrace());
		assertSame(interpreter, contractEnforcer.getExpressionInterpreter());
		ContractEnforcer contractEnforcer2 = 
			new ContractEnforcerImpl(interpreter, false);
		assertFalse(contractEnforcer2.getIncludeStackTrace());
	}

	public void testConstructor2() {
		ContractEnforcer contractEnforcer2 = new ContractEnforcerImpl();
		assertFalse(contractEnforcer2.getIncludeStackTrace());
		assertNull(contractEnforcer2.getExpressionInterpreter());
	}

	public void testSetGetExpressionInterpreter() {
		ExpressionInterpreter ei = new JexlExpressionInterpreter();
		contractEnforcer.setExpressionInterpreter(ei);
		assertSame(ei, contractEnforcer.getExpressionInterpreter());
	}

	public void testSetGetIncludeStackTrace1() {
		assertTrue(contractEnforcer.getIncludeStackTrace());
		try {
			contractEnforcer.handleFailure("Stack Trace Included Test",	new Throwable());
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
			contractEnforcer.handleFailure("Stack Trace Not Included Test",	new Throwable());
			fail();
		} catch (ContractError ce) {
			// Empty: expected
		}
		WriterReporter wr = (WriterReporter) reporter;
		String str = wr.getWriter(Severity.FATAL).toString();
		assertFalse(str.contains("Stack Trace:"));
	}

	public void testHandleFailureNoArgs() {
		try {
			contractEnforcer.handleFailure();
			fail();
		} catch (ContractError ce) {
			// Empty: expected
		}
	}

	public void testHandleFailureString() {
		try {
			contractEnforcer.handleFailure("A message");
			fail();
		} catch (ContractError ce) {
			// Empty: expected
		}
	}

	public void testHandleFailureStringThrowable() {
		try {
			contractEnforcer.handleFailure("A message", new Throwable());
			fail();
		} catch (ContractError ce) {
			// Empty: expected
		}
	}
}
