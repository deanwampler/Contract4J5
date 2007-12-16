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

package org.contract4j5.aspects.method.test;

import junit.framework.TestCase;

import org.contract4j5.configurator.Configurator;
import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.errors.ContractError;
import org.contract4j5.errors.TestSpecificationError;

public class MethodBoundaryTest extends TestCase {
	@Contract
	public static class MethodBoundaryWithDefaultExpr {
		public String name = null;  // public to help tests below
		@Post  // default test 
		public String getName() { return name; }
		@Pre  // default test
		public void setName(String name) { this.name = name; }		

		public int i = 0;
		@Post  // default test
		public int getI() {	return i; }
		@Pre  // default test
		public void setI(int i) { this.i = i; }		
		
		@Pre @Post
		public String doit (String s, Integer i, Float f) {
			if (s != null && s.equals("bad")) {
				s = null;
			}
			return s;
		}
		
		public MethodBoundaryWithDefaultExpr (String name, int i) {
			this.name = name;
			this.i = i;
		}
	}
	
	@Contract
	public static class MethodBoundaryWithDefinedExpr {
		public String name = null;
		@Post ("$return != null && $return.length() > 0") 
		public String getName() { return name; }
		@Pre ("$args[0] != null") 
		public void setName(String name) { this.name = name; }		

		public int i = 0;
		@Post ("$this.i > 1") 
		public int getI() {	return i; }
		@Pre ("$args[0] > 0") 
		public void setI(int i) { this.i = i; }		
		
		@Pre ("$args[0] != null && $args[1] != null && $args[2] != null")
		@Post ("$return != null && $return.length() > 0")
		public String doit (String s, Integer i, Float f) {
			return s;
		}
		
		public MethodBoundaryWithDefinedExpr (String name, int i) {
			this.name = name;
			this.i = i;
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Configurator c = new ConfiguratorForTesting();
		c.configure();
	}
	
	public void testSetIWithDefaultPrePass1() {
		MethodBoundaryWithDefaultExpr t = 
			new MethodBoundaryWithDefaultExpr("foo", 0);
		try {
			t.setI(1);
			assertEquals(1, t.i);  
		} catch (ContractError ce) {
			// should pass because test is "$args[0] != null", even though
			// the argument is a primitive. This information appears to be
			// lost by the time we get to determining the default expression,
			// because we only keep the object list of args, not their classes
			// as might be captured by the aspects.
			fail();  
		}
	}

	public void testSetIWithDefaultPrePass2() {
		MethodBoundaryWithDefaultExpr t = 
			new MethodBoundaryWithDefaultExpr("foo", 1);
		try {
			t.setI(0);
			assertEquals(0, t.i);
		} catch (ContractError ce) {
			fail();  // see #testSetIWithDefaultPrePass1
		}
	}

	public void testSetIWithDefinedPreFail () {
		MethodBoundaryWithDefinedExpr t = 
			new MethodBoundaryWithDefinedExpr("foo", 1);
		try {
			t.setI(0);
			fail();
		} catch (ContractError ce) {
			assertEquals (1, t.i);  // it will still be 1; test failed beforehand.
		}
	}
	public void testSetIWithDefinedPrePass1 () {
		MethodBoundaryWithDefinedExpr t = 
			new MethodBoundaryWithDefinedExpr("foo", 0);
		try {
			t.setI(1);
			assertEquals (1, t.i);
		} catch (ContractError ce) {
			fail(ce.toString());
		}
	}
	public void testSetIWithDefinedPrePass2 () {
		MethodBoundaryWithDefinedExpr t = 
			new MethodBoundaryWithDefinedExpr("foo", 1);
		try {
			t.setI(2);
			assertEquals (2, t.i);
		} catch (ContractError ce) {
			fail(ce.toString());
		}
	}

