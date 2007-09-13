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

import org.contract4j5.errors.ContractError;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.util.ExampleContractInterfaceDerivedImpl;
import org.contract4j5.util.ExampleContractInterfaceImpl;
import org.contract4j5.configurator.test.ConfiguratorForTesting;

public class ContractInterfaceDerivedImplTest extends ContractInterfaceImplTest {
	private ExampleContractInterfaceImpl obj2 = null;

	public ContractInterfaceDerivedImplTest() {
		super();
		new ConfiguratorForTesting().configure();
	}

	protected void setUp() throws Exception {
		super.setUp();
		// Override the parent test's object.
		obj2 = new ExampleContractInterfaceDerivedImpl(1);
		setObj(obj2);
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceDerivedImpl.ClassImplContractInterfaceDerivedImpl(int)'.
	 */
	public void testClassImplContractInterface1() {
		try {
			obj2 = new ExampleContractInterfaceDerivedImpl(0);  // Fail class invariant
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			// Expected
		}
	}

	public void testClassImplContractInterface2() {
		try {
			obj2 = new ExampleContractInterfaceDerivedImpl(100);  // Fail the c'tor precondition.
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			// Expected
		}
	}
	
	public void testClassImplContractInterface3() {
		try {
			obj2 = new ExampleContractInterfaceDerivedImpl(1, 2); // Fail the c'tor postcondition
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			// Expected
		}
	}
	
	public void testClassImplContractInterface4() {
		try {
			obj2 = new ExampleContractInterfaceDerivedImpl(1, 4); // Fail the getName postcondition
		} catch (ContractError ce) {
			fail();		// shouldn't have failed yet!!
		}
		try {
			obj2.getName();
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			// Expected	to now fail!
		}
	}
	
	public void testClassImplContractInterface5() {
		try {
			obj2 = new ExampleContractInterfaceDerivedImpl(1, 5);  // Fail the "b" field invariant
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			// Expected
		}
	}
}
