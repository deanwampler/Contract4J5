package org.contract4j5.configurator.spring.test;

import java.util.Map;

import junit.framework.TestCase;

import org.contract4j5.configurator.NullConfigurator;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.enforcer.ContractEnforcer;
import org.contract4j5.enforcer.defaultimpl.DefaultContractEnforcer;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.bsf.groovy.GroovyBSFExpressionInterpreter;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.Severity;
import org.contract4j5.reporter.WriterReporter;
import org.contract4j5.testexpression.DefaultFieldInvarTestExpressionMaker;
import org.contract4j5.testexpression.DefaultPostTestExpressionMaker;
import org.contract4j5.testexpression.DefaultPreTestExpressionMaker;
import org.contract4j5.testexpression.DefaultTestExpressionMaker;
import org.contract4j5.testexpression.ParentTestExpressionFinder;
import org.contract4j5.testexpression.ParentTestExpressionFinderImpl;
import org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.contract4j5.aspects.ConstructorBoundaryConditions;
import org.contract4j5.aspects.MethodBoundaryConditions;
import org.contract4j5.aspects.InvariantCtorConditions;
import org.contract4j5.aspects.InvariantFieldConditions;
import org.contract4j5.aspects.InvariantFieldCtorConditions;
import org.contract4j5.aspects.InvariantMethodConditions;
import org.contract4j5.aspects.InvariantTypeConditions;

public class ConstructWithSpringTest extends TestCase {

	private ApplicationContext applicationContext;
	protected void setUp() throws Exception {
		super.setUp();
		applicationContext = new ClassPathXmlApplicationContext(
			"conf/applicationContext-contract4j5.xml");
	}

