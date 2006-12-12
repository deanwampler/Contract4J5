/*
 * Copyright 2005, 2006 Dean Wampler. All rights reserved.
 * http://www.aspectprogramming.com
 *
 * Licensed under the Eclipse Public License - v 1.0; you may not use this
 * software except in compliance with the License. You may obtain a copy of the 
 * License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * A copy is also included with this distribution. See the "LICENSE" file.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */

package org.contract4j5.test;

import junit.framework.TestCase;

import org.contract4j5.aspects.InvariantFieldConditions;
import org.contract4j5.aspects.InvariantFieldCtorConditions;
import org.contract4j5.configurator.Configurator;
import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.errors.ContractError;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.testexpression.DefaultFieldInvarTestExpressionMaker;
import org.contract4j5.testexpression.DefaultTestExpressionMaker;
import org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker;

/**
 * Test field invariants when the field is a primitive.
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

	FieldInvarWithDefaultExpr[] fdefault;
	Contract4J c4j;
//	DefaultTestExpressionMaker savedMaker =
//		InvariantFieldCtorConditions.getStaticDefaultFieldInvarTestExpressionMaker();
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Configurator c = new ConfiguratorForTesting();
		c.configure();
		c4j = Contract4J.getInstance();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		// Reset this one static field change!
		initExpressionMakersAndEmptyTestFlag(false, false);
//		InvariantFieldCtorConditions.setStaticDefaultFieldInvarTestExpressionMaker(savedMaker);
	}
	
	private void initExpressionMakersAndEmptyTestFlag (boolean empty, boolean allowed) {
		DefaultTestExpressionMaker maker = empty ?
				new SimpleStringDefaultTestExpressionMaker() :
				new DefaultFieldInvarTestExpressionMaker();
		InvariantFieldConditions.aspectOf().setDefaultFieldInvarTestExpressionMaker(maker);
		InvariantFieldCtorConditions.aspectOf().setDefaultFieldInvarTestExpressionMaker(maker);
		c4j.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(allowed);
	}
	
	private void createObjects() {
		// Construct the array of "default" objects.
		fdefault = new FieldInvarWithDefaultExpr[] {
				new FieldInvarWithDefaultExpr (null),
				new FieldInvarWithDefaultExpr ("foo"),
				new FieldInvarWithDefaultExpr ("bar")
		};
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
		initExpressionMakersAndEmptyTestFlag(empty, allowed);
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
			} catch (TestSpecificationError tse) {
				if (empty && allowed)
					fail(msg);
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
		specialInit(empty, allowed);
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
			} catch (TestSpecificationError tse) {
				if (empty && allowed)
					fail(msg);
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

	private void specialInit(boolean empty, boolean allowed) {
		// Allow anything to be created...
		initExpressionMakersAndEmptyTestFlag(true, true);
		createObjects();
		// ... now set fields as desired.
		initExpressionMakersAndEmptyTestFlag(empty, allowed);
	}

	public void doTestDefaultGet (boolean empty, boolean allowed) {
		specialInit(empty, allowed);
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
			} catch (TestSpecificationError tse) {
				if (empty && allowed)
					fail(msg);
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
		initExpressionMakersAndEmptyTestFlag(true, true);
		//createObjects();
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
		initExpressionMakersAndEmptyTestFlag(true, true);
		createObjects();
		FieldInvarWithDefinedExpr t = 
			new FieldInvarWithDefinedExpr (names[1]);
		try {
			t.setName(null);
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
		}
		try {
			t.setName("bad");
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
		}
	}

	public void testDefinedSetPass () {
		initExpressionMakersAndEmptyTestFlag(true, true);
		createObjects();
		FieldInvarWithDefinedExpr t = 
			new FieldInvarWithDefinedExpr (names[1]);
		try {
			t.setName("foo2");
		} catch (ContractError ce) {
			fail();
		}
	}

	public void testDefinedGetPass () {
		initExpressionMakersAndEmptyTestFlag(true, true);
		createObjects();
		FieldInvarWithDefinedExpr t = 
			new FieldInvarWithDefinedExpr (names[1]);
		try {
			t.getName();
		} catch (ContractError ce) {
			fail();
		}
	}
}
