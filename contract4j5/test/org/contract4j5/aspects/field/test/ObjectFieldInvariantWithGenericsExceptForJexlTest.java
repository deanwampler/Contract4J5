package org.contract4j5.aspects.field.test;

import junit.framework.TestCase;

import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.errors.ContractError;
import org.contract4j5.util.SystemUtils;
import org.contract4j5.util.Valid;
import org.contract4j5.util.Validatable;

/**
 * Jexl doesn't seem to support generics.
 */
public class ObjectFieldInvariantWithGenericsExceptForJexlTest extends TestCase {
	private GenericTestClass<Valid> gtc;

	@Contract 
	class GenericTestClass<T extends Validatable> {
		@Invar("validatable != null && validatable.valid()")
		private T validatable;
		public Validatable getValidatable() { return validatable; }
		public void setValidatable(T t) { this.validatable = t; }

		public GenericTestClass(T t) {
			validatable = t;
		}
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		new ConfiguratorForTesting().configure();
	}
	
	public void testInvariantCatchesFieldSetsWithNullParameter() {
		try {
			gtc = new GenericTestClass<Valid>(new Valid("bar"));
			gtc.setValidatable(new Valid(null));
			fail();
		} catch (ContractError ce) {}
	}
	
	public void testInvariantCatchesFieldSetsWithEmptyParameter() {
		try {
			gtc = new GenericTestClass<Valid>(new Valid("bar"));
			gtc.setValidatable(new Valid(""));
			fail();
		} catch (ContractError ce) {}
	}
	
	public void testInvariantAllowsFieldSetsWithValidParameterExceptForJexl() {
		try {
			gtc = new GenericTestClass<Valid>(new Valid("bar"));
			gtc.setValidatable(new Valid("foo"));
			if (SystemUtils.isJexl()) fail();
		} catch (ContractError ce) {
			if (!SystemUtils.isJexl()) fail();
		}
	}
}