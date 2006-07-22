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
import java.util.List;

import junit.framework.TestCase;

import org.contract4j5.ContractEnforcer;
import org.contract4j5.ContractError;
import org.contract4j5.Instance;
import org.contract4j5.TestContextImpl;
import org.contract4j5.aspects.Contract4J;
import org.contract4j5.configurator.Configurator;
import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.util.reporter.CompositeReporter;
import org.contract4j5.util.reporter.Reporter;
import org.contract4j5.util.reporter.Severity;
import org.contract4j5.util.reporter.WriterReporter;

/**
 * Test ContractEnforcerImpl.invokeTest(). Other tests for this class are handled by
 * a separate TestCase.
 */
public class ContractEnforcerImpl_invokeTest extends TestCase {
	public static class Dummy {
		private String name;
		public String getName() { return this.name; }
		public void   setName(String name) { this.name = name; }
		public Dummy (String name) { setName(name); }
	}
	
	ContractEnforcer      contractEnforcer;
	ExpressionInterpreter interpreter;
	CompositeReporter     reporter;
	Dummy                 dummy;
	
	protected void setUp() throws Exception {
		super.setUp();
		Reporter reporter1 = new WriterReporter(Severity.DEBUG, new StringWriter(1024));
		Reporter reporter2 = new WriterReporter(Severity.DEBUG);  // stdout, stderr
		reporter           = new CompositeReporter(Severity.DEBUG);
		List<Reporter> rlist = reporter.getReporters();
		rlist.add(0, reporter1);
		rlist.add(1, reporter2);
		Configurator c = new ConfiguratorForTesting();
		c.configure();
		c.setReporter(reporter);
		contractEnforcer = Contract4J.getContractEnforcer();
		interpreter = contractEnforcer.getExpressionInterpreter();
		dummy = new Dummy("Dummy");
	}

	public void testNoExpressionInterpreter() {
		try {
			contractEnforcer.setExpressionInterpreter(null);
			Instance dum = new Instance("name", Dummy.class, dummy);
			contractEnforcer.invokeTest(
					"foo == bar",	// bogus test 
					"Invar", 
					"this.name invar. test", 
					new TestContextImpl("name", dum, null, null, null));
			WriterReporter wr = (WriterReporter) (reporter.getReporters().get(0));
			String str = wr.getWriter(Severity.ERROR).toString();
			assertTrue (str, str.contains("No ExpressionInterpreter is defined for the ContractEnforcer!"));
			wr.setWriters(new StringWriter());  // toss the old string
			contractEnforcer.invokeTest(		// call test again
					"foo == bar",	// bogus test 
					"Invar", 
					"this.name invar. test", 
					new TestContextImpl("name", dum, null, null, null));
			String str2 = wr.getWriter(Severity.ERROR).toString();
			// Confirm that new string doesn't have the error message, because an aspect only
			// allows it to be written once!
			assertFalse (str2.contains("No ExpressionInterpreter is defined for the ContractEnforcer!"));
		} catch (ContractError ce) {
			fail();
		}
	}

	public void testBuggyTestIsAnErrorNoThisKeyword() {
		try {
			contractEnforcer.invokeTest(
					"$this == bar",	// invalid test 
					"Invar", 
					"this.name invar. test", 
					new TestContextImpl("name", null, null, null, null, null));
			assertMessage (Severity.WARN, ExpressionInterpreter.InvalidTestExpression.THIS_KEYWORD_WITH_NO_INSTANCE);
		} catch (ContractError ce) {
			fail(ce.toString());
		}
	}

	public void testBuggyTestIsAnErrorNoTargetKeyword() {
		try {
			contractEnforcer.invokeTest(
					"$target == bar",	// invalid test 
					"Invar", 
					"this.name invar. test", 
					new TestContextImpl("name", null, null, null, null, null));
			assertMessage (Severity.WARN, ExpressionInterpreter.InvalidTestExpression.TARGET_KEYWORD_WITH_NO_TARGET);
		} catch (ContractError ce) {
			fail(ce.toString());
		}
	}

	public void testBuggyTestIsAnErrorNoReturnKeyword() {
		try {
			contractEnforcer.invokeTest(
					"$return == bar",	// invalid test 
					"Invar", 
					"this.name invar. test", 
					new TestContextImpl("name", null, null, null, null, null));
			assertMessage (Severity.WARN, ExpressionInterpreter.InvalidTestExpression.RETURN_KEYWORD_WITH_NO_RETURN);
		} catch (ContractError ce) {
			fail(ce.toString());
		}
	}

