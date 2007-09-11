package org.contract4j5.aspects.method.test;

import junit.framework.TestCase;

import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Post;
import org.contract4j5.errors.ContractError;
import org.contract4j5.util.SystemUtils;
import org.contract4j5.util.Valid;
import org.contract4j5.util.Validatable;

/**
 * Jexl doesn't seem to support generics.
 */
public class MethodPostconditionWithGenericsTest extends TestCase {
	private GenericTestClass<Valid> gtc;

	@Contract 
	class GenericTestClass<T extends Validatable> {
		private T validatable;
		public Validatable getValidatable() { return validatable; }

		@Post ("$this.validatable != null && $this.validatable.valid()")
		public void setValidatable(T t) { this.validatable = t; }
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		new ConfiguratorForTesting().configure();
		gtc = new GenericTestClass<Valid>();
	}
	
	public void testPostconditionCatchesMethodCallWithNullParameter() {
		try {
			gtc.setValidatable(new Valid(null));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testPostconditionCatchesMethodCallWithEmptyParameter() {
		try {
			gtc.setValidatable(new Valid(""));
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testPostconditionAllowsMethodCallWithValidParameter() {
		if (!SystemUtils.isJexl())
			gtc.setValidatable(new Valid("foo"));
	}
}
