package org.contract4j5.configurator.test;

import junit.framework.TestCase;

import org.contract4j5.Contract;
import org.contract4j5.ContractError;
import org.contract4j5.Pre;
import org.contract4j5.Contract4J;
import org.contract4j5.TestSpecificationError;

public class DefaultConfigurationWhenNoneUsedExplicitlyTest extends TestCase {
	Contract4J c4j;
	
	protected void setUp() throws Exception {
		super.setUp();
		c4j = new Contract4J();
	}
	
	public void testConfigurationOccursWhenContract4JStateInfoIsQueried() {
		assertEquals (org.contract4j5.configurator.PropertiesConfigurator.class,
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
			new ErrorClass("foo");
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			// expected
		}
	}
}
