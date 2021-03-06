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
import java.util.List;

import junit.framework.TestCase;

import org.contract4j5.configurator.Configurator;
import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.enforcer.ContractEnforcer;
import org.contract4j5.errors.ContractError;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.instance.Instance;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.reporter.CompositeReporter;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.Severity;
import org.contract4j5.reporter.WriterReporter;
import org.contract4j5.util.SystemUtils;

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
	
	Contract4J            c4j;
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
		c4j = Contract4J.getInstance();
		c4j.setReporter(reporter);
		contractEnforcer = c4j.getContractEnforcer();
		interpreter = contractEnforcer.getExpressionInterpreter();
		dummy = new Dummy("Dummy");
	}

	public void testIfNoExpressionInterpreterSetThenOneIsCreated() {
		try {
			contractEnforcer.setExpressionInterpreter(null);
			Instance dum = new Instance("dummy", Dummy.class, dummy);
			contractEnforcer.invokeTest(
					"Invar",	
					"this.name invar. test", 
					new TestContextImpl("1 +1 == 2", "", dum, null, null, null, "", 0));
//			WriterReporter wr = (WriterReporter) (reporter.getReporters().get(0));
//			String str = wr.getWriter(Severity.ERROR).toString();
			assertNotNull (contractEnforcer.getExpressionInterpreter());
		} catch (ContractError ce) {
			fail(ce.toString());
		}
	}

	public void testNoThisDefinedIsAnErrorIfThisUsedInTest() {
		try {
			contractEnforcer.invokeTest(
					"Invar",	// invalid test 
					"this.name invar. test", 
					new TestContextImpl("$this.name != null",	"name", null, null, null, null, "", 0));
			fail();
		} catch (TestSpecificationError tse) {
			assertMessage (Severity.ERROR, ExpressionInterpreter.InvalidTestExpression.THIS_KEYWORD_WITH_NO_INSTANCE);
		}
	}

	public void testNoTargetDefinedIsAnErrorIfThisUsedInTest() {
		try {
			contractEnforcer.invokeTest(
					"Invar",	// invalid test 
					"this.name invar. test", 
					new TestContextImpl("$target.name != null",	"", null, null, null, null, "", 0));
			fail();
		} catch (TestSpecificationError tse) {
			assertMessage (Severity.ERROR, ExpressionInterpreter.InvalidTestExpression.TARGET_KEYWORD_WITH_NO_TARGET);
		}
	}

	public void testNoReturnDefinedIsAnErrorIfThisUsedInTest() {
		try {
			contractEnforcer.invokeTest(
					"Invar",	// invalid test 
					"this.name invar. test", 
					new TestContextImpl("$return != null", "", null, null, null, null, "", 0));
			fail();
		} catch (TestSpecificationError tse) {
			assertMessage (Severity.ERROR, ExpressionInterpreter.InvalidTestExpression.RETURN_KEYWORD_WITH_NO_RETURN);
		}
	}

	public void testNoArgsDefinedIsAnErrorIfThisUsedInTest() {
		try {
			contractEnforcer.invokeTest(
					"Invar",	// invalid test 
					"this.name invar. test", 
					new TestContextImpl("$args[0] != null", "", null, null, null, null, "", 0));
			fail();
		} catch (TestSpecificationError tse) {
			assertMessage (Severity.ERROR, ExpressionInterpreter.InvalidTestExpression.ARGS_KEYWORD_WITH_NO_ARGS);
		}
	}

	public void testResultKeywordIsUndefinedCausingAnErrorExceptForJRuby() {
		if (SystemUtils.isJRuby()) return;
		doTestUnknownKeywordIsAnError("$result");
	}

	public void testUnknownKeywordIsAnErrorExceptForJRuby() {
		if (SystemUtils.isJRuby()) return;
		doTestUnknownKeywordIsAnError("$foo");
	}

	private void doTestUnknownKeywordIsAnError(String badKeyword) {
		try {
			contractEnforcer.invokeTest(
					"Invar", 
					"this.name invar. test", 
					new TestContextImpl(badKeyword, "", null, null, null, null, "", 0));
			fail(badKeyword);
		} catch (TestSpecificationError tse) {
			WriterReporter wr = (WriterReporter) (reporter.getReporters().get(0));
			String str = wr.getWriter(Severity.FATAL).toString();
			assertTrue (str, str.contains(ExpressionInterpreter.InvalidTestExpression.UNRECOGNIZED_KEYWORDS.toString()));
		} catch (ContractError ce) {
			fail(badKeyword);
		}
	}

	public void testInvokeTestWithValidPreTestPasses() {
		doTestInvokeTestWithValidPreTestPasses("name");
	}

	private void doTestInvokeTestWithValidPreTestPasses(String name) {
		try {
			Instance dum = new Instance ("dummy", Dummy.class,  dummy);
			Instance foo = new Instance (name,    String.class, dummy.getName());
			assertNotNull (dummy.getName());
			assertTrue    (dummy.getName().length() > 0);
			contractEnforcer.invokeTest(
					"Invar", 
					"this.name invar. test", 
					new TestContextImpl("$this." + name + " != null", "", dum, foo, null, null, "", 0));
		} catch (ContractError ce) {
			fail(ce.toString()+", name = "+dummy.getName());
		}
	}

	public void testInvokeTestWithAnEmptyTestExpressionIsAnError() {
		try {
			Instance dum = new Instance ("dummy", Dummy.class, dummy);
			contractEnforcer.invokeTest(
					"Pre", 
					"this.name invar. test", 
					new TestContextImpl("",	"", dum, null, null, null, "", 0));
			fail();
		} catch (TestSpecificationError tse) {
		} catch (ContractError ce) {
			fail();
		}
	}
	public void testInvokeTestWithAnEmptyTestExpressionIsNotAnErrorWhenEmptyTestsArePermitted() {
		contractEnforcer.getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(true);
		try {
			Instance dum = new Instance ("dummy", Dummy.class, dummy);
			contractEnforcer.invokeTest(
					"Pre", 
					"this.name invar. test", 
					new TestContextImpl("",	"", dum, null, null, null, "", 0));
		} catch (ContractError ce) {
			fail(ce.toString());
		}
	}

	public void testInvokeTestWithAValidTestThatFails() {
		try {
			Instance dum = new Instance ("dummy", Dummy.class,  dummy);
			Instance foo = new Instance ("foo",   String.class, new String("foo"));
			contractEnforcer.invokeTest(
					"Invar", 
					"this.name invar. test", new TestContextImpl("name",	"", dum, foo, null, null, null, "", 0));
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
		}
	}
	
	protected void assertMessage(Severity severity, ExpressionInterpreter.InvalidTestExpression ite) {
		WriterReporter wr = (WriterReporter) (reporter.getReporters().get(0));
		String str = wr.getWriter(Severity.ERROR).toString();
		assertTrue (str, str.contains(ite.toString()));		
	}
}
