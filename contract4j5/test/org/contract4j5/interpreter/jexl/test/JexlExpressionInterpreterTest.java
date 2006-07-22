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

package org.contract4j5.interpreter.jexl.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.contract4j5.Instance;
import org.contract4j5.TestContext;
import org.contract4j5.TestContextImpl;
import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.interpreter.jexl.JexlExpressionInterpreter;
import org.contract4j5.util.reporter.WriterReporter;

public class JexlExpressionInterpreterTest extends TestCase {
	public static class Foo {
		private String value1 = "value1";
		private String value2 = "value2";
		public  String getValue1() { return value1; }
		public  String getValue2() { return value2; }
		public  void   setValue1(String v1) { value1 = v1; }
		public  void   setValue2(String v2) { value2 = v2; }
		public  int number = 1;
		public  int setAndGetNumber(int i) { number = i; return number; }
		public  Foo () {} 
		public  Foo (String v1, String v2, int n) { 
			value1 = v1;
			value2 = v2;
			number = n;
		}
	}

	private JexlExpressionInterpreter interpreter = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		/* Configurator c = */ new ConfiguratorForTesting();
		interpreter = new JexlExpressionInterpreter();
		interpreter.setReporter(new WriterReporter());
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.jexl.JexlExpressionInterpreter.expandKeywords(String, String, Object, Object[], Object)'
	 */
	public void testExpandKeywords() {
		doTestExpandKeywords ("c4jThis.foo",  "foo");
		doTestExpandKeywords ("bar",          "bar");
		doTestExpandKeywords ("c4jThis",      "$this");
		doTestExpandKeywords ("c4jTarget",    "$target");
		doTestExpandKeywords ("c4jExprVar1",  "$old ( $this) ");
		doTestExpandKeywords ("c4jExprVar2",  "$old ( $target) ");
		doTestExpandKeywords ("c4jExprVar3",  "$old ( $this.length()) ");
		doTestExpandKeywords ("c4jExprVar4",  "$old ( $this . length ()) ");
		doTestExpandKeywords ("c4jExprVar5",  "$old ( $target.length()) ");
		doTestExpandKeywords ("c4jReturn",    "$return");
		doTestExpandKeywords ("c4jReturn",    "$return");
		doTestExpandKeywords ("c4jArgs",      "$args");
		doTestExpandKeywords ("c4jArgs[0]",   "$args[0]");
		doTestExpandKeywords ("c4jExprVar1.compareTo(c4jExprVar2) < 0", "$old($this).compareTo($old($target)) < 0");
	}

	private void doTestExpandKeywords (String expected, String testExpression) {
		String name     = "foo";
		String object   = "object";
		String target   = "target";
		String object2  = "old_object";
		String target2  = "old_target";
		Instance result = new Instance ("", String.class, "result");
		Instance[] args = new Instance[] {
			new Instance ("firstArg",  String.class, new String("arg0")), 
			new Instance ("secondArg", String.class, new String("arg1"))
		}; 
		Map<String, Object> oldMap = new HashMap<String, Object>();
		oldMap.put("$this",   object2);
		oldMap.put("$target", target2);
		oldMap.put("$this.length()",    object2.length());
		oldMap.put("$this . length ()", object2.length());
		oldMap.put("$target.length()",  target2.length());

		Instance    objecti = new Instance ("foo", Foo.class, object);
		Instance    targeti = new Instance ("target", String.class, target); 
		TestContext context = 
			new TestContextImpl(name, objecti, targeti, args, result, oldMap);
		TestResult testResult = interpreter.expandKeywords (testExpression, context);
		String actual = testResult.getMessage();
		// Unfortunately, we don't really know which "c4jExprVar?" will be returned, because
		// they are defined while iterating over the map entries, which has an undefined order.
		// At best, we can ensure that the value equals one of them, so we strip the number off!
		String e = expected.replaceAll ("c4jExprVar\\d", "c4jExprVar");
		String a = actual.replaceAll   ("c4jExprVar\\d", "c4jExprVar");
		assertEquals (e, a);
	}
	