	public void testBuggyTestIsAnErrorNoArgsKeyword() {
		try {
			contractEnforcer.invokeTest(
					"$args[0] == bar",	// invalid test 
					"Invar", 
					"this.name invar. test", 
					new TestContextImpl("name", null, null, null, null, null));
			assertMessage (Severity.WARN, ExpressionInterpreter.InvalidTestExpression.ARGS_KEYWORD_WITH_NO_ARGS);
		} catch (ContractError ce) {
			fail(ce.toString());
		}
	}

	public void testBuggyTestIsAnError5() {
		try {
			contractEnforcer.invokeTest(
					"$result == bar",	// invalid test 
					"Invar", 
					"this.name invar. test", 
					new TestContextImpl("name", null, null, null, null, null));
			fail();
		} catch (ContractError ce) {
			WriterReporter wr = (WriterReporter) (reporter.getReporters().get(0));
			String str = wr.getWriter(Severity.FATAL).toString();
			assertTrue (str, str.contains(ExpressionInterpreter.InvalidTestExpression.UNRECOGNIZED_KEYWORDS.toString()));
		}
	}

	public void testBuggyTestIsAnError6() {
		try {
			contractEnforcer.invokeTest(
					"$foo == bar",	// invalid test 
					"Invar", 
					"this.name invar. test", 
					new TestContextImpl("name", null, null, null, null, null));
			fail();
		} catch (ContractError ce) {
			WriterReporter wr = (WriterReporter) (reporter.getReporters().get(0));
			String str = wr.getWriter(Severity.FATAL).toString();
			assertTrue (str, str.contains(ExpressionInterpreter.InvalidTestExpression.UNRECOGNIZED_KEYWORDS.toString()));
		}
	}

	public void testInvokeTest_PrePass() {
		try {
			Instance dum = new Instance ("dummy", Dummy.class,  dummy);
			Instance foo = new Instance ("name",  String.class, dummy.getName());
			assertNotNull (dummy.getName());
			assertTrue    (dummy.getName().length() > 0);
			contractEnforcer.invokeTest(
					"$target != null && $target.length() > 0", 
					"Pre", 
					"this.name invar. test", 
					new TestContextImpl("name", dum, foo, null, null));
		} catch (ContractError ce) {
			fail(ce.toString()+", name = "+dummy.getName());
		}
	}

	public void testInvokeTest_EmptyTest1() {
		try {
			Instance dum = new Instance ("dummy", Dummy.class, dummy);
			contractEnforcer.invokeTest(
					null, 
					"Pre", 
					"this.name invar. test", 
					new TestContextImpl("name",	dum, null, null, null));
			fail();
		} catch (ContractError ce) {
		}
	}
	public void testInvokeTest_EmptyTest2() {
		contractEnforcer.getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(true);
		try {
			Instance dum = new Instance ("dummy", Dummy.class, dummy);
			contractEnforcer.invokeTest(
					null, 
					"Pre", 
					"this.name invar. test", 
					new TestContextImpl("name",	dum, null, null, null));
		} catch (ContractError ce) {
			fail(ce.toString());
		}
	}

	public void testInvokeTest_InvarPass() {
		try {
			Instance dum = new Instance ("dummy", Dummy.class,  dummy);
			Instance foo = new Instance ("foo",   String.class, dummy.getName());
			assertNotNull (dummy.getName());
			assertTrue    (dummy.getName().length() > 0);
			contractEnforcer.invokeTest(
					"$target != null && $target.length() > 0", 
					"Invar", 
					"this.name invar. test", 
					new TestContextImpl("name",	dum, foo, null, null));
		} catch (ContractError ce) {
			fail(ce.toString()+", name = "+dummy.getName());
		}
	}

	public void testInvokeTest_InvarFail() {
		try {
			Instance dum = new Instance ("dummy", Dummy.class,  dummy);
			Instance foo = new Instance ("foo",   String.class, new String("foo"));
			contractEnforcer.invokeTest(
					"$target.length() == 0", 
					"Invar", "this.name invar. test", 
					new TestContextImpl("name",	dum, foo, null, null, null));
			fail();
		} catch (ContractError ce) {
			// EMPTY - expected.
		}
	}
	
	protected void assertMessage(Severity severity, ExpressionInterpreter.InvalidTestExpression ite) {
		WriterReporter wr = (WriterReporter) (reporter.getReporters().get(0));
		String str = wr.getWriter(Severity.ERROR).toString();
		assertTrue (str, str.contains(ite.toString()));		
	}
}
