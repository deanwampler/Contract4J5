package org.contract4j5.test;

import junit.framework.TestCase;

import org.contract4j5.Contract;
import org.contract4j5.ContractError;
import org.contract4j5.Invar;
import org.contract4j5.aspects.Contract4J;

public class ConstructorInvarTest extends TestCase {
	@Contract
	public static class ConstructorInvarWithDefaultExpr {
		String name = null;
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }		

		int i = 0;
		public int getI() {	return i; }
		public void setI(int i) { this.i = i; }		
		
		@Invar  // default test (nothing!)
		public ConstructorInvarWithDefaultExpr (String name, int i) {
			this.name = name;
			this.i = i;
		}
	}
	
	@Contract
	public static class ConstructorInvarWithDefinedExpr {
		String name = null;
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }		

		int i = 0;
		public int getI() {	return i; }
		public void setI(int i) { this.i = i; }		
		
		@Invar ("$this.name != null && $this.i > 0") 
		public ConstructorInvarWithDefinedExpr (String name, int i) {
			this.name = name;
			this.i = i;
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ManualSetup.wireC4J();
	}
	
	public void testWithDefaultFalse() {
		ConstructorInvarWithDefaultExpr t = 
			doTestCtorWithDefault(false, "foo", 1);  // Won't pass if empty test strings, but args irrelevant
		t = doTestCtorWithDefault(false, null,  1); 
		t = doTestCtorWithDefault(false, "foo", 0); 
		t = doTestCtorWithDefault(false, null,  0); 
		// In order to test the setters, must construct an object while the empty-test issue is bypassed!
		Contract4J.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(true);
		t = doTestCtorWithDefault(true, "foo", 1);  // Won't pass if empty test strings, but args irrelevant
		doTestSetIWithDefault(t);
		doTestSetNameWithDefault(t);
	}
	public void testWithDefaultTrue() {
		Contract4J.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(true);
		ConstructorInvarWithDefaultExpr t = 
			doTestCtorWithDefault(true, "foo", 1);  // Won't pass if empty test strings, but args irrelevant
		t = doTestCtorWithDefault(true, null,  1); 
		t = doTestCtorWithDefault(true, "foo", 0); 
		t = doTestCtorWithDefault(true, null,  0); 
		doTestSetIWithDefault(t);
		doTestSetIWithDefault(t);
		doTestSetNameWithDefault(t);
	}
	protected ConstructorInvarWithDefaultExpr doTestCtorWithDefault (boolean shouldPass, String name, int i) {
		try {
			ConstructorInvarWithDefaultExpr t = new ConstructorInvarWithDefaultExpr(name, i);
			if (!shouldPass) {
				fail();
			}
			return t;
		} catch (ContractError ce) {
			if (shouldPass) {
				fail();
			}
		}
		return null;
	}
	protected void doTestSetIWithDefault (ConstructorInvarWithDefaultExpr t) {
		try {
			t.setI(0);
		} catch (ContractError ce) {
			fail();
		}
	}
	protected void doTestSetNameWithDefault (ConstructorInvarWithDefaultExpr t) {
		try {
			t.setName(null);
		} catch (ContractError ce) {
			fail();
		}
	}
	
	public void testDefined() {
		ConstructorInvarWithDefinedExpr t = null;
		try {
			t = new ConstructorInvarWithDefinedExpr(null, 1);
			fail();
		} catch (ContractError ce) {
		}
		try {
			t = new ConstructorInvarWithDefinedExpr("foo", 0);
			fail();
		} catch (ContractError ce) {
		}
		try {
			t = new ConstructorInvarWithDefinedExpr(null, 0);
			fail();
		} catch (ContractError ce) {
		}
		try {
			t = new ConstructorInvarWithDefinedExpr("foo", 1);
		} catch (ContractError ce) {
			fail();
		}
		try {
			t.setI(0);
		} catch (ContractError ce) {
			fail();
		}
		try {
			t.setName(null);
		} catch (ContractError ce) {
			fail();
		}
	}
}
