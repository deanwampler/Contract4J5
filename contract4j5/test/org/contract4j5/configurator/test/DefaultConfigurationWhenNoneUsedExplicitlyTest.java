package org.contract4j5.configurator.test;

import junit.framework.TestCase;
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
	}	

}
