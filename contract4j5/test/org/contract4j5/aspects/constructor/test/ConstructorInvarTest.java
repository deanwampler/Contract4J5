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

package org.contract4j5.aspects.constructor.test;

import junit.framework.TestCase;

import org.contract4j5.configurator.Configurator;
import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.errors.ContractError;
import org.contract4j5.errors.TestSpecificationError;

public class ConstructorInvarTest extends TestCase {
	@Contract
	public static class ConstructorInvarWithDefaultExpr {
		String name = null;
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }		

		int i = 0;
		public int getI() {	return i; }
		public void setI(int i) { this.i = i; }		
		
		@Invar  // default test; nothing so it will be a test specification error
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

	Contract4J c4j;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Configurator c = new ConfiguratorForTesting();
		c.configure();
		c4j = Contract4J.getInstance();
	}
	
	public void testWithDefaultFalse() {
		ConstructorInvarWithDefaultExpr t = 
			doTestCtorWithDefault(false, "foo", 1);  // Won't pass if empty test strings, but args irrelevant
		t = doTestCtorWithDefault(false, null,  1); 
		t = doTestCtorWithDefault(false, "foo", 0); 
		t = doTestCtorWithDefault(false, null,  0); 
		// In order to test the setters, must construct an object while the empty-test issue is bypassed!
		c4j.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(true);
		t = doTestCtorWithDefault(true, "foo", 1);  // Won't pass if empty test strings, but args irrelevant
		doTestSetIWithDefault(t);
		doTestSetNameWithDefault(t);
	}
	public void testWithDefaultTrue() {
		c4j.getContractEnforcer().getExpressionInterpreter().setTreatEmptyTestExpressionAsValidTest(true);
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
		} catch (TestSpecificationError tse) {
			if (shouldPass) {
				fail();
			}
		} catch (ContractError ce) {
			fail();
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

	@Contract
	class ClassWithBadInvariantContractExpressions {
		@Invar(" > 0")
		public ClassWithBadInvariantContractExpressions(int i) {}
	}
	
	// May fail with either a TestSpecificationError or ContractError, depending on the interpreter used!
	public void testBadInvariantContractExpressionsFail() {
		try {
			new ClassWithBadInvariantContractExpressions(1);
			fail();  
		} catch (ContractError ce) {
		}		
	}
}
