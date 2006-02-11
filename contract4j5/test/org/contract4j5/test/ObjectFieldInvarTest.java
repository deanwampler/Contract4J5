package org.contract4j5.test;

import junit.framework.TestCase;

import org.contract4j5.Contract;
import org.contract4j5.ContractError;
import org.contract4j5.Invar;
import org.contract4j5.aspects.Contract4J;
import org.contract4j5.aspects.InvariantConditions;
import org.contract4j5.testexpression.DefaultFieldInvarTestExpressionMaker;
import org.contract4j5.testexpression.DefaultTestExpressionMaker;
import org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker;

/**
 * Test field invariants when the field is a primitive.
 * @author Dean Wampler
 */
public class ObjectFieldInvarTest extends TestCase {
	@Contract
	public static class FieldInvarWithDefaultExpr {
		@Invar 
		private String name = null;
		public String getName() {	
			nameRW = false;
			String n2 = name;
			nameRW = true;
			return n2;
		}
		public void setName(String n) { 
			nameRW = false;
			this.name = n; 
			nameRW = true;
		}		
		public boolean nameRW = false;
		
		public FieldInvarWithDefaultExpr (String n) {
			this.name = n;
			this.nameRW = false;
		}
	}
	
	@Contract
	public static class FieldInvarWithDefinedExpr {
		@Invar ("name != null && !name.equals(\"bad\")") 
		private String name = null;
		public String getName() {	
			nameRW = false;
			String n2 = name;
			nameRW = true;
			return n2;
		}
		public void setName(String n) { 
			nameRW = false;
			this.name = n; 
			nameRW = true;
		}		
		public boolean nameRW = false;
		
		public FieldInvarWithDefinedExpr (String n) {
			this.name = n;
			this.nameRW = false;
		}
	}

	FieldInvarWithDefaultExpr[] fdefault = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ManualSetup.wireC4J();
		initEnv (true, true);
		// Construct the array of "default" objects.
		fdefault = new FieldInvarWithDefaultExpr[] {
				new FieldInvarWithDefaultExpr (null),
				new FieldInvarWithDefaultExpr ("foo"),
				new FieldInvarWithDefaultExpr ("bar")
		};
	}

	/**
	 * We must override tearDown() to reset the global static expression makers
	 * to the normal defaults. Otherwise, while running a set of test cases, 
	 * subsequent cases may fail because the static makers have unexpected,
	 * non-default values!
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		initEnv (false, false);
	}
	
	private void initEnv (boolean empty, boolean allowed) {
		DefaultTestExpressionMaker maker = empty ?
				new SimpleStringDefaultTestExpressionMaker() :
				new DefaultFieldInvarTestExpressionMaker();
		InvariantConditions.InvariantFieldConditions.setDefaultFieldInvarTestExpressionMaker(maker);
		InvariantConditions.InvariantFieldCtorConditions.setDefaultFieldInvarTestExpressionMaker(maker);
		Contract4J.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(allowed);
	}
	
	public void testDefaultCtor() {
		doTestDefaultCtor (true,  true);
		doTestDefaultCtor (true,  false);
		doTestDefaultCtor (false, true);
		doTestDefaultCtor (false, false);
	}	
	public void testDefaultGet() {
		doTestDefaultGet (true,  true);
		doTestDefaultGet (true,  false);
		doTestDefaultGet (false, true);
		doTestDefaultGet (false, false);
	}	
	public void testDefaultSet() {
		doTestDefaultSet (true,  true);
		doTestDefaultSet (true,  false);
		doTestDefaultSet (false, true);
		doTestDefaultSet (false, false);
	}	
	
	private static final String names[] = new String[] {
		null, "foo", "bad"
	};
	
	public void doTestDefaultCtor (boolean empty, boolean allowed) {
		initEnv (empty, allowed);
		FieldInvarWithDefaultExpr t = null; 
		for (int i = 0; i < 3; i++) {
			// If i=0 (name used is "null"), then tests should pass if empty && allowed only.
			// Otherwise, tests should pass if !empty || allowed.
			boolean pass = i == 0 ? (empty && allowed) : (!empty || allowed);
			String msg = String.format("name=%s, should pass? %b (empty? %b, allowed? %b)", names[i], pass, empty, allowed);
			try {
				t = new FieldInvarWithDefaultExpr(names[i]);
				if (!pass) {
					fail(msg);
				}
			} catch (ContractError ce) {
				if (pass) {
					fail(msg);
				}
			}
			if (pass) {  // Only test if passed; otherwise "t" is null!
				assertFalse(msg, t.nameRW);
			}
		}
	}
	public void doTestDefaultSet (boolean empty, boolean allowed) {
		initEnv (empty, allowed);
		FieldInvarWithDefaultExpr t = null; 
		for (int i = 0; i < 3; i++) {
			// If i=0 (name used is "null"), then tests should pass if empty && allowed only.
			// Otherwise, tests should pass if !empty || allowed.
			boolean pass = i == 0 ? (empty && allowed) : (!empty || allowed);
			String msg = String.format("name=%s, should pass? %b (empty? %b, allowed? %b)", names[i], pass, empty, allowed);
			try {
				t = fdefault[i];
				t.setName(names[i]);
				if (!pass) {
					fail(msg);
				}
			} catch (ContractError ce) {
				if (pass) {
					fail(msg);
				}
			}
			if (pass) {  // Only test if passed; otherwise "t" is null!
				assertTrue(msg, t.nameRW);
			}
		}
	}
	public void doTestDefaultGet (boolean empty, boolean allowed) {
		initEnv (empty, allowed);
		FieldInvarWithDefaultExpr t = null; 
		for (int i = 0; i < 3; i++) {
			// If i=0 (name used is "null"), then tests should pass if empty && allowed only.
			// Otherwise, tests should pass if !empty || allowed.
			boolean pass = i == 0 ? (empty && allowed) : (!empty || allowed);
			String msg = String.format("name=%s, should pass? %b (empty? %b, allowed? %b)", names[i], pass, empty, allowed);
			try {
				t = fdefault[i];
				t.getName();
				if (!pass) {
					fail(msg);
				}
			} catch (ContractError ce) {
				if (pass) {
					fail(msg);
				}
			}
			if (pass) {  // Only test if passed; otherwise "t" is null!
				assertTrue(msg, t.nameRW);
			}
		}
	}

	public void testDefinedCtor () {
		for (int i = 0; i < 3; i++) {
			try {
				new FieldInvarWithDefinedExpr (names[i]);
				if (i != 1) {  // should fail for case 0 and 2
					fail("i="+i+", name=\""+names[i]+"\"");
				}
			} catch (ContractError ce) {
				if (i == 1) {
					fail("i="+i+", name=\""+names[i]+"\"");
				}
			}
		}
	}
	public void testDefinedSetFail () {
		FieldInvarWithDefinedExpr t = 
			new FieldInvarWithDefinedExpr (names[1]);
		try {
			t.setName(null);
			fail();
		} catch (ContractError ce) {
		}
		try {
			t.setName("bad");
			fail();
		} catch (ContractError ce) {
		}
	}
	public void testDefinedSetPass () {
		FieldInvarWithDefinedExpr t = 
			new FieldInvarWithDefinedExpr (names[1]);
		try {
			t.setName("foo2");
		} catch (ContractError ce) {
			fail();
		}
	}

	public void testDefinedGetPass () {
		FieldInvarWithDefinedExpr t = 
			new FieldInvarWithDefinedExpr (names[1]);
		try {
			t.getName();
		} catch (ContractError ce) {
			fail();
		}
	}

}
