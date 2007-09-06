package org.contract4j5.aspects.constructor.test;

import junit.framework.TestCase;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.errors.ContractError;
import org.contract4j5.util.Valid;
import org.contract4j5.util.Validatable;

public class ConstructorFieldInvariantWithGenericsTest extends TestCase {
	@Contract 
	static class GenericTestClass<T extends Validatable> {
		@Invar ("validatable != null && validatable.valid()")
		public Validatable validatable = null;
		public Validatable getValidatable() { return validatable; }
		
		public GenericTestClass(T t) { 
			validatable = t; 
		}
	}
	
	public void testFieldInvariantCatchesConstructorCallWithNullParameter() {
		try {
			new GenericTestClass<Valid>(null);
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testFieldInvariantCatchesConstructorCallWithEmptyParameter() {
		try {
			new GenericTestClass<Valid>(new Valid(""));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testFieldInvariantAllowsConstructorCallWithValidParameter() {
		new GenericTestClass<Valid>(new Valid("foo"));
	}
}
