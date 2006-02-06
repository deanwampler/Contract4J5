package org.contract4j5.test;

import junit.framework.TestCase;

import org.contract4j5.Contract;
import org.contract4j5.ContractError;
import org.contract4j5.Invar;
import org.contract4j5.aspects.Contract4J;

public class TypeInvarTest extends TestCase {
	@Contract
	@Invar  // default test (nothing!)
	public static class TypeInvarWithDefaultExpr {
		String name = null;
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }		

		int i = 0;
		public int getI() {	return i; }
		public void setI(int i) { this.i = i; }		
		
		public TypeInvarWithDefaultExpr (String name, int i) {
			this.name = name;
			this.i = i;
		}
	}
	
	@Contract
	@Invar ("$this.name != null && $this.i > 0") 
	public static class TypeInvarWithDefinedExpr {
		String name = null;
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }		

		int i = 0;
		public int getI() {	return i; }
		public void setI(int i) { this.i = i; }		
		
		public TypeInvarWithDefinedExpr (String name, int i) {
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
		TypeInvarWithDefaultExpr t = 
			doTestCtorWithDefault(false, "foo", 1);  // Won't pass if empty test strings, but args irrelevant
		t = doTestCtorWithDefault(false, null,  1); 
		t = doTestCtorWithDefault(false, "foo", 0); 
		t = doTestCtorWithDefault(false, null,  0); 
		// Now temporarily disable the test failure when the test is empty so we can
		// successfully create an object for subsequent tests.
		Contract4J.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(true);
		t = new TypeInvarWithDefaultExpr("foo", 1);  // Will pass if empty test strings.
		Contract4J.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(false);
		doTestSetIWithDefault(t, false);
		doTestSetNameWithDefault(t, false);
	}
	public void testCtorWithDefaultTrue() {
		Contract4J.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(true);
		TypeInvarWithDefaultExpr t = 
			doTestCtorWithDefault(true, "foo", 1);  // Will pass if empty test strings.
		t = doTestCtorWithDefault(true, null,  1); 
		t = doTestCtorWithDefault(true, "foo", 0); 
		t = doTestCtorWithDefault(true, null,  0); 
		doTestSetIWithDefault(t, true);
		doTestSetNameWithDefault(t, true);
	}
	protected TypeInvarWithDefaultExpr doTestCtorWithDefault (boolean shouldPass, String name, int i) {
		try {
			TypeInvarWithDefaultExpr t = new TypeInvarWithDefaultExpr(name, i);
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
	protected void doTestSetIWithDefault (TypeInvarWithDefaultExpr t, boolean shouldPass) {
		try {
			t.setI(0);
			if (!shouldPass) {
				fail();
			}
		} catch (ContractError ce) {
			if (shouldPass) {
				fail();
			}
		}
	}
	protected void doTestSetNameWithDefault (TypeInvarWithDefaultExpr t, boolean shouldPass) {
		try {
			t.setName(null);
			if (!shouldPass) {
				fail();
			}
		} catch (ContractError ce) {
			if (shouldPass) {
				fail();
			}
		}
	}
	
	public void testDefined() {
		TypeInvarWithDefinedExpr t = null;
		try {
			t = new TypeInvarWithDefinedExpr(null, 1);
			fail();
		} catch (ContractError ce) {
		}
		try {
			t = new TypeInvarWithDefinedExpr("foo", 0);
			fail();
		} catch (ContractError ce) {
		}
		try {
			t = new TypeInvarWithDefinedExpr(null, 0);
			fail();
		} catch (ContractError ce) {
		}
		t = new TypeInvarWithDefinedExpr("foo", 1);
		try {
			t.setI(0);
			fail();
		} catch (ContractError ce) {
		}
		try {
			t.setName(null);
			fail();
		} catch (ContractError ce) {
		}
	}
}
