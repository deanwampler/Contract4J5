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

public class MethodInvarTest extends TestCase {
	@Contract
	public static class MethodInvarWithDefaultExpr {
		public String name = null;  // public to help tests below
		@Invar  // default test (nothing!)
		public String getName() { return name; }
		@Invar  // default test (nothing!)
		public void setName(String name) { this.name = name; }		

		public int i = 0;
		@Invar  // default test (nothing!)
		public int getI() {	return i; }
		@Invar  // default test (nothing!)
		public void setI(int i) { this.i = i; }		
		
		public MethodInvarWithDefaultExpr (String name, int i) {
			this.name = name;
			this.i = i;
		}
	}
	
	@Contract
	public static class MethodInvarWithDefinedExpr {
		public String name = null;
		@Invar ("$this.name != null") 
		public String getName() { return name; }
		@Invar ("$this.name != null") 
		public void setName(String name) { this.name = name; }		

		public int i = 0;
		@Invar ("$this.i > 0") 
		public int getI() {	return i; }
		@Invar ("$this.i > 0") 
		public void setI(int i) { this.i = i; }		
		
		public MethodInvarWithDefinedExpr (String name, int i) {
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
//		c4j = c.getContract4J();
		c4j = Contract4J.getInstance();
	}
	
	public void testSetIWithDefaultOnBefore1 () {
		MethodInvarWithDefaultExpr t = 
			new MethodInvarWithDefaultExpr("foo", 0);
		try {
			t.setI(1);
			fail();  // fails because test is empty
		} catch (TestSpecificationError tse) {
			assertEquals(0, t.i);  // fails before value is set
		} catch (ContractError ce) {
			fail();
		}
	}
	public void testSetIWithDefaultOnBefore2 () {
		MethodInvarWithDefaultExpr t = 
			new MethodInvarWithDefaultExpr("foo", 1);
		try {
			t.setI(0);
			fail();  // fails because test is empty
		} catch (TestSpecificationError tse) {
			assertEquals(1, t.i);  // fails before value is set
		} catch (ContractError ce) {
			fail();
		}
	}
	public void testSetIWithDefaultOnBefore3 () {
		MethodInvarWithDefaultExpr t = 
			new MethodInvarWithDefaultExpr("foo", 1);
		try {
			t.setI(2);
			fail();  // fails because test is empty
		} catch (TestSpecificationError tse) {
			assertEquals(1, t.i);  // fails before value is set
		} catch (ContractError ce) {
			fail();
		}
	}
	public void testSetIWithDefaultOff1 () {
		// Turn off failures for empty tests 
		c4j.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(true);
		MethodInvarWithDefaultExpr t = 
			new MethodInvarWithDefaultExpr("foo", 1);
		try {
			t.setI(0);
		} catch (ContractError ce) {
			fail();  // no test -> never fails 
		}
	}
	public void testSetIWithDefaultOff2 () {
		// Turn off failures for empty tests 
		c4j.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(true);
		MethodInvarWithDefaultExpr t = 
			new MethodInvarWithDefaultExpr("foo", 1);
		try {
			t.setI(1);
		} catch (ContractError ce) {
			fail();  // no test -> never fails 
		}
	}

	public void testSetIWithDefinedFailBefore () {
		MethodInvarWithDefinedExpr t = 
			new MethodInvarWithDefinedExpr("foo", 0);
		try {
			t.setI(1);
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			assertEquals (0, t.i);  // it will have been set to 0; test failed afterwards.
		}
	}
	public void testSetIWithDefinedFailAfter () {
		MethodInvarWithDefinedExpr t = 
			new MethodInvarWithDefinedExpr("foo", 1);
		try {
			t.setI(0);
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			assertEquals (0, t.i);  // it will still be 0; test failed beforehand.
		}
	}
	public void testSetIWithDefinedPass () {
		MethodInvarWithDefinedExpr t = 
			new MethodInvarWithDefinedExpr("foo", 1);
		try {
			t.setI(2);
		} catch (ContractError ce) {
			fail();
		}
	}

	public void testGetIWithDefaultFail () {
		MethodInvarWithDefaultExpr t = 
			new MethodInvarWithDefaultExpr(null, 0);
		try {
			t.getI();
			fail();  // fails because test is empty
		} catch (TestSpecificationError tse) {
			assertEquals (0, t.i);
		} catch (ContractError ce) {
			fail();
		}
	}
	public void testGetIWithDefaultPass () {
		// Turn off failures for empty tests 
		c4j.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(true);
		MethodInvarWithDefaultExpr t = 
			new MethodInvarWithDefaultExpr(null, 0);
		try {
			t.getI();
			assertEquals (0, t.i);
		} catch (ContractError ce) {
			fail();  // no test -> never fails 
		}
	}

