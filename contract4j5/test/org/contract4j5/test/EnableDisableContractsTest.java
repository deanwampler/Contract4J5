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
import org.contract4j5.aspects.Contract4J;
import org.contract4j5.configurator.Configurator;
import org.contract4j5.configurator.test.ConfiguratorForTesting;

/**
 * Test API programmatic manipulation of properties, as opposed to configuration
 * through property files.
 */
public class EnableDisableContractsTest extends TestCase {
	
	BaseTestClass baseTestClass = null;

	protected void setUp() throws Exception {
		super.setUp();
		Configurator c = new ConfiguratorForTesting();
		c.configure();
		Contract4J.setEnabled(Contract4J.TestType.Pre, false);
		Contract4J.setEnabled(Contract4J.TestType.Post, false);
		Contract4J.setEnabled(Contract4J.TestType.Invar, false);
		baseTestClass = new BaseTestClass("baseTestClass");
	}

	public void testPreMethodOff() {
		try {
			baseTestClass.doThat(1, "foo");
		} catch (ContractError ce) {
			fail();
		}
	}

	public void testPreMethodOn() {
		Contract4J.setEnabled(Contract4J.TestType.Pre, true);
		try {
			baseTestClass.doThat(0, "foo");
			fail();
		} catch (ContractError ce) {
		}
		try {
			baseTestClass.doThat(1, "bar");
			fail();
		} catch (ContractError ce) {
		}
	}

	public void testPostMethodOff() {
		try {
			baseTestClass.doThat(0, "bar");
		} catch (ContractError ce) {
			fail();
		}
		try {
			baseTestClass.setName("bad name");
		} catch (ContractError ce) {
			fail();
		}
		try {
			baseTestClass.doIt();
		} catch (ContractError ce) {
			fail();
		}
	}

	public void testPostMethodOn() {
		Contract4J.setEnabled(Contract4J.TestType.Post, true);
		try {
			baseTestClass.setName("bad name");
			baseTestClass.doIt();
			fail();
		} catch (ContractError ce) {
		}
	}

	public void testInvarMethodOff() {
		try {
			baseTestClass.setLazyPi(0f);
		} catch (ContractError ce) {
			fail();
		}
	}

	public void testInvarMethodOn() {
		Contract4J.setEnabled(Contract4J.TestType.Invar, true);
		try {
			baseTestClass.setLazyPi(0f);
			fail();
		} catch (ContractError ce) {
		}
		try {
			baseTestClass.setName(null);
			fail();
		} catch (ContractError ce) {
		}
	}

	public void testInvarFieldOff() {
		try {
			baseTestClass.setName(null);
		} catch (ContractError ce) {
			fail();
		}
	}

	public void testInvarFieldOn() {
		Contract4J.setEnabled(Contract4J.TestType.Invar, true);
		try {
			baseTestClass.setName(null);
			fail();
		} catch (ContractError ce) {
		}
	}

}