	static final String[] msgs = new String[] {
			"instead of boolean",
			"\"$this\" present, but the \"instance\" is null",
			"\"$target\" present, but the \"target\" is null",
			"\"$return\" present, but the \"result\" is null",
			"\"$args\" present, but the \"args\" array is null or empty",
			"The \"$old(..)\" keyword 'function' requires a",
			"The \"$old(..)\" keyword argument is empty or contains an invalid value",
			"One or more \"$old(..)\" strings remain in test expression",
			"Test Expression contains unrecognized \"$\" keywords",
			"Test Expression contains whitespace between \"$\" and one or more keywords"
		};
	public enum WhenFails { WARN_VALIDATE, ERROR_VALIDATE, WARN_INVOKE_TEST, ERROR_INVOKE_TEST, PASS };
	static final WhenFails[] whenFails = new WhenFails[] {
			WhenFails.ERROR_INVOKE_TEST, 
			WhenFails.WARN_VALIDATE, 
			WhenFails.WARN_VALIDATE, 
			WhenFails.WARN_VALIDATE, 
			WhenFails.WARN_VALIDATE,
			WhenFails.ERROR_VALIDATE, 
			WhenFails.ERROR_VALIDATE, 
			WhenFails.ERROR_VALIDATE, 
			WhenFails.ERROR_VALIDATE, 
			WhenFails.ERROR_VALIDATE
	};
	
	/*
	 * Test method for 'org.contract4j5.interpreter.jexl.JexlExpressionInterpreter.test(String, String, Object, Object[], Object)'
	 */
	public void testTest1() {
		Instance object = new Instance ("Foo", Foo.class, new Foo("object", "object2", 1));
		Instance target = new Instance (String.class.getName(), String.class, "target");
		Instance result = new Instance (String.class.getName(), String.class, "result");
		Instance[] args = new Instance[] {
				new Instance ("firstArg",  String.class, new String("arg0")), 
				new Instance ("secondArg", String.class, new String("arg1"))
			}; 
		doTest1 (0, "foo",     null,     null,   null,   null, null);
		doTest1 (0, "foo",     "foo",    null,   null,   null, null);
		doTest1 (0, "foo",     "foo",    object, null,   null, null);
		doTest1 (0, "foo",     "foo",    object, target, null, null);
		doTest1 (0, "foo",     "foo",    object, target, args, null);
		doTest1 (0, "foo",     "foo",    object, target, args, result);
		doTest1 (1, "$this",   null,     null,   null,   null, null);
		doTest1 (1, "$this",   "this",   null,   null,   null, null);
		doTest1 (0, "$this",   "this",   object, null,   null, null);
		doTest1 (0, "$this",   "this",   object, target, null, null);
		doTest1 (0, "$this",   "this",   object, target, args, null);
		doTest1 (0, "$this",   "this",   object, target, args, result);
		doTest1 (2, "$target", null,     null,   null,   null, null);
		doTest1 (2, "$target", "target", null,   null,   null, null);
		doTest1 (2, "$target", "target", object, null,   null, null);
		doTest1 (0, "$target", "target", object, target, null, null);
		doTest1 (0, "$target", "target", object, target, args, null);
		doTest1 (0, "$target", "target", object, target, args, result);
		doTest1 (3, "$return", null,     null,   null,   null, null);
		doTest1 (3, "$return", "return", null,   null,   null, null);
		doTest1 (3, "$return", "return", object, null,   null, null);
		doTest1 (3, "$return", "return", object, target, null, null);
		doTest1 (3, "$return", "return", object, target, args, null);
		doTest1 (0, "$return", "return", object, target, args, result);
		doTest1 (8, "$arg[0]",  null,      null,   null,   null, null);
		doTest1 (4, "$args[0]", null,      null,   null,   null, null);
		doTest1 (4, "$args[0]", "args[0]", null,   null,   null, null);
		doTest1 (4, "$args[0]", "args[0]", object, null,   null, null);
		doTest1 (4, "$args[0]", "args[0]", object, target, null, null);
		doTest1 (0, "$args[0]", "args[0]", object, target, args, null);
		doTest1 (0, "$args[0]", "args[0]", object, target, args, result);
		doTest1 (5, "$old",    "$old",     object, target, args, result);
		doTest1 (6, "$old()",  "$old()",   object, target, args, result);
		doTest1 (0, "$old($this)",   "$old($this)",   object, target, args, result);
		doTest1 (0, "$old($target)", "$old($target)", object, target, args, result);
		doTest1 (0, "$old(object)",  "$old(object)",  object, target, args, result);
		doTest1 (9, "$ this",    null, null, null, null, null);
		doTest1 (9, "$ target",  null, null, null, null, null);
		doTest1 (9, "$ return",  null, null, null, null, null);
		doTest1 (9, "$ args[0]", null, null, null, null, null);
		doTest1 (9, "$ old",     null, null, null, null, null);
		doTest1 (9, "$ old ( $ this )",  null, null, null, null, null);
		doTest1 (8, "$foo",     null, null, null, null, null);
		doTest1 (8, "$object",  null, null, null, null, null);
		doTest1 (8, "$thisx",   null, null, null, null, null);
		doTest1 (8, "$targetx", null, null, null, null, null);
		doTest1 (8, "$argsx",   null, null, null, null, null);
		doTest1 (8, "$returnx", null, null, null, null, null);
		doTest1 (8, "$result",  null, null, null, null, null);
		doTest1 (8, "$foo $object $thisx $targetx $argsx $returnx $result",  null, null, null, null, null);
	}

