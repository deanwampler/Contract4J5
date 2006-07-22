package org.contract4j5.configurator.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import junit.framework.TestCase;

import org.contract4j5.ContractEnforcer;
import org.contract4j5.ContractError;
import org.contract4j5.TestContext;
import org.contract4j5.aspects.ConstructorBoundaryConditions;
import org.contract4j5.aspects.Contract4J;
import org.contract4j5.aspects.InvariantConditions;
import org.contract4j5.aspects.MethodBoundaryConditions;
import org.contract4j5.configurator.AbstractConfigurator;
import org.contract4j5.configurator.Configurator;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.testexpression.ParentTestExpressionFinder;
import org.contract4j5.util.reporter.Reporter;
import org.contract4j5.util.reporter.WriterReporter;

public class AbstractConfiguratorTest extends TestCase {
	public static class StubExpressionInterpreter implements ExpressionInterpreter {
		public Map<String, Object> determineOldValues(String testExpression, TestContext context) { return null; }
		public Map<String, String> getOptionalKeywordSubstitutions() { return null;	}
		private Reporter reporter = null;
		public Reporter getReporter() {	return reporter; }
		public void     setReporter(Reporter reporter) { this.reporter = reporter; }
		public boolean getTreatEmptyTestExpressionAsValidTest() { return false;	}
		public TestResult invokeTest(String testExpression, TestContext context) { return null; }
		public void setOptionalKeywordSubstitutions(Map<String, String> optionalKeywordSubstitutions) {}
		public void setTreatEmptyTestExpressionAsValidTest(boolean emptyOK) {}
		public TestResult validateTestExpression(String testExpression, TestContext context) { return null;	}
	}
	
	public static class StubContractEnforcer implements ContractEnforcer {
		private ExpressionInterpreter expressionInterpreter = new StubExpressionInterpreter();
		public ExpressionInterpreter getExpressionInterpreter() { return expressionInterpreter;	}
		public boolean getIncludeStackTrace() {	return false; }
		private Reporter reporter = null;
		public Reporter getReporter() {	return reporter; }
		public void     setReporter(Reporter reporter) { this.reporter = reporter; }
		public void handleFailure(String message, Throwable throwable) throws ContractError {}
		public void handleFailure(String message) throws ContractError {}
		public void handleFailure() throws ContractError {}
		public void invokeTest(String testExpression, String testPrefix, String extraMessage, TestContext context) {}
		public String makeFailureMessage(String testExpression, String testPrefix, String extraMessage, TestContext context, TestResult testResult) { return null; }
		public void setExpressionInterpreter(ExpressionInterpreter expressionInterpreter) {}
		public void setIncludeStackTrace(boolean onOff) {}
	}

	public static class StubTestExpressionFinder implements ParentTestExpressionFinder {
		public TestResult findParentAdviceTestExpression(Annotation whichAnnotationType, Method advice, TestContext context) { return null;	}
		public TestResult findParentAdviceTestExpressionIfEmpty(String testExpression, Annotation whichAnnotationType, Method advice, TestContext context) { return null; }
		public TestResult findParentConstructorTestExpression(Annotation whichAnnotationType, Constructor constructor, TestContext context) { return null; }
		public TestResult findParentConstructorTestExpressionIfEmpty(String testExpression, Annotation whichAnnotationType, Constructor constructor, TestContext context) {	return null; }
		public TestResult findParentMethodTestExpression(Annotation whichAnnotationType, Method method, TestContext context) { return null; }
		public TestResult findParentMethodTestExpressionIfEmpty(String testExpression, Annotation whichAnnotationType, Method advice, TestContext context) { return null; }
		public TestResult findParentTypeInvarTestExpression(Class clazz, TestContext context) {	return null; }
		public TestResult findParentTypeInvarTestExpressionIfEmpty(String testExpression, Class clazz, TestContext context) { return null; }
		private Reporter reporter = null;
		public Reporter getReporter() {	return reporter; }
		public void     setReporter(Reporter reporter) { this.reporter = reporter; }
	}
	
	public static class StubConfigurator extends AbstractConfigurator {
		@Override
		protected void doConfigure() {
			Reporter r = new WriterReporter();
			setReporter(r);
			Contract4J.setContractEnforcer(new StubContractEnforcer()); 
			ConstructorBoundaryConditions.setParentTestExpressionFinder(new StubTestExpressionFinder());
			MethodBoundaryConditions.setParentTestExpressionFinder(new StubTestExpressionFinder());
			InvariantConditions.InvariantTypeConditions.setParentTestExpressionFinder(new StubTestExpressionFinder());
			InvariantConditions.InvariantMethodConditions.setParentTestExpressionFinder(new StubTestExpressionFinder());
			InvariantConditions.InvariantCtorConditions.setParentTestExpressionFinder(new StubTestExpressionFinder());
			setReporter(r);
		}
	}
	
	private Configurator configurator = null;
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		configurator = new StubConfigurator();
		configurator.configure();
	}
	
	/*
	 * Test method for 'org.contract4j5.configurator.AbstractConfigurator.getReporter()'
	 */
	public void testGetReporter() {
		Reporter reporter = configurator.getReporter();
		checkReporter(reporter);
	}

	/*
	 * Test method for 'org.contract4j5.configurator.AbstractConfigurator.setReporter(Reporter)'
	 */
	public void testSetReporter() {
		WriterReporter wr = new WriterReporter();
		configurator.setReporter(wr);
		assertEquals(wr, configurator.getReporter());
		checkReporter(wr);
	}

	/**
	 * @param reporter
	 */
	protected void checkReporter(Reporter reporter) {
		assertEquals(WriterReporter.class, reporter.getClass());
		assertEquals(reporter, Contract4J.getReporter());
		assertEquals(reporter, Contract4J.getContractEnforcer().getReporter());
		assertEquals(reporter, Contract4J.getContractEnforcer().getExpressionInterpreter().getReporter());
		assertEquals(reporter, ConstructorBoundaryConditions.getParentTestExpressionFinder().getReporter());
		assertEquals(reporter, MethodBoundaryConditions.getParentTestExpressionFinder().getReporter());
		assertEquals(reporter, InvariantConditions.InvariantTypeConditions.getParentTestExpressionFinder().getReporter());
		assertEquals(reporter, InvariantConditions.InvariantMethodConditions.getParentTestExpressionFinder().getReporter());
		assertEquals(reporter, InvariantConditions.InvariantCtorConditions.getParentTestExpressionFinder().getReporter());
	}
}
