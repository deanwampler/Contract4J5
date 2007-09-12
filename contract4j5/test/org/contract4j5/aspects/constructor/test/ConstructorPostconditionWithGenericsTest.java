package org.contract4j5.aspects.constructor.test;

import junit.framework.TestCase;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Post;
import org.contract4j5.errors.ContractError;
import org.contract4j5.util.SystemUtils;
import org.contract4j5.util.Valid;
import org.contract4j5.util.Validatable;

/**
 * Confirms that a constructor postcondition works correctly where the constructor takes a parameterized (generic) object.
 * It appears that Jexl can't handle generics.
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
		if (SystemUtils.isJexl()) return;
		try {
			new GenericTestClass<Valid>(new Valid(null));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testPostconditionCatchesConstructorCallWithEmptyParameter() {
		if (SystemUtils.isJexl()) return;
		try {
			new GenericTestClass<Valid>(new Valid(""));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testPostconditionAllowsConstructorCallWithValidParameter() {
		if (SystemUtils.isJexl()) return;
		new GenericTestClass<Valid>(new Valid("foo"));
	}
}
