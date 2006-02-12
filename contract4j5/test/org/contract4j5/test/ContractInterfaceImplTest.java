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

import org.contract4j5.ContractError;

public class ContractInterfaceImplTest extends TestCase {
	private ContractInterfaceImpl obj = null;
	public ContractInterfaceImpl getObj() {
		return obj;
	}
	public void setObj(ContractInterfaceImpl obj) {
		this.obj = obj;
	}

	protected void setUp() throws Exception {
		super.setUp();
		ManualSetup.wireC4J();
		obj = new ContractInterfaceImpl(1);
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.getName()'.
	 */
	public void testGetName() {
		try {
			// Should not fail the postcondition test and the class and field ("b") invariant tests.
			String n = obj.getName();
			assertNotNull (n);
			doTestClassAndFieldInvariants();
		} catch (ContractError ce) {
			fail();
		}		
		// Trigger failure of getName()...
		obj = new ContractInterfaceImpl(1, 4);  
		try {
			obj.getName();
			fail();
		} catch (ContractError ce) {
			// Expected
		}		
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.setName(String)'
	 */
	public void testSetName() {
		try {
			obj.setName(null);
			fail();
		} catch (ContractError ce) {
			// Expected
		}		
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.getFlag()'.
	 */
	public void testGetFlag() {
		try {
			int f = obj.getFlag();
			assertTrue (f > 0);
			doTestClassAndFieldInvariants();
		} catch (ContractError ce) {
			fail();
		}		
		// Trigger failure of class invariant for getFlag()...
		try {
			obj = new ContractInterfaceImpl(0, 2);  
			fail();
		} catch (ContractError ce) {
			// Expected
		}		
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.m()'.
	 * Should not fail the class invariant test nor the method invariant test.
	 */
	public void testM() {
		try {
			obj.m("good");
			doTestClassAndFieldInvariants();
		} catch (ContractError ce) {
			fail();
		}		
		try {
			obj.m("bad");
			fail();
		} catch (ContractError ce) {
		}		
		// Trigger failure of m invariant...
		try {
			obj = new ContractInterfaceImpl(0, 4);
			obj.m("bad");
			fail();
		} catch (ContractError ce) {
			// Expected
		}		
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.ClassImplContractInterface(int)'.
	 */
	public void testClassImplContractInterface1() {
		try {
			obj = new ContractInterfaceImpl(0);  // Fail class invariant
			fail();
		} catch (ContractError ce) {
			// Expected
		}
	}

	public void testClassImplContractInterface2() {
		try {
			obj = new ContractInterfaceImpl(100);  // Fail the c'tor precondition.
			fail();
		} catch (ContractError ce) {
			// Expected
		}
	}
	
	public void testClassImplContractInterface3() {
		try {
			obj = new ContractInterfaceImpl(1, 2); // Fail the c'tor postcondition
			fail();
		} catch (ContractError ce) {
			// Expected
		}
	}
	
	public void testClassImplContractInterface4() {
		try {
			obj = new ContractInterfaceImpl(1, 4); // Fail the getName postcondition
		} catch (ContractError ce) {
			fail();		// shouldn't have failed yet!!
		}
		try {
			obj.getName();
			fail();
		} catch (ContractError ce) {
			// Expected	to now fail!
		}
	}
	
	public void testClassImplContractInterface5() {
		try {
			obj = new ContractInterfaceImpl(1, 5);  // Fail the "b" field invariant
			fail();
		} catch (ContractError ce) {
			// Expected
		}
	}

	protected void doTestClassAndFieldInvariants () {
		int f = obj.getFlag();
		assertTrue (f > 0);
		boolean b = obj.getB();
		assertTrue (b);
		
	}
}