	private void doTest1 (int expectedErrMsgIndex, String testExpression, String itemName, 
			Instance object, Instance target, Instance[] args, Instance result) {
		String expectedErrMsg = msgs[expectedErrMsgIndex]; 
		TestContext context = new TestContextImpl(itemName, object, target, args, result);
		TestResult testResult = interpreter.validateTestExpression(testExpression, context); 
		String msg = "Test expression: \""+testExpression+"\", result = \""+testResult.toString()+"\", expected to contain = \""+expectedErrMsg+"\".";
		switch (whenFails[expectedErrMsgIndex]) {
		case WARN_VALIDATE:
			assertTrue  (msg, testResult.isPassed());
			assertTrue  (msg, testResult.getMessage().contains(expectedErrMsg));
			break;
		case ERROR_VALIDATE:
			assertFalse (msg, testResult.isPassed());
			assertTrue  (msg, testResult.getMessage().contains(expectedErrMsg));
			break;
		default:
			assertTrue  (msg, testResult.isPassed());	
		}
		testResult = interpreter.invokeTest(testExpression, context); 
		msg = "Test expression: \""+testExpression+"\", result = \""+testResult.toString()+"\", expected to contain = \""+expectedErrMsg+"\".";
		assertFalse  (msg, testResult.isPassed());  // all tests should fail!
		switch (whenFails[expectedErrMsgIndex]) {
		case WARN_INVOKE_TEST:
			assertTrue  (msg, testResult.getMessage().contains(expectedErrMsg));
			break;
		case ERROR_INVOKE_TEST:
			assertTrue  (msg, testResult.getMessage().contains(expectedErrMsg));
			break;
		}
	}
	
