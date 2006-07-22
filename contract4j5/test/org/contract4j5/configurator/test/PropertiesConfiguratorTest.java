package org.contract4j5.configurator.test;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.contract4j5.aspects.Contract4J;
import org.contract4j5.aspects.ConstructorBoundaryConditions;
import org.contract4j5.aspects.MethodBoundaryConditions;
import org.contract4j5.aspects.InvariantConditions;
import org.contract4j5.ContractEnforcer;
import org.contract4j5.NullContractEnforcer;
import org.contract4j5.TestContext;
import org.contract4j5.configurator.AbstractConfigurator;
import org.contract4j5.configurator.Configurator;
import org.contract4j5.configurator.PropertiesConfigurator;
import org.contract4j5.configurator.PropertiesConfigurator.KnownBeanKeys;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.testexpression.DefaultTestExpressionMaker;
import org.contract4j5.testexpression.ParentTestExpressionFinder;
import org.contract4j5.util.reporter.Reporter;
import org.contract4j5.util.reporter.Severity;
import org.contract4j5.util.reporter.WriterReporter;

public class PropertiesConfiguratorTest extends TestCase {
	public static class StubConfigurator extends AbstractConfigurator {
		public boolean wasCalled = false;
		@Override
		protected void doConfigure() {
			wasCalled = true;
		}
	}
	
	public static class StubReporter implements Reporter {
		public Severity getThreshold() {
			return null;
		}
		public void report(Severity level, Class clazz, String message) {
		}

		public void setThreshold(Severity level) {
		}
	}
	
