package org.contract4j5.aspects.method.test;

import junit.framework.TestCase;

import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.errors.ContractError;
import org.contract4j5.util.SystemUtils;
import org.contract4j5.util.Valid;
import org.contract4j5.util.Validatable;

public class MethodInvariantWithGenericsTest extends TestCase {
	private GenericTestClass<Valid> gtc;

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
	
	class Valid2 extends Valid {
		public Valid2(String name) {
			super(name);
		}
		@Override
		public boolean valid() {
			return true;
			//			if (name.equals("foo"))
//				throw new RuntimeException("boo!");
//			return super.valid();
		}
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		new ConfiguratorForTesting().configure();
		gtc = new GenericTestClass<Valid>(new Valid("bar"));
	}

	public void testInvariantCatchesMethodCallWithNullParameter() {
		try {
			gtc.setValidatable(new Valid(null));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testInvariantCatchesMethodCallWithEmptyParameter() {
		try {
			gtc.setValidatable(new Valid(""));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testInvariantAllowsMethodCallWithValidParameter() {
		try {
			gtc.setValidatable(new Valid("foo"));
			if (SystemUtils.isJexl()) fail();
		} catch (ContractError ce) {
			if (!SystemUtils.isJexl())  fail();
		}
	}
}
