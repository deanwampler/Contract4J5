package org.contract4j5.aspects.constructor.test;

import junit.framework.TestCase;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Post;
import org.contract4j5.errors.ContractError;
import org.contract4j5.util.Valid;
import org.contract4j5.util.Validatable;

/**
 * Confirms that a constructor postcondition works correctly where the constructor takes a parameterized (generic) object.
 */
public class ConstructorPostconditionWithGenericsTest extends TestCase {
	@Contract 
	class GenericTestClass<T extends Validatable> {
		Validatable validatable;
		public Validatable getValidatable() { return validatable; }
		
		@Post ("$this.validatable != null && $this.validatable.valid()")
		public GenericTestClass(T t) { validatable = t; }
	}
	
	public void testPostconditionCatchesConstructorCallWithNullParameter() {
		try {
			new GenericTestClass<Valid>(new Valid(null));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testPostconditionCatchesConstructorCallWithEmptyParameter() {
		try {
			new GenericTestClass<Valid>(new Valid(""));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testPostconditionAllowsConstructorCallWithValidParameter() {
		new GenericTestClass<Valid>(new Valid("foo"));
	}
}
