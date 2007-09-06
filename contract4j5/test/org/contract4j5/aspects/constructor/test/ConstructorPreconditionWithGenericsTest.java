package org.contract4j5.aspects.constructor.test;

import junit.framework.TestCase;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Pre;
import org.contract4j5.errors.ContractError;
import org.contract4j5.util.Valid;
import org.contract4j5.util.Validatable;

/**
 * Confirms that a constructor precondition works correctly where the constructor takes a parameterized (generic) object.
 * Based on a user's query...
 */
public class ConstructorPreconditionWithGenericsTest extends TestCase {
	@Contract 
	class GenericTestClass<T extends Validatable> {
		@Pre ("t != null && t.valid()")
		public GenericTestClass(T t) {}
	}
	
	public void testPreconditionCatchesConstructorCallWithNullParameter() {
		try {
			new GenericTestClass<Valid>(new Valid(null));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testPreconditionCatchesConstructorCallWithEmptyParameter() {
		try {
			new GenericTestClass<Valid>(new Valid(""));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testPreconditionAllowsConstructorCallWithValidParameter() {
		new GenericTestClass<Valid>(new Valid("foo"));
	}
}