	/*
	 * Test method for 'org.contract4j5.interpreter.jexl.JexlExpressionInterpreter.test(String, String, Object, Object[], Object)'
	 */
	public void testTest2() {
		Instance object = new Instance ("Foo",    Foo.class, new Foo("value1", "value2", 1));
		Instance target = new Instance ("target", String.class, "target");
		Instance result = new Instance ("result", String.class, "result");
		Instance[] args = new Instance[] {
				new Instance ("firstArg",  Integer.TYPE, new Integer(1)), 
				new Instance ("secondArg", Integer.TYPE, new Integer(2)),
				new Instance ("thirdArg",  Integer.TYPE, new Integer(3)),
			}; 
		doTest2 (true, "1+1 == 2", null, null, null, null, null);
		doTest2 (true, "1+1 >  1", null, null, null, null, null);
		doTest2 (true, "$args[0] + $args[1] >=  $args[2]",  null, null, null, args, null);
		doTest2 (true, "firstArg + secondArg >= thirdArg",  null, null, null, args, null);
		doTest2 (true, "$args[2] - $args[0] ==  $args[1]",  null, null, null, args, null);
		doTest2 (true, "thirdArg - firstArg ==  secondArg", null, null, null, args, null);
		doTest2 (true, "$args[2] != null",  null, null, null, args, null);
		doTest2 (true, "thirdArg != null",  null, null, null, args, null);
		doTest2 (true, "$args[3] == null",  null, null, null, args, null);
		doTest2 (true, "fourthArg == null", null, null, null, args, null);
		Instance[] resultArg = new Instance[] { new Instance("arg1", String.class, "result")};
		doTest2 (true, "$target.length() == $args[0].length()", null, null,   target, resultArg, null);
		doTest2 (true, "$target.length() == arg1.length()",     null, null,   target, resultArg, null);
		doTest2 (true, "$return.length() == $args[0].length()", null, null,   null,   resultArg, result);
		doTest2 (true, "$return.length() == arg1.length()",     null, null,   null,   resultArg, result);
		doTest2 (true, "$target.length() == $args[0].length()", null, null,   target, resultArg, null);
		doTest2 (true, "$target.length() == arg1.length()",     null, null,   target, resultArg, null);
		doTest2 (true, "$this.equals($target)", null, object, object, null, null);
		doTest2 (true, "$target.equals($this)", null, object, object, null, null);
		doTest2 (true, "$target.compareTo($return) > 0", null, null, target, null, result);
		doTest2 (true, "$return.compareTo($target) < 0", null, null, target, null, result);
		doTest2 (true, "$args[0].compareTo($return) == 0", null, null, null, resultArg, result);
		doTest2 (true, "arg1.compareTo($return)     == 0", null, null, null, resultArg, result);
		Instance thiz = new Instance ("this", String.class, "this");
		doTest2 (true, "$this.compareTo($target) > 0 && $args[0].compareTo($return) == 0", null, thiz, target, resultArg, result);
		doTest2 (true, "$this.compareTo($target) > 0 && arg1.compareTo($return)     == 0", null, thiz, target, resultArg, result);

//		doTest2 (true, "c4jOldThis.compareTo(c4jOldTarget) < 0", null, object, target, null, null);
		doTest2 (true, "$old($this).compareTo($old($target)) > 0", null, thiz, target, null, null);

		Foo foo = new Foo();
		Instance fooInstance    = new Instance("foo", Foo.class, foo);
		Instance value1Instance = new Instance("value1", String.class, foo.value1);
		//Instance value2Instance = new Instance("value2", String.class, foo.value2);
		doTest2 (true, "$this.getClass().getSimpleName().equals(\"Foo\")", null, fooInstance, null, null, null);
		doTest2 (true, "$this.class.simpleName.equals(\"Foo\")",           null, fooInstance, null, null, null);
		
		// A bare reference to a field? Only recognized if the field name matches
		// the itemName and the enclosing object is passed as the instance.
		doTest2 (true, "empty(value1)",  null,     fooInstance, null,    null, null);
		doTest2 (true, "!empty(value1)", "value1", fooInstance, null,    null, null);
		doTest2 (true, "empty(value1)",  null,     null, value1Instance, null, null);
		doTest2 (true, "empty(value1)",  "value1", null, value1Instance, null, null);

		// Contrast with explicit $this in front of field:
		doTest2 (true, "!empty($this.value1)",       null, fooInstance, null, null, null);
		doTest2 (true, "!empty($old($this.value1))", null, fooInstance, null, null, null);
		doTest2 (true, "!empty($old(value1))",       null, fooInstance, null, null, null);
		
		doTest2 (true, "size($old(value1)) == 6 && size($old(value2)) == 6", 
				                                                      null, fooInstance, null, null, null);
		doTest2 (true, "$old(value1).equals(\"value1\")",            null, fooInstance, null, null, null);
		doTest2 (true, "$old($this.value1).equals(\"value1\")",	  null, fooInstance, null, null, null);
		doTest2 (true, "$old($this).value1.equals(\"value1\")",	  null, fooInstance, null, null, null);
		doTest2 (true, "$old($this.getValue1()).equals(\"value1\")", null, fooInstance, null, null, null);
		doTest2 (true, "$old($this).getValue1().equals(\"value1\")", null, fooInstance, null, null, null);
		doTest2 (true, "$old(value1).equals(\"value1\") && $old(value2).equals(\"value2\")",
				                                                      null, fooInstance, null, null, null);
		doTest2 (true, "$old($this.value1).equals(\"value1\") && $old($this.value2).equals(\"value2\")",
				                                                      null, fooInstance, null, null, null);
		// The following fails because our you must prepend both value1 and value2 with "$this".
		doTest2 (false, "value1.equals(\"value1\") && value2.equals(\"value2\")",
				null, fooInstance, null, null, null);
		// Same test with the "$this." added for each one, then both, explicitly.
		doTest2 (false, "$this.value1.equals(\"value1\") && value2.equals(\"value2\")",
				null, fooInstance, null, null, null);
		doTest2 (false, "value1.equals(\"value1\") && $this.value2.equals(\"value2\")",
				null, fooInstance, null, null, null);
		// Passes because we use $this where we have to.
		doTest2 (true,  "$this.value1.equals(\"value1\") && $this.value2.equals(\"value2\")",
				null, fooInstance, null, null, null);
	}

