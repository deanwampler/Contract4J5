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

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.errors.ContractError;
import org.contract4j5.errors.TestSpecificationError;

import junit.framework.TestCase;

public class Eclipse153490BugTest extends TestCase {

	@Contract
	public static class Foo {

		private String fooField = null;

		@Pre("f != null")
		public void setFooField(String f) {
			fooField = f; 
		}

		@Post("$return != null")
		public String getFooField() {
			return fooField;
		}
	}

	public static void main(String[] args) {
		new Eclipse153490BugTest().testFoo();
	}

	public void testFoo() {
		Contract4J c4j = new Contract4J();
		c4j.setEnabled(Contract4J.TestType.Pre,   true); //1
		c4j.setEnabled(Contract4J.TestType.Post,  true); //2 
		c4j.setEnabled(Contract4J.TestType.Invar, true); //3

		Foo foo = new Foo();
		try {
			foo.setFooField(null);
			fail();
		} catch (TestSpecificationError tse) {
			fail(tse.getMessage());
		} catch (ContractError ce) {
			// expected
		}
		try {
			System.out.println(foo.getFooField());
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			// expected
		}
	}	
}
