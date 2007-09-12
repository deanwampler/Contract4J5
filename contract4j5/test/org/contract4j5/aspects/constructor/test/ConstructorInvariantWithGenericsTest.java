package org.contract4j5.aspects.constructor.test;

import junit.framework.TestCase;

import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.errors.ContractError;
import org.contract4j5.util.SystemUtils;
import org.contract4j5.util.Valid;
import org.contract4j5.util.Validatable;

/**
 * Jexl and JRuby don't seem seem to support generics.
 */
public class ConstructorInvariantWithGenericsTest extends TestCase {
	@Contract 
	static class GenericTestClass<T extends Validatable> {
		public Validatable validatable = null;
		public Validatable getValidatable() { return validatable; }
		
		@Invar ("$this.validatable != null && $this.validatable.valid()")
		public GenericTestClass(T t) { 
			validatable = t; 
		}
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (SystemUtils.isGroovy())
			new ConfiguratorForTesting().configure();
	}
	
	public void testGroovyInvariantCatchesConstructorCallWithNullParameter() {
		if (!SystemUtils.isGroovy())
			return;
		try {
			new GenericTestClass<Valid>(null);
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testGroovyInvariantCatchesConstructorCallWithEmptyParameter() {
		if (!SystemUtils.isGroovy())
			return;
		try {
			new GenericTestClass<Valid>(new Valid(""));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testGroovyInvariantAllowsConstructorCallWithValidParameterOnlyForGroovy() {
		if (!SystemUtils.isGroovy())
			return;
		new GenericTestClass<Valid>(new Valid("foo"));
	}
}