	public void testGetIWithDefaultPostPass () {
		MethodBoundaryWithDefaultExpr t = 
			new MethodBoundaryWithDefaultExpr(null, 0);
		try {
			t.getI();
			assertEquals (0, t.i);
		} catch (ContractError ce) {
			// Like the "setI" tests, it won't fail because the
			// the post test will not know it's returning a primitive
			// type and hence the test won't be empty.
			fail();  
		}
	}

	public void testDoitWithDefaultPreFail1() {
		MethodBoundaryWithDefaultExpr t = 
			new MethodBoundaryWithDefaultExpr("foo", 1);
		try {
			t.doit(null, new Integer(1), new Float(1.0));
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
		}
	}
	public void testDoitWithDefaultPreFail2() {
		MethodBoundaryWithDefaultExpr t = 
			new MethodBoundaryWithDefaultExpr("foo", 1);
		try {
			t.doit(new String("foo"), null, new Float(1.0));
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testDoitWithDefaultPreFail3() {
		MethodBoundaryWithDefaultExpr t = 
			new MethodBoundaryWithDefaultExpr("foo", 1);
		try {
			t.doit(new String("foo"), new Integer(1), null);
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
		}
	}
	public void testDoitWithDefaultPostFail() {
		MethodBoundaryWithDefaultExpr t = 
			new MethodBoundaryWithDefaultExpr("foo", 1);
		try {
			// Fail the post condition
			t.doit(new String("bad"), new Integer(1), new Float(1.0));
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testDoitWithDefaultPrePostPass() {
		MethodBoundaryWithDefaultExpr t = 
			new MethodBoundaryWithDefaultExpr("foo", 1);
		try {
			t.doit(new String("foo"), new Integer(1), new Float(1.0));
		} catch (ContractError ce) {
			fail(ce.toString());
		}
	}
	
	public void testGetIWithDefinedPostFail1 () {
		MethodBoundaryWithDefinedExpr t = 
			new MethodBoundaryWithDefinedExpr(null, 0);
		try {
			t.getI();
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			assertEquals (0, t.i); 
		}
	}
	public void testGetIWithDefinedPostFail2 () {
		MethodBoundaryWithDefinedExpr t = 
			new MethodBoundaryWithDefinedExpr(null, 1);
		try {
			t.getI();
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			assertEquals (1, t.i);
		}
	}
	public void testGetIWithDefinedPostPass () {
		MethodBoundaryWithDefinedExpr t = 
			new MethodBoundaryWithDefinedExpr(null, 2);
		try {
			t.getI();
			assertEquals (2, t.i);
		} catch (ContractError ce) {
			fail();
		}
	}

	public void testSetNameWithDefaultPreFail () {
		MethodBoundaryWithDefaultExpr t = 
			new MethodBoundaryWithDefaultExpr("foo", 1);
		try {
			t.setName(null);
			fail();  // fails because "$args[0] != null" fails.
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			assertEquals ("foo", t.name);  // test fails before setting to null
		}
	}
	public void testSetNameWithDefaultPrePass1 () {
		MethodBoundaryWithDefaultExpr t = 
			new MethodBoundaryWithDefaultExpr(null, 1);
		try {
			t.setName("foo");
			assertNotNull (t.name);
			assertEquals  ("foo", t.name); 
		} catch (ContractError ce) {
			fail(); 
		}
	}
	public void testSetNameWithDefaultPrePass2 () {
		MethodBoundaryWithDefaultExpr t = 
			new MethodBoundaryWithDefaultExpr("foo", 1);
		try {
			t.setName("bar");
			assertEquals ("bar", t.name);  
		} catch (ContractError ce) {
			fail();  // fails because test is empty
		}
	}
	
	public void testSetNameWithDefinedPreFail () {
		MethodBoundaryWithDefinedExpr t = 
			new MethodBoundaryWithDefinedExpr("foo", 1);
		try {
			t.setName(null);
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			assertEquals ("foo", t.name);  // fails before setting value
		}
	}
	public void testSetNameWithDefinedPrePass1 () {
		MethodBoundaryWithDefinedExpr t = 
			new MethodBoundaryWithDefinedExpr(null, 1);
		try {
			t.setName("foo");
			assertNotNull (t.name);  // fails before setting value
		} catch (ContractError ce) {
			// passes because test is only evaluated afterwards, 
			// when value is now valid.
			fail();  
		}
	}
	public void testSetNameWithDefinedPrePass2 () {
		MethodBoundaryWithDefinedExpr t = 
			new MethodBoundaryWithDefinedExpr("foo", 1);
		try {
			t.setName("bar");
			assertEquals ("bar", t.name);
		} catch (ContractError ce) {
			fail();
		}
	}

	public void testGetNameWithDefaultPostFail () {
		MethodBoundaryWithDefaultExpr t = 
			new MethodBoundaryWithDefaultExpr(null, 0);
		try {
			t.getName();
			fail(); 
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			assertNull(t.name);
		}
	}
	public void testGetNameWithDefaultPostPass () {
		MethodBoundaryWithDefaultExpr t = 
			new MethodBoundaryWithDefaultExpr("foo", 0);
		try {
			t.getName();
		} catch (ContractError ce) {
			fail(); 
		}
	}
	public void testGetNameWithDefinedPostFail () {
		MethodBoundaryWithDefinedExpr t = 
			new MethodBoundaryWithDefinedExpr(null, 0);
		try {
			t.getName();
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			assertNull(t.name);
		}
	}
	public void testGetNameWithDefinedPostPass () {
		MethodBoundaryWithDefinedExpr t = 
			new MethodBoundaryWithDefinedExpr("foo", 0);
		try {
			t.getName();
			assertEquals("foo", t.name);
		} catch (ContractError ce) {
			fail();
		}
	}
	
	public void testDoitWithDefinedPreFail1 () {
		MethodBoundaryWithDefinedExpr t = 
			new MethodBoundaryWithDefinedExpr("foo", 1);
		try {
			t.doit(null, new Integer(1), new Float(1.0));
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
		}
	}
	public void testDoitWithDefinedPreFail2 () {
		MethodBoundaryWithDefinedExpr t = 
			new MethodBoundaryWithDefinedExpr("foo", 1);
		try {
			t.doit(new String("foo"), null, new Float(1.0));
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
		}
	}
	public void testDoitWithDefinedPreFail3 () {
		MethodBoundaryWithDefinedExpr t = 
			new MethodBoundaryWithDefinedExpr("foo", 1);
		try {
			t.doit(new String("foo"), new Integer(1), null);
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
		}
	}
	public void testDoitWithDefinedPostFail () {
		MethodBoundaryWithDefinedExpr t = 
			new MethodBoundaryWithDefinedExpr("foo", 1);
		try {
			// Fail the post condition
			t.doit(new String(""), new Integer(1), new Float(1.0));
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
		}
	}
	
	public void testDoitWithDefinedPrePostPass() {
		MethodBoundaryWithDefinedExpr t = 
			new MethodBoundaryWithDefinedExpr("foo", 1);
		try {
			t.doit(new String("foo"), new Integer(1), new Float(1.0));
		} catch (ContractError ce) {
			fail(ce.toString());
		}
	}

	@Contract
	class ClassWithBadMethodBoundaryContractExpressions {
		@Pre(" > 0")
		public void do1(int i) {}
		@Post(" > 0.0f")
		public void do2(float f) {}
	}
	
	// May fail with either a TestSpecificationError or ContractError, depending on the interpreter used!
	public void testBadPreconditionContractExpressionsFail() {
		ClassWithBadMethodBoundaryContractExpressions obj = new ClassWithBadMethodBoundaryContractExpressions();
		try {
			obj.do1(1);
			fail();  
		} catch (ContractError ce) {
		}		
	}
	
	public void testBadPostconditionContractExpressionsFail() {
		ClassWithBadMethodBoundaryContractExpressions obj = new ClassWithBadMethodBoundaryContractExpressions();
		try {
			obj.do2(1.0f);
			fail();  
		} catch (ContractError ce) {
		}		
	}

}
