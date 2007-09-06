package org.contract4j5.aspects.method.test;

import junit.framework.TestCase;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Pre;
import org.contract4j5.errors.ContractError;
import org.contract4j5.util.Valid;
import org.contract4j5.util.Validatable;

public class MethodPreconditionWithGenericsTest extends TestCase {
	@Contract 
	class GenericTestClass<T extends Validatable> {
		private T validatable;
		public Validatable getValidatable() { return validatable; }

		@Pre ("t != null && t.valid()")
		public void setValidatable(T t) { this.validatable = t; }
	}
	
	public void testPreconditionCatchesMethodCallWithNullParameter() {
		try {
			GenericTestClass<Valid> gtc = new GenericTestClass<Valid>();
			gtc.setValidatable(new Valid(null));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testPreconditionCatchesMethodCallWithEmptyParameter() {
		try {
			GenericTestClass<Valid> gtc = new GenericTestClass<Valid>();
			gtc.setValidatable(new Valid(""));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testPreconditionAllowsMethodCallWithValidParameter() {
		GenericTestClass<Valid> gtc = new GenericTestClass<Valid>();
		gtc.setValidatable(new Valid("foo"));
	}
}
