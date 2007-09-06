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
import org.contract4j5.util.NoContractInterfaceDerivedImpl;

public class NoContractInterfaceDerivedImplTest extends NoContractInterfaceImplTest {
	NoContractInterfaceDerivedImpl obj2 = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		// Override the parent test's object.
		obj2 = new NoContractInterfaceDerivedImpl(1);
		setObj(obj2);
	}
	/*
	 * Test method for 'org.contract4j5.test.NoContractInterfaceDerivedImpl.NoContractInterfaceDerivedImpl(int)'.
	 * Should not fail any tests.
	 */
	public void testClassImplNoContractInterface() {
		try {
			obj2 = new NoContractInterfaceDerivedImpl(100);
			String n = obj2.getName();
			assertNull (n);
			int f = obj2.getFlag();
			assertEquals (100, f);
			doTestClassAndFieldInvariants();
		} catch (ContractError ce) {
			fail();
		}
	}
}