	public void testLoadMainBeansAndAspects() {
		Contract4J c4j = (Contract4J) applicationContext.getBean("contract4j5");
		assertEquals(Contract4J.class, c4j.getClass());
		assertTrue(c4j.isInvarTestsEnabled());
		assertTrue(c4j.isPreTestsEnabled());
		assertTrue(c4j.isPostTestsEnabled());
		assertEquals(NullConfigurator.class, c4j.getSystemConfigurator().getClass());

		Reporter reporter = (Reporter) applicationContext.getBean("globalReporter");
		assertEquals(WriterReporter.class, reporter.getClass());
		assertEquals(Severity.WARN, reporter.getThreshold());

		ContractEnforcer contractEnforcer = (ContractEnforcer) applicationContext.getBean("contractEnforcer");
		assertEquals(DefaultContractEnforcer.class, contractEnforcer.getClass());
		assertTrue(contractEnforcer.getReportErrors());
		assertEquals(Severity.FATAL, contractEnforcer.getErrorReportingSeverityLevel());
		assertFalse(contractEnforcer.getIncludeStackTrace());
		
		ExpressionInterpreter expressionInterpreter = (ExpressionInterpreter) applicationContext.getBean("expressionInterpreter");
		assertEquals(GroovyBSFExpressionInterpreter.class, expressionInterpreter.getClass());
		assertFalse(expressionInterpreter.getTreatEmptyTestExpressionAsValidTest());
		Map<String, String> map = expressionInterpreter.getOptionalKeywordSubstitutions();
		assertEquals(2, map.size());
		assertEquals("foo", map.get("FOO"));
		assertEquals("bar", map.get("BAR"));

		DefaultTestExpressionMaker defaultPreTestExpressionMaker = 
			(DefaultTestExpressionMaker) applicationContext.getBean("defaultPreTestExpressionMaker");
		assertEquals(DefaultPreTestExpressionMaker.class, defaultPreTestExpressionMaker.getClass());
		
		DefaultTestExpressionMaker defaultPostTestExpressionMaker = 
			(DefaultTestExpressionMaker) applicationContext.getBean("defaultPostTestExpressionMaker");
		assertEquals(DefaultPostTestExpressionMaker.class, defaultPostTestExpressionMaker.getClass());

		DefaultTestExpressionMaker defaultPostReturningVoidTestExpressionMaker = 
			(DefaultTestExpressionMaker) applicationContext.getBean("defaultPostReturningVoidTestExpressionMaker");
		assertEquals(SimpleStringDefaultTestExpressionMaker.class, defaultPostReturningVoidTestExpressionMaker.getClass());

		DefaultTestExpressionMaker defaultFieldInvarTestExpressionMaker = 
			(DefaultTestExpressionMaker) applicationContext.getBean("defaultFieldInvarTestExpressionMaker");
		assertEquals(DefaultFieldInvarTestExpressionMaker.class, defaultFieldInvarTestExpressionMaker.getClass());

		DefaultTestExpressionMaker simpleStringDefaultTestExpressionMaker = 
			(DefaultTestExpressionMaker) applicationContext.getBean("simpleStringDefaultTestExpressionMaker");
		assertEquals(SimpleStringDefaultTestExpressionMaker.class, simpleStringDefaultTestExpressionMaker.getClass());

		ParentTestExpressionFinder parentTestExpressionFinder = 
			(ParentTestExpressionFinder) applicationContext.getBean("parentTestExpressionFinder");
		assertEquals(ParentTestExpressionFinderImpl.class, parentTestExpressionFinder.getClass());


		ConstructorBoundaryConditions cbc = (ConstructorBoundaryConditions) 
			applicationContext.getBean("constructorBoundaryConditions");
		assertEquals(ConstructorBoundaryConditions.class, cbc.getClass());
		assertEquals(defaultPreTestExpressionMaker, cbc.getDefaultPreTestExpressionMaker());
		assertEquals(defaultPostReturningVoidTestExpressionMaker, cbc.getDefaultPostReturningVoidTestExpressionMaker());
		assertEquals(parentTestExpressionFinder, cbc.getParentTestExpressionFinder());
		
		MethodBoundaryConditions mbc = (MethodBoundaryConditions) 
			applicationContext.getBean("methodBoundaryConditions");
		assertEquals(MethodBoundaryConditions.class, mbc.getClass());
		assertEquals(defaultPreTestExpressionMaker, mbc.getDefaultPreTestExpressionMaker());
		assertEquals(defaultPostTestExpressionMaker, mbc.getDefaultPostTestExpressionMaker());
		assertEquals(defaultPostReturningVoidTestExpressionMaker, mbc.getDefaultPostReturningVoidTestExpressionMaker());
		assertEquals(parentTestExpressionFinder, mbc.getParentTestExpressionFinder());
		
		InvariantCtorConditions icc = (InvariantCtorConditions) 
			applicationContext.getBean("invariantCtorConditions");
		assertEquals(InvariantCtorConditions.class, icc.getClass());
		assertEquals(simpleStringDefaultTestExpressionMaker, icc.getDefaultCtorInvarTestExpressionMaker());
		assertEquals(parentTestExpressionFinder, icc.getParentTestExpressionFinder());
		
		InvariantFieldConditions ifc = (InvariantFieldConditions) 
			applicationContext.getBean("invariantFieldConditions");
		assertEquals(InvariantFieldConditions.class, ifc.getClass());
		assertEquals(defaultFieldInvarTestExpressionMaker, ifc.getDefaultFieldInvarTestExpressionMaker());
		
		InvariantFieldCtorConditions ifcc = (InvariantFieldCtorConditions) 
			applicationContext.getBean("invariantFieldCtorConditions");
		assertEquals(InvariantFieldCtorConditions.class, ifcc.getClass());
		assertEquals(defaultFieldInvarTestExpressionMaker, ifcc.getDefaultFieldInvarTestExpressionMaker());
		
		InvariantMethodConditions imc = (InvariantMethodConditions) 
			applicationContext.getBean("invariantMethodConditions");
		assertEquals(InvariantMethodConditions.class, imc.getClass());
		assertEquals(simpleStringDefaultTestExpressionMaker, imc.getDefaultMethodInvarTestExpressionMaker());
		assertEquals(parentTestExpressionFinder, imc.getParentTestExpressionFinder());
		
		InvariantTypeConditions itc = (InvariantTypeConditions)
			applicationContext.getBean("invariantTypeConditions");		
		assertEquals(InvariantTypeConditions.class, itc.getClass());
		assertEquals(simpleStringDefaultTestExpressionMaker, itc.getDefaultTypeInvarTestExpressionMaker());
		assertEquals(parentTestExpressionFinder, itc.getParentTestExpressionFinder());
	}
}
