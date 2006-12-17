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

package org.contract4j5.testexpression.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.contract4j5.configurator.Configurator;
import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.contract.Invar;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.instance.Instance;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.test.OverriddenContractBase;
import org.contract4j5.test.OverriddenContractDerived;
import org.contract4j5.testexpression.ParentTestExpressionFinderImpl;

public class ParentTestExpressionFinderImplTest extends TestCase {
	ParentTestExpressionFinderImpl finder;
	TestResult  defaultResult; 
	OverriddenContractBase base;   
	OverriddenContractDerived derived;
	Instance object;
	private TestContext context;
	
	protected void setUp() throws Exception {
		super.setUp();
		Configurator c = new ConfiguratorForTesting();
		c.configure();
		defaultResult = new TestResult (true, "default");
		finder   = new ParentTestExpressionFinderImpl();
		base     = new OverriddenContractBase("f1", "f2", "f3");
		derived  = new OverriddenContractDerived("f1", "f2", "f3");
		object   = new Instance("derived", OverriddenContractDerived.class, derived);
		context  = new TestContextImpl("", object, null, new Instance[0], null);
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.ParentTestExpressionFinderImpl.findParentMethodTestExpressionIfEmpty(String, Annotation, Method, TestContext)'
	 */
	public void testFindParentTestExpressionIfEmptyNotEmpty() {
		assertEquals (defaultResult, finder.findParentAdviceTestExpressionIfEmpty("default", null, null, context));
		assertEquals (defaultResult, finder.findParentMethodTestExpressionIfEmpty("default", null, null, context));
		assertEquals (defaultResult, finder.findParentConstructorTestExpressionIfEmpty("default", null, null, context));
	}
	
	public void testFindParentMethodTestExpressionIfEmptyPre() throws SecurityException, NoSuchMethodException {
		Class<?>[] argTypes = new Class[] { String.class };
		Instance[] args     = new Instance[] { new Instance("foo", String.class, new String("foo")) };
		Method method = OverriddenContractDerived.class.getDeclaredMethod("setField3", (Class[]) argTypes);
		Pre pre = method.getAnnotation(Pre.class);
		assertNotNull (pre);
		context = new TestContextImpl("setField3", object, null, args, null);
		// If "object" points to "derived", it will find derived's own test expression.
		// Also test whitespace suppression
		TestResult expected = new TestResult(true, "$args[0] != null && $args[0].length() >= 1");
		assertEquals (expected, finder.findParentMethodTestExpressionIfEmpty("", pre, method, context));
		
		// Now point "object" to "base" so it will find base's test expression.
		method = OverriddenContractBase.class.getDeclaredMethod("setField3", (Class[]) argTypes);
		pre = method.getAnnotation(Pre.class);
		assertNotNull (pre);
		expected = new TestResult(true, "$args[0] != null && $args[0].length() >= 3");
		context.setInstance(new Instance("base", OverriddenContractBase.class, base));
		assertEquals (expected, finder.findParentMethodTestExpressionIfEmpty("", pre, method, context));
	}
	
	public void testFindParentMethodTestExpressionIfEmptyPost() throws SecurityException, NoSuchMethodException {
		Instance[] args = new Instance[0];
		Method method   = OverriddenContractDerived.class.getDeclaredMethod("getField3", new Class[0]);
		Post post = method.getAnnotation(Post.class);
		assertNotNull (post);
		Instance result = new Instance("", String.class, new String("f3"));
		context = new TestContextImpl("getField3", object, null, args, result);
		// Will find "derived's" test first:
		TestResult expected = new TestResult(true, "$this.field.equals(\"foo\")");
		assertEquals (expected, finder.findParentMethodTestExpressionIfEmpty("", post, method, context));
		
		// Now find base's...
		method   = OverriddenContractBase.class.getDeclaredMethod("getField3", new Class[0]);
		post = method.getAnnotation(Post.class);
		assertNotNull (post);
		expected = new TestResult(true, "$this.field3.equals(\"foo\") || $this.field3.equals(\"bar\")");
		assertEquals (expected, finder.findParentMethodTestExpressionIfEmpty("", post, method, context));
	}
	
	public void testFindParentMethodTestExpressionIfEmptyInvar() throws SecurityException, NoSuchMethodException {
		Method method = OverriddenContractDerived.class.getDeclaredMethod("doNothing", new Class[0]);
		Invar  invar  = method.getAnnotation(Invar.class);
		assertNotNull (invar);
		context = new TestContextImpl("doNothing", object, null, new Instance[0], null);
		TestResult expected = new TestResult(true, "$this.invarFlagMethod == 0");
		assertEquals (expected, finder.findParentMethodTestExpressionIfEmpty("", invar, method, context));
	}

	public void testFindParentConstructorTestExpressionIfEmptyPre() throws SecurityException, NoSuchMethodException {
		Class<?>[] argTypes = new Class[] { String.class, String.class, String.class };
		Instance[] args = new Instance[] {
				new Instance ("f1", String.class,  new String("f1")), 
				new Instance ("f2", String.class,  new String("f2")), 
				new Instance ("f3", String.class,  new String("f3")) 
			}; 
		Constructor<?> constructor = OverriddenContractDerived.class.getConstructor((Class[]) argTypes);
		assertNotNull(constructor.getAnnotation(Pre.class));
		context = new TestContextImpl("OverriddenContractDerived", object, null, args, null);
		// Will find the derived test first (since it isn't empty)
		// We also test the whitespace suppression while we're at it.
		TestResult expected = new TestResult(true, "$args[0] != null");
		assertEquals (expected, finder.findParentConstructorTestExpressionIfEmpty("", constructor.getAnnotation(Pre.class), constructor, context));

		// Now find the base test 
		constructor = OverriddenContractBase.class.getConstructor((Class[]) argTypes);
		assertNotNull (constructor.getAnnotation(Pre.class));
		expected = new TestResult(true, "$args[0] != null && $args[1] != null && $args[2] != null");
		assertEquals (expected, finder.findParentConstructorTestExpressionIfEmpty("", constructor.getAnnotation(Pre.class), constructor, context));
	}

	public void testFindParentConstructorTestExpressionIfEmptyPost() throws SecurityException, NoSuchMethodException {
		Class<?>[] argTypes = new Class[] { String.class, String.class, String.class };
		Instance[] args = new Instance[] {
				new Instance ("f1", String.class,  new String("f1")), 
				new Instance ("f2", String.class,  new String("f2")), 
				new Instance ("f3", String.class,  new String("f3")) 
			}; 
		Constructor<?> constructor = OverriddenContractDerived.class.getConstructor((Class[]) argTypes);
		assertNotNull (constructor.getAnnotation(Post.class));
		context = new TestContextImpl("OverriddenContractDerived", object, null, args, null);
		// Will find the derived test first (since it isn't empty)
		// We also test the whitespace suppression while we're at it.
		TestResult expected = new TestResult(true, "$this.postFlag > 1");
		assertEquals (expected, finder.findParentConstructorTestExpressionIfEmpty("", constructor.getAnnotation(Post.class), constructor, context));

		// Now find the base test 
		constructor = OverriddenContractBase.class.getConstructor((Class[]) argTypes);
		assertNotNull (constructor.getAnnotation(Pre.class));
		expected = new TestResult(true, "$this.postFlag > 0");
		assertEquals (expected, finder.findParentConstructorTestExpressionIfEmpty("", constructor.getAnnotation(Post.class), constructor, context));
	}
	@SuppressWarnings("unchecked")
	public void testFindParentConstructorTestExpressionIfEmptyInvar() throws SecurityException, NoSuchMethodException {
		Class<?>[] argTypes = new Class[] { String.class, String.class, String.class };
		Instance[] args = new Instance[] {
				new Instance ("f1", String.class,  new String("f1")), 
				new Instance ("f2", String.class,  new String("f2")), 
				new Instance ("f3", String.class,  new String("f3")) 
			}; 
		Constructor<?> constructor = OverriddenContractDerived.class.getConstructor((Class[]) argTypes);
		assertNotNull (constructor.getAnnotation(Invar.class));
		context = new TestContextImpl("OverriddenContractDerived", object, new Instance(), args, new Instance());
		TestResult expected = new TestResult(true, "$this.invarFlagCtor == 0");
		assertEquals (expected, finder.findParentConstructorTestExpressionIfEmpty("", constructor.getAnnotation(Invar.class), constructor, context));
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.ParentTestExpressionFinderImpl.findParentTypeInvarTestExpressionIfEmpty(String, Class, TestContext)'
	 */
	public void testFindParentTypeInvarTestExpressionIfEmptyNotEmpty() {
		assertEquals (defaultResult, finder.findParentTypeInvarTestExpressionIfEmpty("default", OverriddenContractDerived.class, context));
	}
	public void testFindParentTypeInvarTestExpressionIfEmptyIsEmpty() {
		TestResult expected = new TestResult(true, "$this.field1 != null");
		assertEquals (expected, finder.findParentTypeInvarTestExpressionIfEmpty("", OverriddenContractDerived.class, context));
	}

}