	private void doTest2 (boolean shouldPass, String testExpression, String itemName, Instance object, Instance target, Instance[] args, Instance result) {
		TestContext context = new TestContextImpl(itemName, object, target, args, result);
		Map<String, Object> oldMap = interpreter.determineOldValues(testExpression, context);
		context.setOldValuesMap(oldMap);
		TestResult testResult = interpreter.invokeTest (testExpression, context);
		String msg = "Expression: "+testExpression+", "+testResult.toString();
		assertTrue (msg, testResult.isPassed() == shouldPass);
	}
	
	/** 
	 * Test issue #TBD
	 * Demonstrates that Jexl fails to parse "$this.field", where "field" is 
	 * a field without a public getter method, independent of the access 
	 * declaration of the field. (e.g., public, private, etc.)
	 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
	 */
	static public class AccessorTester {
		          int withNoAccessorDefault   = 10;	
		private   int withNoAccessorPrivate   = 10;	
		protected int withNoAccessorProtected = 10;	
		public    int withNoAccessorPublic    = 10;	
		private   int withAccessor = 10;
		          int getWithAccessorDefault()   { return withAccessor; }
		private   int getWithAccessorPrivate()   { return withAccessor; }
		protected int getWithAccessorProtected() { return withAccessor; }
		public    int getWithAccessorPublic()    { return withAccessor; }
		public    int getWithAccessor()          { return withAccessor; }
	}
	
