package org.contract4j5.configurator.test;

import junit.framework.TestCase;

import org.contract4j5.aspects.ConstructorBoundaryConditions;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Pre;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.errors.ContractError;
import org.contract4j5.errors.TestSpecificationError;

public class DefaultConfigurationWhenNoneUsedExplicitlyTest extends TestCase {
	Contract4J c4j;
	
	protected void setUp() throws Exception {
		super.setUp();
		c4j = new Contract4J();
		// Bad hack: we have to clear some "singleton" data in this aspect in order for
		// the expected startup conditions to be right. Otherwise, side-effects of previous
		// tests cause problems.
		ConstructorBoundaryConditions.aspectOf().setDefaultPreTestExpressionMaker(null);
		ConstructorBoundaryConditions.aspectOf().setDefaultPostReturningVoidTestExpressionMaker(null);
		ConstructorBoundaryConditions.aspectOf().setParentTestExpressionFinder(null);
		Contract4J.setInstance(c4j);
	}
	
	public void tearDown() throws Exception {
		super.tearDown();
		Contract4J.setInstance(null);	//reset
	}

	public void testConfigurationOccursWhenContract4JStateInfoIsQueried() {
		assertEquals (org.contract4j5.configurator.properties.PropertiesConfigurator.class,
				c4j.getSystemConfigurator().getClass());
		assertNotNull (c4j.getSystemConfigurator());
		assertNotNull (c4j.getContractEnforcer());
		assertNotNull (c4j.getReporter());
	}	

	@Contract
	public static class ErrorClass {
		@Pre("arg == null")
		public ErrorClass(String arg) {}
	}
	
	public void testDefaultConfigurationByExercisingSystemByForcingATestFailure() {
		try {
			assertNotNull(Contract4J.getInstance());
			new ErrorClass("foo");
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			// expected
		}
	}
}
