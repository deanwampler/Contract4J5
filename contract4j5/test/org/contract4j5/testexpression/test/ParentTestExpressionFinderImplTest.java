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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.contract4j5.Instance;
import org.contract4j5.Invar;
import org.contract4j5.Post;
import org.contract4j5.Pre;
import org.contract4j5.TestContext;
import org.contract4j5.TestContextImpl;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.test.OverriddenContractBase;
import org.contract4j5.test.OverriddenContractDerived;
import org.contract4j5.testexpression.ParentTestExpressionFinderImpl;

public class ParentTestExpressionFinderImplTest extends TestCase {
	ParentTestExpressionFinderImpl finder = null;
	TestResult defaultResult = new TestResult (true, "default");
	TestContext context = null;
	OverriddenContractBase    base    = new OverriddenContractBase("f1", "f2", "f3");
	OverriddenContractDerived derived = new OverriddenContractDerived("f1", "f2", "f3");
	Constructor constructor = null;
	Method     method   = null;
	String     itemName = null;
	Pre        pre      = null;
	Post       post     = null;
	Invar      invar    = null;
	Class[]    argTypes = null;
	Instance[] args     = null;
	Instance   object   = null;
	Instance   target   = null;
	Instance   result   = null;
	protected void setUp() throws Exception {
		super.setUp();
		finder   = new ParentTestExpressionFinderImpl();
		itemName = "";
		method   = null;
		pre      = null;
		post     = null;
		invar    = null;
		argTypes = null;
		args     = null;
		object   = new Instance("derived", OverriddenContractDerived.class, derived);
		target   = null;
		result   = null;
		context  = new TestContextImpl(itemName, object, target, args, result);
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.ParentTestExpressionFinderImpl.findParentMethodTestExpressionIfEmpty(String, Annotation, Method, TestContext)'
	 */
	public void testFindParentTestExpressionIfEmptyNotEmpty() {
		assertEquals (defaultResult, finder.findParentAdviceTestExpressionIfEmpty("default", pre, method, context));
		assertEquals (defaultResult, finder.findParentMethodTestExpressionIfEmpty("default", pre, method, context));
		assertEquals (defaultResult, finder.findParentConstructorTestExpressionIfEmpty("default", pre, constructor, context));
	}
	
	public void testFindParentMethodTestExpressionIfEmptyPre() {
		argTypes = new Class[] { String.class };
		args     = new Instance[] { new Instance("foo", String.class, new String("foo")) };
		try {
			method   = OverriddenContractDerived.class.getDeclaredMethod("setField3", argTypes);
		} catch (Exception e) {
			fail(e.toString());
		}
		pre = method.getAnnotation(Pre.class);
		assertNotNull (pre);
		context = new TestContextImpl("setField3", object, target, args, result);
		// If "object" points to "derived", it will find derived's own test expression.
		// Also test whitespace suppression
		TestResult expected = new TestResult(true, "$args[0] != null && $args[0].length() >= 1");
		assertEquals (expected, finder.findParentMethodTestExpressionIfEmpty("", pre, method, context));
		
		// Now point "object" to "base" so it will find base's test expression.
		try {
			method   = OverriddenContractBase.class.getDeclaredMethod("setField3", argTypes);
		} catch (Exception e) {
			fail(e.toString());
		}
		pre = method.getAnnotation(Pre.class);
		assertNotNull (pre);
		expected = new TestResult(true, "$args[0] != null && $args[0].length() >= 3");
		context.setInstance(new Instance("base", OverriddenContractBase.class, base));
		assertEquals (expected, finder.findParentMethodTestExpressionIfEmpty("", pre, method, context));
	}
	public void testFindParentMethodTestExpressionIfEmptyPost() {
		argTypes = new Class[0];
		args     = new Instance[0];
		try {
			method   = OverriddenContractDerived.class.getDeclaredMethod("getField3", argTypes);
		} catch (Exception e) {
			fail(e.toString());
		}
		post = method.getAnnotation(Post.class);
		assertNotNull (post);
		result = new Instance("", String.class, new String("f3"));
		context = new TestContextImpl("getField3", object, target, args, result);
		// Will find "derived's" test first:
		TestResult expected = new TestResult(true, "$this.field.equals(\"foo\")");
		assertEquals (expected, finder.findParentMethodTestExpressionIfEmpty("", post, method, context));
		
		// Now find base's...
		try {
			method   = OverriddenContractBase.class.getDeclaredMethod("getField3", argTypes);
		} catch (Exception e) {
			fail(e.toString());
		}
		post = method.getAnnotation(Post.class);
		assertNotNull (post);
		expected = new TestResult(true, "$this.field.equals(\"foo\") || $this.field.equals(\"bar\")");
		assertEquals (expected, finder.findParentMethodTestExpressionIfEmpty("", post, method, context));
	}
	public void testFindParentMethodTestExpressionIfEmptyInvar() {
		try {
			argTypes = new Class[0];
			args     = new Instance[0];
			method   = OverriddenContractDerived.class.getDeclaredMethod("doNothing", argTypes);
		} catch (Exception e) {
			fail(e.toString());
		}
		invar = method.getAnnotation(Invar.class);
		assertNotNull (invar);
		context = new TestContextImpl("doNothing", object, target, args, result);
		TestResult expected = new TestResult(true, "$this.invarFlagMethod == 0");
		assertEquals (expected, finder.findParentMethodTestExpressionIfEmpty("", invar, method, context));
	}

	public void testFindParentConstructorTestExpressionIfEmptyPre() {
		argTypes = new Class[] { String.class, String.class, String.class };
		args = new Instance[] {
				new Instance ("f1", String.class,  new String("f1")), 
				new Instance ("f2", String.class,  new String("f2")), 
				new Instance ("f3", String.class,  new String("f3")) 
			}; 
		try {
			constructor = OverriddenContractDerived.class.getConstructor(argTypes);
		} catch (Exception e) {
			fail(e.toString());
		}
		Annotation a = constructor.getAnnotation(Pre.class);
		pre = (Pre) a;
//		pre = constructor.getAnnotation(Pre.class);
		assertNotNull (pre);
		context = new TestContextImpl("OverriddenContractDerived", object, target, args, result);
		// Will find the derived test first (since it isn't empty)
		// We also test the whitespace suppression while we're at it.
		TestResult expected = new TestResult(true, "$args[0] != null");
		assertEquals (expected, finder.findParentConstructorTestExpressionIfEmpty("", pre, constructor, context));

		// Now find the base test 
		try {
			constructor = OverriddenContractBase.class.getConstructor(argTypes);
		} catch (Exception e) {
			fail(e.toString());
		}
		a = constructor.getAnnotation(Pre.class);
		pre = (Pre) a;
//		pre = constructor.getAnnotation(Pre.class);
		assertNotNull (pre);
		expected = new TestResult(true, "$args[0] != null && $args[1] != null && $args[2] != null");
		assertEquals (expected, finder.findParentConstructorTestExpressionIfEmpty("", pre, constructor, context));
	}
	public void testFindParentConstructorTestExpressionIfEmptyPost() {
		argTypes = new Class[] { String.class, String.class, String.class };
		args = new Instance[] {
				new Instance ("f1", String.class,  new String("f1")), 
				new Instance ("f2", String.class,  new String("f2")), 
				new Instance ("f3", String.class,  new String("f3")) 
			}; 
		try {
			constructor = OverriddenContractDerived.class.getConstructor(argTypes);
		} catch (Exception e) {
			fail(e.toString());
		}
		Annotation a = constructor.getAnnotation(Post.class);
		post = (Post) a;
//		post = constructor.getAnnotation(Post.class);
		assertNotNull (post);
		context = new TestContextImpl("OverriddenContractDerived", object, target, args, result);
		// Will find the derived test first (since it isn't empty)
		// We also test the whitespace suppression while we're at it.
		TestResult expected = new TestResult(true, "$this.postFlag > 1");
		assertEquals (expected, finder.findParentConstructorTestExpressionIfEmpty("", post, constructor, context));

		// Now find the base test 
		try {
			constructor = OverriddenContractBase.class.getConstructor(argTypes);
		} catch (Exception e) {
			fail(e.toString());
		}
		a = constructor.getAnnotation(Pre.class);
		pre = (Pre) a;
//		pre = constructor.getAnnotation(Pre.class);
		assertNotNull (pre);
		expected = new TestResult(true, "$this.postFlag > 0");
		assertEquals (expected, finder.findParentConstructorTestExpressionIfEmpty("", post, constructor, context));
	}
	public void testFindParentConstructorTestExpressionIfEmptyInvar() {
		argTypes = new Class[] { String.class, String.class, String.class };
		args = new Instance[] {
				new Instance ("f1", String.class,  new String("f1")), 
				new Instance ("f2", String.class,  new String("f2")), 
				new Instance ("f3", String.class,  new String("f3")) 
			}; 
		try {
			constructor = OverriddenContractDerived.class.getConstructor(argTypes);
		} catch (Exception e) {
			fail(e.toString());
		}
		Annotation a = constructor.getAnnotation(Invar.class);
		invar = (Invar) a;
//		invar = constructor.getAnnotation(Invar.class);
		assertNotNull (invar);
		context = new TestContextImpl("OverriddenContractDerived", object, target, args, result);
		TestResult expected = new TestResult(true, "$this.invarFlagCtor == 0");
		assertEquals (expected, finder.findParentConstructorTestExpressionIfEmpty("", invar, constructor, context));
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