	public void testSingleLetterAttributeInExpression() {
		AccessorTester at = new AccessorTester();
		Instance slaInstance = new Instance ("at", AccessorTester.class, at);

		// Only a public getter method will work:
		Instance withAccInstance = new Instance ("withAccessor", Integer.TYPE, at.withAccessor);
		doTest2(false, "$this.withAccessorDefault   > 5", "withAccessor", slaInstance, withAccInstance, null, null);
		doTest2(false, "$this.withAccessorPrivate   > 5", "withAccessor", slaInstance, withAccInstance, null, null);
		doTest2(false, "$this.withAccessorProtected > 5", "withAccessor", slaInstance, withAccInstance, null, null);
		doTest2(true,  "$this.withAccessorPublic    > 5", "withAccessor", slaInstance, withAccInstance, null, null);
		doTest2(true,  "$this.withAccessor          > 5", "withAccessor", slaInstance, withAccInstance, null, null);

		Instance noAccInstanceDefault = new Instance ("withNoAccessorDefault", Integer.TYPE, at.withNoAccessorDefault);
		doTest2(false, "$this.withNoAccessorDefault > 5", "withNoAccessorDefault", slaInstance, noAccInstanceDefault, null, null);
		Instance noAccInstancePrivate = new Instance ("withNoAccessorPrivate", Integer.TYPE, at.withNoAccessorPrivate);
		doTest2(false, "$this.withNoAccessorPrivate > 5", "withNoAccessorPrivate", slaInstance, noAccInstancePrivate, null, null);
		Instance noAccInstanceProtected = new Instance ("withNoAccessorProtected", Integer.TYPE, at.withNoAccessorProtected);
		doTest2(false, "$this.withNoAccessorProtected > 5", "withNoAccessorProtected", slaInstance, noAccInstanceProtected, null, null);
		Instance noAccInstancePblic = new Instance ("withNoAccessorPublic", Integer.TYPE, at.withNoAccessorPublic);
		doTest2(false, "$this.withNoAccessorPublic > 5", "withNoAccessorPublic", slaInstance, noAccInstancePblic, null, null);
	}
	