	public StubConfigurator configurator = null;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		StubOutputStream.invoked = false;
		Contract4J.setSystemConfigurator(null);
		configurator = new StubConfigurator();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		for (KnownBeanKeys beanKey: KnownBeanKeys.values()) {
			unsetProp(beanKey.name());
		}
	}
	
	public void testDefaultConfiguratorIsSet() {
		assertEquals (org.contract4j5.configurator.PropertiesConfigurator.class,
				Contract4J.getSystemConfigurator().getClass());
	}
	
	public void testConfiguratorGetSet() {
		Contract4J.setSystemConfigurator(configurator);
		assertEquals (configurator,	Contract4J.getSystemConfigurator());
	}
	
	public void testSetReporterThroughProperty() {
		setProp(PropertiesConfigurator.KnownBeanKeys.GlobalReporter.name(), 
				StubReporter.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		Reporter r = c.getReporter();
		assertEquals (PropertiesConfiguratorTest.StubReporter.class,
				r.getClass());
	}

	public void testSetReporterFailsBecauseValueEmpty() {
		setProp(PropertiesConfigurator.KnownBeanKeys.GlobalReporter.name(), "");
		Configurator c = new PropertiesConfigurator();
		c.configure();
		Reporter r = c.getReporter();
		// defaults to WriterReporter?
		assertEquals (WriterReporter.class,	r.getClass());
	}

	public void testSetReporterThresholdThroughProperty() {
		setProp(PropertiesConfigurator.KnownBeanKeys.GlobalReporter.name(), 
				WriterReporter.class.getName());
		setProp(PropertiesConfigurator.KnownBeanKeys.GlobalReporterThreshold.name(), 
				Severity.DEBUG.name());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		Reporter r = c.getReporter();
		assertEquals (Severity.DEBUG, r.getThreshold());
	}

	public static class StubWriter extends java.io.Writer {
		public void close() throws IOException {}
		public void flush() throws IOException {}
		public void write(char[] cbuf, int off, int len) throws IOException {}
	}
	
	public static class StubOutputStream extends java.io.OutputStream {
		public static boolean invoked = false;
		public void write(int arg0) throws IOException { invoked = true; }
	}
	
	public void testSetWriterReporterWritersThroughProperty() {
		setProp(PropertiesConfigurator.KnownBeanKeys.GlobalReporter.name(), 
				WriterReporter.class.getName());
		setProp(PropertiesConfigurator.KnownBeanKeys.GlobalWriterReporterWriter.name(), 
				StubWriter.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		WriterReporter wr = (WriterReporter) c.getReporter();
		assertEquals (StubWriter.class, wr.getWriter(Severity.DEBUG).getClass());
		assertEquals (StubWriter.class, wr.getWriter(Severity.ERROR).getClass());
	}

	public void testSetWriterReporterOutputStreamThroughProperty() {
		setProp(PropertiesConfigurator.KnownBeanKeys.GlobalReporter.name(), 
				WriterReporter.class.getName());
		setProp(PropertiesConfigurator.KnownBeanKeys.GlobalWriterReporterOutputStream.name(), 
				StubOutputStream.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		WriterReporter wr = (WriterReporter) c.getReporter();
		wr.report(Severity.ERROR, this.getClass(), "hello!");
		assertTrue (StubOutputStream.invoked);
	}

	public void testMissingClassHandled() {
		setProp(PropertiesConfigurator.KnownBeanKeys.GlobalReporter.name(), 
				"NonexistentClass");
		Configurator c = new PropertiesConfigurator();
		c.configure();
		Reporter r = c.getReporter();
		assertEquals (WriterReporter.class, r.getClass());
	}

	public void testSetWriterReporterStreamsTrumpsWriters() {
		setProp(PropertiesConfigurator.KnownBeanKeys.GlobalReporter.name(), 
				WriterReporter.class.getName());
		setProp(PropertiesConfigurator.KnownBeanKeys.GlobalWriterReporterWriter.name(), 
				StubWriter.class.getName());
		setProp(PropertiesConfigurator.KnownBeanKeys.GlobalWriterReporterOutputStream.name(), 
				StubOutputStream.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		assertTrue (c.getReporter() instanceof WriterReporter);
		StubOutputStream.invoked = false;
		c.getReporter().report(Severity.ERROR, this.getClass(), "hello!");
		assertTrue (StubOutputStream.invoked);
	}

	public void testSetWriterReporterWritersThroughPropertyButNotAWriterReporter() {
		setProp(PropertiesConfigurator.KnownBeanKeys.GlobalReporter.name(), 
				StubReporter.class.getName());
		setProp(PropertiesConfigurator.KnownBeanKeys.GlobalWriterReporterWriter.name(), 
				StubWriter.class.getName());
		setProp(PropertiesConfigurator.KnownBeanKeys.GlobalWriterReporterOutputStream.name(), 
				StubOutputStream.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		assertFalse (c.getReporter() instanceof WriterReporter);
		c.getReporter().report(Severity.ERROR, this.getClass(), "hello!");
		assertFalse (StubOutputStream.invoked);
	}

	public void testSetContractEnforcerThroughProperty() {
		setProp(PropertiesConfigurator.KnownBeanKeys.ContractEnforcer.name(), 
				NullContractEnforcer.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		ContractEnforcer ce = Contract4J.getContractEnforcer();
		assertEquals (NullContractEnforcer.class, ce.getClass());
	}
	
	public void testSetIncludeStackTrace() {
		setProp(PropertiesConfigurator.KnownBeanKeys.ContractEnforcer.name(), 
				NullContractEnforcer.class.getName());
		setProp(PropertiesConfigurator.KnownBeanKeys.ContractEnforcerIncludeStackTrace.name(), 
				"true");
		Configurator c = new PropertiesConfigurator();
		c.configure();
		ContractEnforcer ce = Contract4J.getContractEnforcer();
		assertTrue (ce.getIncludeStackTrace());
	}
	
	
	public static class StubExpressionInterpreter implements ExpressionInterpreter {
		public Map<String, Object> determineOldValues(String testExpression, TestContext context) {	return null; }
		public Reporter getReporter() {	return null; }
		public void     setReporter(Reporter reporter) {}
		private boolean empty = false;
		public boolean getTreatEmptyTestExpressionAsValidTest() { return empty; }
		public void    setTreatEmptyTestExpressionAsValidTest(boolean emptyOK) { empty=emptyOK; }
		private Map<String, String> subs = null;
		public Map<String, String> getOptionalKeywordSubstitutions() { return subs; }
		public void setOptionalKeywordSubstitutions(Map<String, String> subs) { this.subs = subs; }
		public TestResult invokeTest(String testExpression, TestContext context) { return null;	}
		public TestResult validateTestExpression(String testExpression, TestContext context) { return null; }
	}
	
	public void testSetExpressionInterpreterThroughProperty() {
		setProp(PropertiesConfigurator.KnownBeanKeys.ExpressionInterpreter.name(), 
				StubExpressionInterpreter.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		ExpressionInterpreter ei = Contract4J.getContractEnforcer().getExpressionInterpreter();
		assertEquals (StubExpressionInterpreter.class, ei.getClass());
	}
	
	public void testSetEmptyTestExpressionsOK() {
		setProp(PropertiesConfigurator.KnownBeanKeys.ExpressionInterpreter.name(), 
				StubExpressionInterpreter.class.getName());
		setProp(PropertiesConfigurator.KnownBeanKeys.ExpressionInterpreterEmptyTestExpressionsValid.name(), "true");
		Configurator c = new PropertiesConfigurator();
		c.configure();
		ExpressionInterpreter ei = Contract4J.getContractEnforcer().getExpressionInterpreter();
		assertTrue (ei.getTreatEmptyTestExpressionAsValidTest());
	}

	public void testSetOptionalKeywordSubstitutions1() {	
		doTestSetOptionalKeywordSubstitutions("foobar=barfoo,xxxxx=yyy");
	}
	public void testSetOptionalKeywordSubstitutions2() {	
		doTestSetOptionalKeywordSubstitutions(" 	foobar =	 barfoo 	,	  xxxxx	 = 	yyy  	");
	}
	public void testSetOptionalKeywordSubstitutions3() {	
		doTestSetOptionalKeywordSubstitutions("=bad, foobar=barfoo, xxxxx=yyy");
	}
	public void testSetOptionalKeywordSubstitutions4() {	
		doTestSetOptionalKeywordSubstitutions("foobar=barfoo, xxxxx=yyy, =bad2");
	}
	public void testSetOptionalKeywordSubstitutions5() {	
		doTestSetOptionalKeywordSubstitutions("foobar=barfoo, =bad3, xxxxx=yyy");
	}
	
	public void doTestSetOptionalKeywordSubstitutions(String mapString) {
		setProp(PropertiesConfigurator.KnownBeanKeys.ExpressionInterpreter.name(), 
				StubExpressionInterpreter.class.getName());
		setProp(PropertiesConfigurator.KnownBeanKeys.ExpressionInterpreterOptionalKeywordSubstitutions.name(), mapString);
		Configurator c = new PropertiesConfigurator();
		c.configure();
		ExpressionInterpreter ei = Contract4J.getContractEnforcer().getExpressionInterpreter();
		Map<String,String> subs = ei.getOptionalKeywordSubstitutions();
		assertEquals (2,        subs.size());
		assertEquals ("barfoo", subs.get("foobar"));
		assertEquals ("yyy",    subs.get("xxxxx"));
		assertNull   (subs.get("nada"));
	}
	
	public static class StubDefaultTestExpressionMaker implements DefaultTestExpressionMaker {
		public boolean isNotPrimitive(Class clazz) {
			return false;
		}
		public String makeArgsNotNullExpression(TestContext context) {
			return null;
		}
		public String makeDefaultTestExpression(TestContext context) {
			return null;
		}
		public String makeDefaultTestExpressionIfEmpty(String testExpression, TestContext context) {
			return testExpression;
		}
	}
	
	public static class StubParentTestExpressionFinder implements ParentTestExpressionFinder {

		public TestResult findParentAdviceTestExpression(Annotation whichAnnotationType, Method advice, TestContext context) {
			return null;
		}
		public TestResult findParentAdviceTestExpressionIfEmpty(String testExpression, Annotation whichAnnotationType, Method advice, TestContext context) {
			return null;
		}
		public TestResult findParentConstructorTestExpression(Annotation whichAnnotationType, Constructor constructor, TestContext context) {
			return null;
		}
		public TestResult findParentConstructorTestExpressionIfEmpty(String testExpression, Annotation whichAnnotationType, Constructor constructor, TestContext context) {
			return null;
		}
		public TestResult findParentMethodTestExpression(Annotation whichAnnotationType, Method method, TestContext context) {
			return null;
		}
		public TestResult findParentMethodTestExpressionIfEmpty(String testExpression, Annotation whichAnnotationType, Method advice, TestContext context) {
			return null;
		}
		public TestResult findParentTypeInvarTestExpression(Class clazz, TestContext context) {
			return null;
		}
		public TestResult findParentTypeInvarTestExpressionIfEmpty(String testExpression, Class clazz, TestContext context) {
			return null;
		}
		public Reporter getReporter() {
			return null;
		}
		public void setReporter(Reporter reporter) {
		}
	}
	
	public void testSetDefaultFieldInvarTestExpressionMaker() {
		setProp(PropertiesConfigurator.KnownBeanKeys.DefaultFieldInvarTestExpressionMaker.name(), 
				StubDefaultTestExpressionMaker.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		DefaultTestExpressionMaker dtem = 
			InvariantConditions.InvariantFieldConditions.getDefaultFieldInvarTestExpressionMaker();	
		assertEquals (StubDefaultTestExpressionMaker.class, dtem.getClass());
	}
	
	public void testSetDefaultFieldCtorInvarTestExpressionMaker() {
		setProp(PropertiesConfigurator.KnownBeanKeys.DefaultFieldCtorInvarTestExpressionMaker.name(), 
				StubDefaultTestExpressionMaker.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		DefaultTestExpressionMaker dtem = 
			InvariantConditions.InvariantFieldCtorConditions.getDefaultFieldInvarTestExpressionMaker();	
		assertEquals (StubDefaultTestExpressionMaker.class, dtem.getClass());
	}
	
	public void testSetDefaultMethodInvarTestExpressionMaker() {
		setProp(PropertiesConfigurator.KnownBeanKeys.DefaultMethodInvarTestExpressionMaker.name(), 
				StubDefaultTestExpressionMaker.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		DefaultTestExpressionMaker dtem = 
			InvariantConditions.InvariantMethodConditions.getDefaultMethodInvarTestExpressionMaker();	
		assertEquals (StubDefaultTestExpressionMaker.class, dtem.getClass());
	}
	
	public void testSetDefaultCtorInvarTestExpressionMaker() {
		setProp(PropertiesConfigurator.KnownBeanKeys.DefaultCtorInvarTestExpressionMaker.name(), 
				StubDefaultTestExpressionMaker.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		DefaultTestExpressionMaker dtem = 
			InvariantConditions.InvariantCtorConditions.getDefaultCtorInvarTestExpressionMaker();	
		assertEquals (StubDefaultTestExpressionMaker.class, dtem.getClass());
	}
	
	public void testSetDefaultTypeInvarTestExpressionMaker() {
		setProp(PropertiesConfigurator.KnownBeanKeys.DefaultTypeInvarTestExpressionMaker.name(), 
				StubDefaultTestExpressionMaker.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		DefaultTestExpressionMaker dtem = 
			InvariantConditions.InvariantTypeConditions.getDefaultTypeInvarTestExpressionMaker();	
		assertEquals (StubDefaultTestExpressionMaker.class, dtem.getClass());
	}
	
	public void testSetDefaultCtorPreTestExpressionMaker() {
		setProp(PropertiesConfigurator.KnownBeanKeys.DefaultCtorPreTestExpressionMaker.name(), 
				StubDefaultTestExpressionMaker.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		DefaultTestExpressionMaker dtem = 
			ConstructorBoundaryConditions.getDefaultPreTestExpressionMaker();	
		assertEquals (StubDefaultTestExpressionMaker.class, dtem.getClass());
	}
	
	public void testSetDefaultCtorPostRtnVoidTestExpressionMaker() {
		setProp(PropertiesConfigurator.KnownBeanKeys.DefaultCtorPostReturningVoidTestExpressionMaker.name(), 
				StubDefaultTestExpressionMaker.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		DefaultTestExpressionMaker dtem = 
			ConstructorBoundaryConditions.getDefaultPostReturningVoidTestExpressionMaker();	
		assertEquals (StubDefaultTestExpressionMaker.class, dtem.getClass());
	}
	
	public void testSetDefaultMethodPreTestExpressionMaker() {
		setProp(PropertiesConfigurator.KnownBeanKeys.DefaultMethodPreTestExpressionMaker.name(), 
				StubDefaultTestExpressionMaker.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		DefaultTestExpressionMaker dtem = 
			MethodBoundaryConditions.getDefaultPreTestExpressionMaker();	
		assertEquals (StubDefaultTestExpressionMaker.class, dtem.getClass());
	}
	
	public void testSetDefaultMethodPostRtnVoidTestExpressionMaker() {
		setProp(PropertiesConfigurator.KnownBeanKeys.DefaultMethodPostReturningVoidTestExpressionMaker.name(), 
				StubDefaultTestExpressionMaker.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		DefaultTestExpressionMaker dtem = 
			MethodBoundaryConditions.getDefaultPostReturningVoidTestExpressionMaker();	
		assertEquals (StubDefaultTestExpressionMaker.class, dtem.getClass());
	}
	
	public void testSetDefaultMethodPostTestExpressionMaker() {
		setProp(PropertiesConfigurator.KnownBeanKeys.DefaultMethodPostTestExpressionMaker.name(), 
				StubDefaultTestExpressionMaker.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		DefaultTestExpressionMaker dtem = 
			MethodBoundaryConditions.getDefaultPostTestExpressionMaker();	
		assertEquals (StubDefaultTestExpressionMaker.class, dtem.getClass());
	}
	
	public void testSetCtorParentTestExpressionFinder() {
		setProp(PropertiesConfigurator.KnownBeanKeys.CtorParentTestExpressionFinder.name(), 
				StubParentTestExpressionFinder.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		ParentTestExpressionFinder ptef = 
			ConstructorBoundaryConditions.getParentTestExpressionFinder();	
		assertEquals (StubParentTestExpressionFinder.class, ptef.getClass());
	}
	
	public void testSetMethodParentTestExpressionFinder() {
		setProp(PropertiesConfigurator.KnownBeanKeys.MethodParentTestExpressionFinder.name(), 
				StubParentTestExpressionFinder.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		ParentTestExpressionFinder ptef = 
			MethodBoundaryConditions.getParentTestExpressionFinder();	
		assertEquals (StubParentTestExpressionFinder.class, ptef.getClass());
	}
	
	public void testSetMethodInvarParentTestExpressionFinder() {
		setProp(PropertiesConfigurator.KnownBeanKeys.MethodInvarParentTestExpressionFinder.name(), 
				StubParentTestExpressionFinder.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		ParentTestExpressionFinder ptef = 
			InvariantConditions.InvariantMethodConditions.getParentTestExpressionFinder();	
		assertEquals (StubParentTestExpressionFinder.class, ptef.getClass());
	}
	
	public void testSetCtorInvarParentTestExpressionFinder() {
		setProp(PropertiesConfigurator.KnownBeanKeys.CtorInvarParentTestExpressionFinder.name(), 
				StubParentTestExpressionFinder.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		ParentTestExpressionFinder ptef = 
			InvariantConditions.InvariantCtorConditions.getParentTestExpressionFinder();	
		assertEquals (StubParentTestExpressionFinder.class, ptef.getClass());
	}
	
	public void testSetTypeInvarParentTestExpressionFinder() {
		setProp(PropertiesConfigurator.KnownBeanKeys.TypeInvarParentTestExpressionFinder.name(), 
				StubParentTestExpressionFinder.class.getName());
		Configurator c = new PropertiesConfigurator();
		c.configure();
		ParentTestExpressionFinder ptef = 
			InvariantConditions.InvariantTypeConditions.getParentTestExpressionFinder();	
		assertEquals (StubParentTestExpressionFinder.class, ptef.getClass());
	}
	
	public void testSetTypeInvarParentTestExpressionFinderWithPassedInProps() {
		Properties props = new Properties();
		props.setProperty(PropertiesConfigurator.PROPERTY_PREFIX + 
			PropertiesConfigurator.KnownBeanKeys.TypeInvarParentTestExpressionFinder.name(), 
			StubParentTestExpressionFinder.class.getName());
		Configurator c = new PropertiesConfigurator(props);
		c.configure();
		ParentTestExpressionFinder ptef = 
			InvariantConditions.InvariantTypeConditions.getParentTestExpressionFinder();	
		assertEquals (StubParentTestExpressionFinder.class, ptef.getClass());
	}
	
	public void testSetEmptyTestExpressionsOKWithPassedInProps() {
		Properties props = new Properties();
		props.setProperty(PropertiesConfigurator.PROPERTY_PREFIX + 
			PropertiesConfigurator.KnownBeanKeys.ExpressionInterpreter.name(), 
			StubExpressionInterpreter.class.getName());
		props.setProperty(PropertiesConfigurator.PROPERTY_PREFIX + 
			PropertiesConfigurator.KnownBeanKeys.ExpressionInterpreterEmptyTestExpressionsValid.name(), "true");
		Configurator c = new PropertiesConfigurator(props);
		c.configure();
		ExpressionInterpreter ei = Contract4J.getContractEnforcer().getExpressionInterpreter();
		assertTrue (ei.getTreatEmptyTestExpressionAsValidTest());
	}

	public void testEnableContractFlag() {
		setupEnabledFlags(false, false, false);
		checkEnabled(0, true, true, true, true);
	}

	public void testDisableContractFlag() {
		setupEnabledFlags(true, true, true);
		checkEnabled(0, false, false, false, false);
	}

	public void testEnablePreFlag() {
		setupEnabledFlags(false, false, false);
		checkEnabled(1, true, true, false, false);
	}

	public void testDisablePreFlag() {
		setupEnabledFlags(true, false, false);
		checkEnabled(1, false, false, false, false);
	}

	public void testEnablePostFlag() {
		setupEnabledFlags(false, false, false);
		checkEnabled(2, true, false, true, false);
	}

	public void testDisablePostFlag() {
		setupEnabledFlags(false, true, false);
		checkEnabled(2, false, false, false, false);
	}

	public void testEnableInvarFlag() {
		setupEnabledFlags(false, false, false);
		checkEnabled(3, true, false, false, true);
	}

	public void testDisableInvarFlag() {
		setupEnabledFlags(false, false, true);
		checkEnabled(3, false, false, false, false);
	}

	private void setupEnabledFlags(boolean preFlag, boolean postFlag, boolean invarFlag) {
		for (int i = 0; i < PropertiesConfigurator.enabledPropertyKeys.length; i++) {
			unsetProp(PropertiesConfigurator.enabledPropertyKeys[i]);
		}
		Contract4J.setEnabled(Contract4J.TestType.Pre,   preFlag);
		Contract4J.setEnabled(Contract4J.TestType.Post,  postFlag);
		Contract4J.setEnabled(Contract4J.TestType.Invar, invarFlag);
		doTestPrePostInvarValues(preFlag, postFlag, invarFlag);
	}
	
	private void checkEnabled(int whichEnabledPropertyKey, boolean enabledPropertyValue,
			boolean preFlag, boolean postFlag, boolean invarFlag) {
		setProp(PropertiesConfigurator.enabledPropertyKeys[whichEnabledPropertyKey], 
				Boolean.toString(enabledPropertyValue));
		Configurator c = new PropertiesConfigurator();
		c.configure();
		unsetProp(PropertiesConfigurator.enabledPropertyKeys[whichEnabledPropertyKey]);
		doTestPrePostInvarValues(preFlag, postFlag, invarFlag);
	}

	private void doTestPrePostInvarValues(boolean preFlag, boolean postFlag, boolean invarFlag) {
		boolean b =  Contract4J.isEnabled(Contract4J.TestType.Pre);
		assertEquals(preFlag,   Contract4J.isEnabled(Contract4J.TestType.Pre));
		assertEquals(postFlag,  Contract4J.isEnabled(Contract4J.TestType.Post));
		assertEquals(invarFlag, Contract4J.isEnabled(Contract4J.TestType.Invar));
	}

	private void setProp(String name, String value) {
		System.setProperty(PropertiesConfigurator.PROPERTY_PREFIX + name, value);
	}
	private void unsetProp(String name) {
		System.clearProperty(PropertiesConfigurator.PROPERTY_PREFIX + name);
	}
	
//	public void testDumpSystemProps() {
//		Properties props = System.getProperties();
//		props.list(System.out);
//	}
}
