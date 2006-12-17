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
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.errors.ContractError;
import org.contract4j5.errors.TestSpecificationError;

/**
 * Test that user defined error messages are picked up from the test annotations.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class AnnotationMessageTest extends TestCase {
	/**
	 * Static class with all the test types, each of which has a message.
	 * Note that some of the "getX" methods exist only to silence warnings
	 * about unread fields.
	 */
	@Contract
	@Invar(value="$this.i > 0", message="i must be greater than 0!!")
	static public class Foo {
		private int i = 1;
		public void setI(int i) { this.i = i; }
		public int  getI() { return this.i; }
		
		@Invar(value="$this.j == 0", message="j must be = 0")
		private int j = 0;
		public void setJ (int j) { this.j = j; }
		public int  getJ() { return this.j; }
		
		private int k = 0;
		@Pre(value="$args[0] > 0", message="argument must be greater than 0")
		public void setK (int k) { this.k = k; }
		@Post(value="$this.k > 1", message="k must be > 1")
		public int getK() { return this.k; }
		@Post(value="$this.k > 1", message="k must be > 1")
		public void voidK() { k = 0; }
		
		private int l = 0;
		public int  getL() { return this.l; }
		@Invar(value="$this.l == 0", message="l must be = 0")
		public void doit(int arg) { this.l = arg; }
		
		private int m = 0;
		public int  getM() { return this.m; }
		
		private int n = 0;
		public int  getN() { return this.n; }
		@Invar(value="$this.n == 0", message="n must be = 0")
		public Foo(boolean pass) {
			if (!pass) {
				this.n = 1;
			}
		}
		
		@Pre(value="$args[0] > 0", message="arg must be > 0")
		public Foo(int arg) {
		}
		
		private int o=0;
		public int getO() { return this.o; }
		
		@Post(value="$this.o > 0", message="o must be > 0")
		public Foo(float f) {
			o = 0; // ignore f and always fail...
		}

		public Foo() {}
	}

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		Configurator c = new ConfiguratorForTesting();
		c.configure();
	}
	
	public void testTypeInvar() {
		try {
			Foo f = new Foo();
			f.setI(0);
			fail();
		} catch (TestSpecificationError tse) {
			fail(tse.getMessage());
		} catch (ContractError ce) {
			// Expected
		}
	}
	
	public void testFieldInvar() {
		try {
			Foo f = new Foo();
			f.setJ(1);
			fail();
		} catch (TestSpecificationError tse) {
			fail(tse.getMessage());
		} catch (ContractError ce) {
			// Expected
		}
	}
	
	public void testMethodInvar() {
		try {
			Foo f = new Foo();
			f.doit(1);
			fail();
		} catch (TestSpecificationError tse) {
			fail(tse.getMessage());
		} catch (ContractError ce) {
			// Expected
		}
	}

	public void testMethodPre() {
		try {
			Foo f = new Foo();
			f.setK(0);
			fail();
		} catch (TestSpecificationError tse) {
			fail(tse.getMessage());
		} catch (ContractError ce) {
			// Expected
		}
	}

	public void testMethodPost() {
		try {
			Foo f = new Foo();
			f.setK(-1);
			fail();
		} catch (TestSpecificationError tse) {
			fail(tse.getMessage());
		} catch (ContractError ce) {
			// Expected
		}
	}
	
	public void testMethodVoidPost() {
		try {
			Foo f = new Foo();
			f.voidK();
			fail();
		} catch (TestSpecificationError tse) {
			fail(tse.getMessage());
		} catch (ContractError ce) {
			// Expected
		}
	}
	
	public void testCtorInvar() {
		try {
			new Foo(false);
			fail();
		} catch (TestSpecificationError tse) {
			fail(tse.getMessage());
		} catch (ContractError ce) {
			// Expected
		}
	}

	public void testCtorPre() {
		try {
			new Foo(0);
			fail();
		} catch (TestSpecificationError tse) {
			fail(tse.getMessage());
		} catch (ContractError ce) {
			// Expected
		}
	}

	public void testCtorPost() {
		try {
			new Foo(1.0f);
			fail();
		} catch (TestSpecificationError tse) {
			fail(tse.getMessage());
		} catch (ContractError ce) {
			// Expected
		}
	}
}