	/*
	 * Test method for 'org.contract4j5.interpreter.jexl.JexlExpressionInterpreter.captureOldValues(String testExpression, TestContext context)'
	 */
	public void testCaptureOldValues1() {
		Instance object = new Instance ("old_this",   String.class, new String("old_this"));
		Instance target = new Instance ("old_target", String.class, new String("old_target"));
		Instance result = new Instance ("", String.class, new String("result"));
		Instance[] args = new Instance[] {
				new Instance ("firstArg",  String.class, new String("arg0")), 
				new Instance ("secondArg", String.class, new String("arg1"))
			}; 
		TestContext context = 
			new TestContextImpl("itemName", object, target, args, result);
		String expr = "$this $old($this) $target $old($target) $return, $args[0], $args[1]";
		Map<String, Object> map = interpreter.determineOldValues(expr, context);
		assertEquals  (2, map.size());
		assertNotNull (map.get("$this"));
		assertEquals  ("old_this", map.get("$this"));
		assertNotNull (map.get("$target"));
		assertEquals  ("old_target", map.get("$target"));

		expr = "$this $old($this) $target $return, $args[0], $args[1]";
		map = interpreter.determineOldValues(expr, context);
		assertEquals  (1, map.size());
		assertNotNull (map.get("$this"));
		assertEquals  ("old_this", map.get("$this"));

		expr = "$this $target $old($target) $return, $args[0], $args[1]";
		map = interpreter.determineOldValues(expr, context);
		assertEquals  (1, map.size());
		assertNotNull (map.get("$target"));
		assertEquals  ("old_target", map.get("$target"));

		expr = "$this $target $return, $args[0], $args[1]";
		map = interpreter.determineOldValues(expr, context);
		assertEquals  (0, map.size());
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.jexl.JexlExpressionInterpreter.captureOldValues(String testExpression, TestContext context)'
	 */
	public void testCaptureOldValues2() {
		Instance foo1 = new Instance ("foo1", Foo.class, new Foo());
		Instance foo2 = new Instance ("foo2", Foo.class, new Foo("v21", "v22", 2));
		TestContext context = new TestContextImpl("number", foo1, foo2, null, null);

		// Bogus item:
		String expr = "$old($this.bogus)";
		Map<String, Object> map = interpreter.determineOldValues(expr, context);
		assertEquals  (1, map.size());
		assertNull    (map.toString(), map.get("$this.bogus"));
		expr = "$old(bogus)";
		map = interpreter.determineOldValues(expr, context);
		assertEquals  (1, map.size());
		assertNull    (map.toString(), map.get("bogus"));

		// Even though "number" is a public field in Foo, Jexl doesn't see it! An accessor method is required.
		expr = "$old($this.number)";
		map = interpreter.determineOldValues(expr, context);
		assertEquals  (1, map.size());
		assertNull    (map.toString(), map.get("$this.number"));
//		assertNotNull (map.toString(), map.get("$this.number"));
//		assertEquals  (1, map.get("$this.number"));
	
		// Still no see-um...
		expr = "$old(number)";
		map = interpreter.determineOldValues(expr, context);
		assertEquals  (1, map.size());
		assertNull    (map.toString(), map.get("number"));
//		assertNotNull (map.toString(), map.get("number"));
//		assertEquals  (1, map.get("number"));

		// Nested method call with an argument.
		expr = "$old($this.setAndGetNumber(2))";
		map = interpreter.determineOldValues(expr, context);
		assertEquals  (1, map.size());
		assertNotNull (map.toString(), map.get("$this.setAndGetNumber(2)"));
		assertEquals  (2, map.get("$this.setAndGetNumber(2)"));

		// Same, without "$this."
		expr = "$old(setAndGetNumber(2))";
		map = interpreter.determineOldValues(expr, context);
		assertEquals  (1, map.size());
		assertNotNull (map.toString(), map.get("setAndGetNumber(2)"));
		assertEquals  (2, map.get("setAndGetNumber(2)"));
		
		// Reference a field through an accessor and directly.
		expr = "$old($this.getValue1())";
		map = interpreter.determineOldValues(expr, context);
		assertEquals  (1, map.size());
		assertNotNull (map.toString(), map.get("$this.getValue1()"));
		assertEquals  ("value1", map.get("$this.getValue1()"));
		expr = "$old($this.value1)";
		map = interpreter.determineOldValues(expr, context);
		assertEquals  (1, map.size());
		assertNotNull (map.toString(), map.get("$this.value1"));
		assertEquals  ("value1", map.get("$this.value1"));

		// Same, without "$this."
		expr = "$old(value1)";
		map = interpreter.determineOldValues(expr, context);
		assertEquals  (1, map.size());
		assertNotNull (map.toString(), map.get("value1"));
		assertEquals  ("value1", map.get("value1"));
		expr = "$old(getValue1())";
		map = interpreter.determineOldValues(expr, context);
		assertEquals  (1, map.size());
		assertNotNull (map.toString(), map.get("getValue1()"));
		assertEquals  ("value1", map.get("getValue1()"));
		
		// A bunch together:
		expr = "$old($this.number) $old(number) $old($this.getValue1()) $old(getValue1()) " +
				"$old($this.value1) $old(value1) $old($this.setAndGetNumber(3)) $old(setAndGetNumber(4)) $this $target $return $args";
		map = interpreter.determineOldValues(expr, context);
		assertEquals  (8, map.size());
		assertNull    (map.toString(), map.get("$this.number"));
		assertNull    (map.toString(), map.get("number"));
		assertNotNull (map.toString(), map.get("$this.getValue1()"));
		assertNotNull (map.toString(), map.get("getValue1()"));
		assertNotNull (map.toString(), map.get("$this.value1"));
		assertNotNull (map.toString(), map.get("value1"));
		assertNotNull (map.toString(), map.get("$this.setAndGetNumber(3)"));
		assertNotNull (map.toString(), map.get("setAndGetNumber(4)"));
		assertEquals  ("value1", map.get("$this.getValue1()"));
		assertEquals  ("value1", map.get("getValue1()"));
		assertEquals  ("value1", map.get("$this.value1"));
		assertEquals  ("value1", map.get("value1"));
		assertEquals  (3, map.get("$this.setAndGetNumber(3)"));
		assertEquals  (4, map.get("setAndGetNumber(4)"));
		
	}

}
