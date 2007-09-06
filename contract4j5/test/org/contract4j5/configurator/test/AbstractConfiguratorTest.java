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
package org.contract4j5.configurator.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import junit.framework.TestCase;

import org.contract4j5.aspects.ConstructorBoundaryConditions;
import org.contract4j5.aspects.InvariantCtorConditions;
import org.contract4j5.aspects.InvariantMethodConditions;
import org.contract4j5.aspects.InvariantTypeConditions;
import org.contract4j5.aspects.MethodBoundaryConditions;
import org.contract4j5.configurator.AbstractConfigurator;
import org.contract4j5.context.TestContext;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.enforcer.ContractEnforcer;
import org.contract4j5.errors.ContractError;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.Severity;
import org.contract4j5.reporter.WriterReporter;
import org.contract4j5.testexpression.ParentTestExpressionFinder;

public class AbstractConfiguratorTest extends TestCase {
	public static class StubExpressionInterpreter implements ExpressionInterpreter {
		public Map<String, Object> determineOldValues(String testExpression, TestContext context) { return null; }
		public Map<String, String> getOptionalKeywordSubstitutions() { return null;	}
		public boolean getTreatEmptyTestExpressionAsValidTest() { return false;	}
		public TestResult invokeTest(String testExpression, TestContext context) { return null; }
		public void setOptionalKeywordSubstitutions(Map<String, String> optionalKeywordSubstitutions) {}
		public void setTreatEmptyTestExpressionAsValidTest(boolean emptyOK) {}
		public TestResult validateTestExpression(String testExpression, TestContext context) { return null;	}
	}
	
	public static class StubContractEnforcer implements ContractEnforcer {
		private ExpressionInterpreter expressionInterpreter = new StubExpressionInterpreter();
		public ExpressionInterpreter getExpressionInterpreter() { return expressionInterpreter;	}
		public void setExpressionInterpreter(ExpressionInterpreter expressionInterpreter) {}
		public void invokeTest(String testExpression, String testPrefix, String extraMessage, TestContext context)
			throws ContractError {}
		public void fail(String testExpression, String testPrefix, String extraMessage, TestContext context, Throwable th)
			throws ContractError { throw new ContractError(testExpression + ": " + extraMessage, th); }
		public boolean  getIncludeStackTrace() {	return false; }
		public void     setIncludeStackTrace(boolean onOff) {}
		public Severity getErrorReportingSeverityLevel() { return Severity.FATAL; }
		public void     setErrorReportingSeverityLevel(Severity severity) {}
		public boolean  getReportErrors() { return true; }
		public void     setReportErrors(boolean onOff) {}
		public void handleFailure(String testExpression, String testPrefix,
				String extraMessage, TestContext context, TestResult testResult)
				throws ContractError {
			throw new ContractError(testExpression);
		}
	}

	public static class StubTestExpressionFinder implements ParentTestExpressionFinder {
		private Contract4J c4j;
		public TestResult findParentAdviceTestExpression(Annotation whichAnnotationType, Method advice, TestContext context) { return null;	}
		public TestResult findParentAdviceTestExpressionIfEmpty(String testExpression, Annotation whichAnnotationType, Method advice, TestContext context) { return null; }
		public TestResult findParentConstructorTestExpression(Annotation whichAnnotationType, Constructor constructor, TestContext context) { return null; }
		public TestResult findParentConstructorTestExpressionIfEmpty(String testExpression, Annotation whichAnnotationType, Constructor constructor, TestContext context) {	return null; }
		public TestResult findParentMethodTestExpression(Annotation whichAnnotationType, Method method, TestContext context) { return null; }
		public TestResult findParentMethodTestExpressionIfEmpty(String testExpression, Annotation whichAnnotationType, Method advice, TestContext context) { return null; }
		public TestResult findParentTypeInvarTestExpression(Class clazz, TestContext context) {	return null; }
		public TestResult findParentTypeInvarTestExpressionIfEmpty(String testExpression, Class clazz, TestContext context) { return null; }
		public Reporter getReporter() { return c4j.getReporter(); }
		public StubTestExpressionFinder(Contract4J c4j) { this.c4j = c4j; }
	}
	
	public static class StubConfigurator extends AbstractConfigurator {
		protected void doConfigure() {
			Contract4J c4j = Contract4J.getInstance();
			c4j.setReporter(new WriterReporter());
			c4j.setContractEnforcer(new StubContractEnforcer()); 
			setParentTestExpressionFinder(new StubTestExpressionFinder(c4j));
		}

		private void setParentTestExpressionFinder(ParentTestExpressionFinder ptef) {
			ConstructorBoundaryConditions.aspectOf().setParentTestExpressionFinder(ptef);
			MethodBoundaryConditions.aspectOf().setParentTestExpressionFinder(ptef);
			InvariantTypeConditions.aspectOf().setParentTestExpressionFinder(ptef);
			InvariantMethodConditions.aspectOf().setParentTestExpressionFinder(ptef);
			InvariantCtorConditions.aspectOf().setParentTestExpressionFinder(ptef);
		}

		public void unsetParentTestExpressionFinder() {
			setParentTestExpressionFinder(null);
		}
	}
	
	private StubConfigurator configurator = null;
	private Contract4J c4j;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		configurator = new StubConfigurator();
		configurator.configure();
		c4j = Contract4J.getInstance();
	}
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		configurator.unsetParentTestExpressionFinder();
		// Set up fresh system-wide copy. We do it here, rather than in setUp
		// so it's available for the *next* test case!
		Contract4J.setInstance(new Contract4J()); 
	}
	
	/*
	 * Test method for 'org.contract4j5.configurator.AbstractConfigurator.getReporter()'
	 */
	public void testGetReporter() {
		Reporter reporter = c4j.getReporter();
		checkReporter(reporter);
	}

	/*
	 * Test method for 'org.contract4j5.configurator.AbstractConfigurator.setReporter(Reporter)'
	 */
	public void testSetReporter() {
		WriterReporter wr = new WriterReporter();
		c4j.setReporter(wr);
		assertEquals(wr, c4j.getReporter());
		checkReporter(wr);
	}

	/**
	 * @param reporter
	 */
	protected void checkReporter(Reporter reporter) {
		assertEquals(WriterReporter.class, reporter.getClass());
		assertEquals(reporter, c4j.getReporter());
		checkEachReporter(reporter, ConstructorBoundaryConditions.aspectOf().getParentTestExpressionFinder());
		checkEachReporter(reporter, MethodBoundaryConditions.aspectOf().getParentTestExpressionFinder());
		checkEachReporter(reporter, InvariantTypeConditions.aspectOf().getParentTestExpressionFinder());
		checkEachReporter(reporter, InvariantMethodConditions.aspectOf().getParentTestExpressionFinder());
		checkEachReporter(reporter, InvariantCtorConditions.aspectOf().getParentTestExpressionFinder());
	}
	protected void checkEachReporter(Reporter r, ParentTestExpressionFinder ptef) {
		assertTrue(ptef instanceof StubTestExpressionFinder);
		StubTestExpressionFinder stef = (StubTestExpressionFinder) ptef;
		assertEquals(c4j.getReporter(), stef.getReporter());
	}
}
