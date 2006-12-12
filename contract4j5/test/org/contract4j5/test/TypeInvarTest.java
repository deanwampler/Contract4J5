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

import org.contract4j5.configurator.Configurator;
import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.errors.ContractError;
import org.contract4j5.errors.TestSpecificationError;

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

	Contract4J c4j;

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
		c4j.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(false);
	}

	public void testWithDefaultFalse() {
		TypeInvarWithDefaultExpr t = 
			doTestCtorWithDefault(false, "foo", 1);  // Won't pass if empty test strings, but args irrelevant
		t = doTestCtorWithDefault(false, null,  1); 
		t = doTestCtorWithDefault(false, "foo", 0); 
		t = doTestCtorWithDefault(false, null,  0); 
		// Now temporarily disable the test failure when the test is empty so we can
		// successfully create an object for subsequent tests.
		c4j.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(true);
		t = new TypeInvarWithDefaultExpr("foo", 1);  // Will pass if empty test strings.
		c4j.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(false);
		doTestSetIWithDefault(t, false);
		doTestSetNameWithDefault(t, false);
	}
	public void testCtorWithDefaultTrue() {
		c4j.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(true);
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
		} catch (TestSpecificationError tse) {
			if (shouldPass) {
				fail();
			}
		} catch (ContractError ce) {
			fail();
		}
		return null;
	}
	protected void doTestSetIWithDefault (TypeInvarWithDefaultExpr t, boolean shouldPass) {
		try {
			t.setI(0);
			if (!shouldPass) {
				fail();
			}
		} catch (TestSpecificationError tse) {
			if (shouldPass) {
				fail();
			}
		} catch (ContractError ce) {
			fail();
		}
	}
	protected void doTestSetNameWithDefault (TypeInvarWithDefaultExpr t, boolean shouldPass) {
		try {
			t.setName(null);
			if (!shouldPass) {
				fail();
			}
		} catch (TestSpecificationError tse) {
			if (shouldPass) {
				fail();
			}
		} catch (ContractError ce) {
			fail();
		}
	}
	
	public void testDefined() {
		TypeInvarWithDefinedExpr t = null;
		try {
			t = new TypeInvarWithDefinedExpr(null, 1);
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
		}
		try {
			t = new TypeInvarWithDefinedExpr("foo", 0);
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
		}
		try {
			t = new TypeInvarWithDefinedExpr(null, 0);
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
		}
		t = new TypeInvarWithDefinedExpr("foo", 1);
		try {
			t.setI(0);
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
		}
		try {
			t.setName(null);
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
		}
	}
}
