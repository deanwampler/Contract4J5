package org.contract4j5.configurator.test;

import junit.framework.TestCase;

import org.contract4j5.Contract;
import org.contract4j5.ContractError;
import org.contract4j5.Pre;
import org.contract4j5.aspects.Contract4J;

public class DefaultConfigurationWhenNoneUsedExplicitlyTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Contract4J.setSystemConfigurator(null);
		Contract4J.setConfigured(false);
	}
	
	public void testNoConfigurationOccursDuringConstructionOfContract4J() {
		assertFalse (Contract4J.isConfigured());
	}
	
	public void testConfigurationOccursWhenContract4JStateInfoIsQueried() {
		assertEquals (org.contract4j5.configurator.PropertiesConfigurator.class,
				Contract4J.getSystemConfigurator().getClass());
		assertNotNull (Contract4J.getSystemConfigurator());
		assertNotNull (Contract4J.getContractEnforcer());
		assertNotNull (Contract4J.getReporter());
		assertNotNull (Contract4J.getContractEnforcer().getReporter());
	}	

	@Contract
	public static class ErrorClass {
		@Pre("arg == null")
		public ErrorClass(String arg) {}
	}
	
	public void testDefaultConfigurationByExercisingSystemByForcingATestFailure() {
		try {
			new ErrorClass("foo");
			fail();
		} catch (ContractError ce) {
			// expected
		}
	}
}