	public void testGetIWithDefinedFail () {
		MethodInvarWithDefinedExpr t = 
			new MethodInvarWithDefinedExpr(null, 0);
		try {
			t.getI();
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			assertEquals (0, t.i);
		}
	}
	public void testGetIWithDefinedPass () {
		MethodInvarWithDefinedExpr t = 
			new MethodInvarWithDefinedExpr(null, 1);
		try {
			t.getI();
			assertEquals (1, t.i);
		} catch (ContractError ce) {
			fail();
		}
	}

	public void testSetNameWithDefaultOnBefore1 () {
		MethodInvarWithDefaultExpr t = 
			new MethodInvarWithDefaultExpr("foo", 1);
		try {
			t.setName(null);
			fail();  // fails because test is empty
		} catch (TestSpecificationError tse) {
			assertEquals ("foo", t.name);  // test fails before setting to null
		} catch (ContractError ce) {
			fail();
		}
	}
	public void testSetNameWithDefaultOnBefore2 () {
		MethodInvarWithDefaultExpr t = 
			new MethodInvarWithDefaultExpr(null, 1);
		try {
			t.setName("foo");
			fail();  // fails because test is empty
		} catch (TestSpecificationError tse) {
			assertNull (t.name);  // test fails before setting value to "foo"
		} catch (ContractError ce) {
			fail();
		}
	}
	public void testSetNameWithDefaultOnBefore3 () {
		MethodInvarWithDefaultExpr t = 
			new MethodInvarWithDefaultExpr("foo", 1);
		try {
			t.setName("bar");
			fail();  // fails because test is empty
		} catch (TestSpecificationError tse) {
			assertEquals ("foo", t.name);  // test fails before setting to null
		} catch (ContractError ce) {
			fail();
		}
	}
	
	public void testSetNameWithDefaultOff1 () {
		// Turn off failures for empty tests 
		c4j.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(true);
		MethodInvarWithDefaultExpr t = 
			new MethodInvarWithDefaultExpr("foo", 1);
		try {
			t.setName(null);
			assertNull(t.name);
		} catch (ContractError ce) {
			fail();  // no test -> never fails 
		}
	}
	public void testSetNameWithDefaultOff2 () {
		// Turn off failures for empty tests 
		c4j.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(true);
		MethodInvarWithDefaultExpr t = 
			new MethodInvarWithDefaultExpr("foo", 1);
		try {
			t.setName("foo");
			assertEquals("foo", t.name);
		} catch (ContractError ce) {
			fail();  // no test -> never fails 
		}
	}

	public void testSetNameWithDefinedFailBefore () {
		MethodInvarWithDefinedExpr t = 
			new MethodInvarWithDefinedExpr(null, 1);
		try {
			t.setName("foo");
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			assertNull (t.name);  // fails before setting value
		}
	}
	public void testSetNameWithDefinedFailAfter () {
		MethodInvarWithDefinedExpr t = 
			new MethodInvarWithDefinedExpr("foo", 1);
		try {
			t.setName(null);
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			assertNull (t.name);  // fails after setting value
		}
	}
	public void testSetNameWithDefinedPass () {
		MethodInvarWithDefinedExpr t = 
			new MethodInvarWithDefinedExpr("foo", 1);
		try {
			t.setName("bar");
			assertEquals ("bar", t.name);
		} catch (ContractError ce) {
			fail();
		}
	}

	public void testGetNameWithDefaultFail () {
		MethodInvarWithDefaultExpr t = 
			new MethodInvarWithDefaultExpr(null, 0);
		try {
			t.getName();
			fail();  // fails because test is empty
		} catch (TestSpecificationError tse) {
		} catch (ContractError ce) {
			fail();
		}
	}
	public void testGetNameWithDefaultPass () {
		// Turn off failures for empty tests 
		c4j.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(true);
		MethodInvarWithDefaultExpr t = 
			new MethodInvarWithDefaultExpr(null, 0);
		try {
			t.getName();
			assertNull (t.name);
		} catch (ContractError ce) {
			fail();  // no test -> never fails 
		}
	}

	public void testGetNameWithDefinedFail () {
		MethodInvarWithDefinedExpr t = 
			new MethodInvarWithDefinedExpr(null, 0);
		try {
			t.getName();
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			assertNull(t.name);
		}
	}
	public void testGetNameWithDefinedPass () {
		MethodInvarWithDefinedExpr t = 
			new MethodInvarWithDefinedExpr("foo", 0);
		try {
			t.getName();
			assertEquals("foo", t.name);
		} catch (ContractError ce) {
			fail();
		}
	}
}
