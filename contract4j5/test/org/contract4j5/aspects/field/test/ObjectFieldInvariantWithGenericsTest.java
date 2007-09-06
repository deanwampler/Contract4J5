package org.contract4j5.aspects.field.test;

import junit.framework.TestCase;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.errors.ContractError;
import org.contract4j5.util.Valid;
import org.contract4j5.util.Validatable;

public class ObjectFieldInvariantWithGenericsTest extends TestCase {
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
	
	public void testInvariantCatchesFieldSetsWithNullParameter() {
		try {
			GenericTestClass<Valid> gtc = new GenericTestClass<Valid>(new Valid("bar"));
			gtc.setValidatable(new Valid(null));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testInvariantCatchesFieldSetsWithEmptyParameter() {
		try {
			GenericTestClass<Valid> gtc = new GenericTestClass<Valid>(new Valid("bar"));
			gtc.setValidatable(new Valid(""));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testInvariantAllowsFieldSetsWithValidParameter() {
		GenericTestClass<Valid> gtc = new GenericTestClass<Valid>(new Valid("bar"));
		gtc.setValidatable(new Valid("foo"));
	}
}
