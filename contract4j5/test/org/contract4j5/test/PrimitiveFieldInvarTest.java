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
 */
public class PrimitiveFieldInvarTest extends TestCase {
	@Contract
	public static class FieldInvarWithDefaultExpr {
		@Invar 
		private int i = 0;
		public int getI() {	
			iRW = false;
			int i2 = i;
			iRW = true;
			return i2;
		}
		public void setI(int i) { 
			iRW = false;
			this.i = i; 
			iRW = true;
		}		
		public boolean iRW = false;
		
		public FieldInvarWithDefaultExpr (int i) {
			this.i = i;
			this.iRW = false;
		}
	}
	
	@Contract
	public static class FieldInvarWithDefinedExpr {
		@Invar ("i > 0") 
		public int i = 0;
		public int getI() {	
			iRW = false;
			int i2 = i;
			iRW = true;
			return i2;
		}
		public void setI(int i) { 
			iRW = false;
			this.i = i; 
			iRW = true;
		}		
		public boolean iRW = false;
		
		public FieldInvarWithDefinedExpr (int i) {
			this.i = i;
			this.iRW = false;
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
				new FieldInvarWithDefaultExpr (0),
				new FieldInvarWithDefaultExpr (1),
				new FieldInvarWithDefaultExpr (2)
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
	
	// setI will fail if allowed == false, because the test expression will always
	// be empty, no matter what "empty" equals, since "i" is primitive.
	public void doTestDefaultCtor (boolean empty, boolean allowed) {
		initEnv (empty, allowed);
		boolean pass = allowed;
		FieldInvarWithDefaultExpr t = null; 
		for (int i = 0; i < 3; i++) {
			String msg = String.format("i=%d, empty? %b, allowed? %b", i, empty, allowed);
			try {
				t = new FieldInvarWithDefaultExpr(i);
				if (!pass) {
					fail(msg);
				}
			} catch (ContractError ce) {
				if (pass) {
					fail(msg);
				}
			}
			if (pass) {  // Only test if passed; otherwise "t" is null!
				assertFalse(msg, t.iRW);
			}
		}
	}
	// setI will fail if allowed == false, because the test expression will always
	// be empty, no matter what "empty" equals, since "i" is primitive.
	public void doTestDefaultSet (boolean empty, boolean allowed) {
		initEnv (empty, allowed);
		boolean pass = allowed;
		FieldInvarWithDefaultExpr t = null; 
		for (int i = 0; i < 3; i++) {
			String msg = String.format("i=%d, empty? %b, allowed? %b", i, empty, allowed);
			try {
				t = fdefault[i];
				t.setI(i);
				if (!pass) {
					fail(msg);
				}
			} catch (ContractError ce) {
				if (pass) {
					fail(msg);
				}
			}
			if (pass) {  // Only test if passed; otherwise "t" is null!
				assertTrue(msg, t.iRW);
			}
		}
	}
	// setI will fail if allowed == false, because the test expression will always
	// be empty, no matter what "empty" equals, since "i" is primitive.
	public void doTestDefaultGet (boolean empty, boolean allowed) {
		initEnv (empty, allowed);
		boolean pass = allowed;
		FieldInvarWithDefaultExpr t = null; 
		for (int i = 0; i < 3; i++) {
			String msg = String.format("i=%d, empty? %b, allowed? %b", i, empty, allowed);
			try {
				t = fdefault[i];
				t.getI();
				if (!pass) {
					fail(msg);
				}
			} catch (ContractError ce) {
				if (pass) {
					fail(msg);
				}
			}
			if (pass) {  // Only test if passed; otherwise "t" is null!
				assertTrue(msg, t.iRW);
			}
		}
	}

	public void testDefinedCtor () {
		for (int i = 0; i < 3; i++) {
			try {
				new FieldInvarWithDefinedExpr (i);
				if (i == 0) {
					fail();
				}
			} catch (ContractError ce) {
				if (i > 0) {
					fail();
				}
			}
		}
	}
	public void testDefinedSetFail () {
		FieldInvarWithDefinedExpr t = 
			new FieldInvarWithDefinedExpr (1);
		try {
			t.setI(0);
			fail();
		} catch (ContractError ce) {
		}
	}
	public void testDefinedSetPass () {
		FieldInvarWithDefinedExpr t = 
			new FieldInvarWithDefinedExpr (1);
		try {
			t.setI(2);
		} catch (ContractError ce) {
			fail();
		}
	}

	public void testDefinedGetPass () {
		FieldInvarWithDefinedExpr t = 
			new FieldInvarWithDefinedExpr (1);
		try {
			t.getI();
		} catch (ContractError ce) {
			fail();
		}
	}

}
