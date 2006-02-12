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

import org.contract4j5.Instance;
import org.contract4j5.TestContextImpl;

/**
 * Test {@link TestContextImpl}. Mostly, we test the nested 
 * {@link Instance} class.
 */
public class TestContextImplTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testInstanceCtor() {
		Instance i = new Instance();
		assertNull (i.getClazz());
		assertNull (i.getValue());
	}

	public void testInstanceCtorClassValue() {
		Instance i = new Instance("foo", String.class, new String("foo"));
		assertEquals (String.class, i.getClazz());
		assertEquals ("foo", i.getValue());
	}
	
	public void testToString() {
		Instance i = new Instance("foo", String.class, new String("foo"));
		assertEquals (i.toString(), "[name = foo, class = class java.lang.String, value = foo]", i.toString());
	}
	
	public void testEquals() {
		Instance i1 = new Instance("foo", String.class,  new String("foo"));
		Instance i2 = new Instance("foo", String.class,  new String("foo"));
		Instance i3 = new Instance("foo", String.class,  new String("bar"));
		Instance i4 = new Instance("bar", String.class,  new String("bar"));
		Instance i5 = new Instance("foo", Integer.class, new Integer(1));
		Instance i6 = new Instance();
		assertEquals (i1, i2);
		assertEquals (i2, i1);
		assertFalse  (i1.equals(i3));
		assertFalse  (i1.equals(i4));
		assertFalse  (i1.equals(i5));
		assertFalse  (i1.equals(i6));
		assertFalse  (i3.equals(i1));
		assertFalse  (i4.equals(i1));
		assertFalse  (i5.equals(i1));
		assertFalse  (i6.equals(i1));
		assertFalse  (i1.equals(null));
	}
}
