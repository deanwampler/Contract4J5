package org.contract4j5.aspects.method.test;

import junit.framework.TestCase;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.errors.ContractError;
import org.contract4j5.util.Valid;
import org.contract4j5.util.Validatable;

public class MethodInvariantWithGenericsTest extends TestCase {
	@Contract 
	class GenericTestClass<T extends Validatable> {
		private T validatable;
		public Validatable getValidatable() { return validatable; }

		public GenericTestClass(T t) {
			validatable = t;
		}

		@Invar ("$this.validatable != null && $this.validatable.valid()")
		public void setValidatable(T t) { this.validatable = t; }
	}
	
	public void testInvariantCatchesMethodCallWithNullParameter() {
		try {
			GenericTestClass<Valid> gtc = new GenericTestClass<Valid>(new Valid("bar"));
			gtc.setValidatable(new Valid(null));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testInvariantCatchesMethodCallWithEmptyParameter() {
		try {
			GenericTestClass<Valid> gtc = new GenericTestClass<Valid>(new Valid("bar"));
			gtc.setValidatable(new Valid(""));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testInvariantAllowsMethodCallWithValidParameter() {
		GenericTestClass<Valid> gtc = new GenericTestClass<Valid>(new Valid("bar"));
		gtc.setValidatable(new Valid("foo"));
	}
}
