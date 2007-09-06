package org.contract4j5.aspects.method.test;

import junit.framework.TestCase;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Post;
import org.contract4j5.errors.ContractError;
import org.contract4j5.util.Valid;
import org.contract4j5.util.Validatable;

public class MethodPostconditionWithGenericsTest extends TestCase {
	@Contract 
	class GenericTestClass<T extends Validatable> {
		private T validatable;
		public Validatable getValidatable() { return validatable; }

		@Post ("$this.validatable != null && $this.validatable.valid()")
		public void setValidatable(T t) { this.validatable = t; }
	}
	
	public void testPostconditionCatchesMethodCallWithNullParameter() {
		try {
			GenericTestClass<Valid> gtc = new GenericTestClass<Valid>();
			gtc.setValidatable(new Valid(null));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testPostconditionCatchesMethodCallWithEmptyParameter() {
		try {
			GenericTestClass<Valid> gtc = new GenericTestClass<Valid>();
			gtc.setValidatable(new Valid(""));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testPostconditionAllowsMethodCallWithValidParameter() {
		GenericTestClass<Valid> gtc = new GenericTestClass<Valid>();
		gtc.setValidatable(new Valid("foo"));
	}
}
