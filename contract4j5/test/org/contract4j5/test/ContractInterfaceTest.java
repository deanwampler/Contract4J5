/*
 * Copyright 2005, 2006 Dean Wampler. All rights reserved.
 * http://www.contract4j.org
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
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.errors.ContractError;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.util.ExampleContractInterface;

/** 
 * Tests and demonstrates behavior of a class that implements a contract-defining
 * Interface, with or without any annotations in the class. 
 * For "ContractInterfaceImplWithNoAnnos", The method tests are not inherited 
 * because the annotations are not used. No tests fail that should fail even 
 * though the contract is violated.
 * In contrast, "ContractInterfaceImplWithAnnos" carries the annnotations, so
 * the contracts fails when they should fail.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class ContractInterfaceTest extends TestCase {

	public static class ExampleContractInterfaceImplWithNoAnnos implements ExampleContractInterface {
		private String name = null;
		public void setName (String s) { name = s; }
		public String getName () { return name; }
		public void m (String s) {}
		public boolean getB() {	return true; }
		public int getFlag() { return 0; }
	}
		
	private ExampleContractInterfaceImplWithNoAnnos implWithNoAnnos = null;
	
	@Contract
	public static class ExampleContractInterfaceImplWithAnnos implements ExampleContractInterface {
		private String name = null;
		@Pre   public void setName (String s) { name = s; }
		@Post  public String getName () { return name; }
		@Invar public void m (String s) {}
		public boolean getB() {	return true; }
		public int getFlag() { return 0; }
	}
		
	private ExampleContractInterfaceImplWithAnnos implWithAnnos = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		Configurator c = new ConfiguratorForTesting();
		c.configure();
		implWithNoAnnos = new ExampleContractInterfaceImplWithNoAnnos();
		implWithAnnos   = new ExampleContractInterfaceImplWithAnnos();
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.setName(String)'
	 */
	public void testNoAnnosSetNameWithNull() {
		try {
			implWithNoAnnos.setName(null);  // Breaks contract!
		} catch (ContractError e) {
			fail();
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.setName(String)'
	 */
	public void testNoAnnosSetNameWithNonNull() {
		try {
			implWithNoAnnos.setName("ok");
		} catch (ContractError e) {
			fail();
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.getName()'
	 */
	public void testNoAnnosGetNameWithNull() {
		try {
			implWithNoAnnos.getName(); // Breaks contract!
		} catch (ContractError e) {
			fail();
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.getName()'
	 */
	public void tesNoAnnostGetNameWithNonNull() {
		try {
			implWithNoAnnos.setName("ok");
			implWithNoAnnos.getName();
		} catch (ContractError e) {
			fail();
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.m(String)'
	 */
	public void testNoAnnosMWithNullName() {
		try {
			implWithNoAnnos.m("toss"); // Breaks contract because name never initialized!
		} catch (ContractError e) {
			fail();
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.m(String)'
	 */
	public void testNoAnnosMWithEmptyName() {
		try {
			implWithNoAnnos.setName("");
			implWithNoAnnos.m("toss"); // Breaks contract because name is empty!
		} catch (ContractError e) {
			fail();
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.m(String)'
	 */
	public void testNoAnnosMWithNonEmptyName() {
		try {
			implWithNoAnnos.setName("foo");
			implWithNoAnnos.m("toss"); 
		} catch (ContractError e) {
			fail();
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.setName(String)'
	 */
	public void testWithAnnosSetNameWithNull() {
		try {
			implWithAnnos.setName(null);  // Breaks contract!
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError e) {
			// Expected
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.setName(String)'
	 */
	public void testWithAnnosSetNameWithNonNull() {
		try {
			implWithAnnos.setName("ok");
		} catch (ContractError e) {
			fail();
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.getName()'
	 */
	public void testWithAnnosGetNameWithNull() {
		try {
			implWithAnnos.getName(); // Breaks contract!
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError e) {
			// Expected
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.getName()'
	 */
	public void testWithAnnosGetNameWithNonNull() {
		try {
			implWithAnnos.setName("ok");
			implWithAnnos.getName();
		} catch (ContractError e) {
			fail();
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.m(String)'
	 */
	public void testWithAnnosMWithNullName() {
		try {
			implWithAnnos.m("toss"); // Breaks contract because name never initialized!
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError e) {
			// Expected
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.m(String)'
	 */
	public void testWithAnnosMWithEmptyName() {
		try {
			implWithAnnos.setName("");
			implWithAnnos.m("toss"); // Breaks contract because name is empty!
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError e) {
			// Expected
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.m(String)'
	 */
	public void testWithAnnosMWithNonEmptyName() {
		try {
			implWithAnnos.setName("foo");
			implWithAnnos.m("toss"); 
		} catch (ContractError e) {
			fail();
		}
	}

}
