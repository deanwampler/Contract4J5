package org.contract4j5.aspects.type.test;

import junit.framework.TestCase;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.errors.ContractError;
import org.contract4j5.util.Valid;
import org.contract4j5.util.Validatable;

public class TypeInvariantWithGenericsTest extends TestCase {
	@Contract 
	@Invar("$this.validatable != null && $this.validatable.valid()")
	class GenericTestClass<T extends Validatable> {
		private T validatable;
		public Validatable getValidatable() { return validatable; }
		public void setValidatable(T t) { this.validatable = t; }

		public GenericTestClass(T t) {
			validatable = t;
		}
	}
	
	public void testTypeInvariantCatchesFieldSetsWithNullParameter() {
		try {
			GenericTestClass<Valid> gtc = new GenericTestClass<Valid>(new Valid("bar"));
			gtc.setValidatable(new Valid(null));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testTypeInvariantCatchesFieldSetsWithEmptyParameter() {
		try {
			GenericTestClass<Valid> gtc = new GenericTestClass<Valid>(new Valid("bar"));
			gtc.setValidatable(new Valid(""));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testTypeInvariantAllowsFieldSetsWithValidParameter() {
		GenericTestClass<Valid> gtc = new GenericTestClass<Valid>(new Valid("bar"));
		gtc.setValidatable(new Valid("foo"));
	}
}
