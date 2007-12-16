package org.contract4j5.aspects.constructor.test;

import java.util.ArrayList;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.errors.ContractError;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.util.SystemUtils;

import junit.framework.TestCase;

/**
 * You can reference a class' own static method if you prefix with $this. However,
 * this doesn't appear to work with JRuby.
 * @author deanwampler
 *
 */
public class ConstructorBoundaryExpressionTest extends TestCase {
	public void testCanUseDollarThisDotClassStaticMethodInPreconditionsToFailTest() {
		if (SystemUtils.isJRuby())
			return;
		ArrayList<String> arrayList = new ArrayList<String>();
		try {
			new ConstructorBoundaries(arrayList);
			fail();
		} catch (ContractError ce) {}
	}
	
	public void testCanUseDollarThisDotClassStaticMethodInPreconditionsToPassTest() {
		if (SystemUtils.isJRuby())
			return;
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("1");
		arrayList.add("2");
		arrayList.add("3");
		new ConstructorBoundaries(arrayList);
	}
	
	@Contract
	class ClassWithBadContractExpressions {
		@Pre(" > 0")
		public ClassWithBadContractExpressions(int i) {}
		@Post(" > 0.0f")
		public ClassWithBadContractExpressions(float f) {}
	}
	
	// May fail with either a TestSpecificationError or ContractError, depending on the interpreter used!
	public void testBadPreconditionContractExpressionsFail() {
		try {
			new ClassWithBadContractExpressions(1);
			fail();  
		} catch (ContractError ce) {
		}		
	}
	
	public void testBadPostconditionContractExpressionsFail() {
		try {
			new ClassWithBadContractExpressions(1.0f);
			fail();  
		} catch (ContractError ce) {
		}		
	}

}
