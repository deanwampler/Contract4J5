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

package org.contract4j5.interpreter.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.instance.Instance;
import org.contract4j5.interpreter.ExpressionInterpreterHelper;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.util.SystemUtils;

public class ExpressionInterpreterAdapterExpressionEvalTest extends TestCase {
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

	public ExpressionInterpreterAdapterExpressionEvalTest() {
		super();
		ConfiguratorForTesting c = new ConfiguratorForTesting();
		c.configure();
		interpreter = c.expressionInterpreter;
	}
	
	private ExpressionInterpreterHelper interpreter = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		interpreter = (ExpressionInterpreterHelper) Contract4J.getInstance().getContractEnforcer().getExpressionInterpreter();
		interpreter.setCacheTestExpressionValidations(false);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		interpreter.setCacheTestExpressionValidations(true);
	}
	
	public void testExpandKeywordsWithValidExpressionsReplacesDollarKeywordsWithC4JKeywords() {
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
		Instance[] args = makeTestArgs(); 
		Map<String, Object> oldMap = new HashMap<String, Object>();
		oldMap.put("$this",   object2);
		oldMap.put("$target", target2);
		oldMap.put("$this.length()",    object2.length());
		oldMap.put("$this . length ()", object2.length());
		oldMap.put("$target.length()",  target2.length());

		Instance    objecti = new Instance ("foo", Foo.class, object);
		Instance    targeti = new Instance ("target", String.class, target); 
		TestContext context = 
			new TestContextImpl(name, name, objecti, targeti, args, result, oldMap, "", 0);
		TestResult testResult = interpreter.expandKeywords (testExpression, context);
		String actual = testResult.getMessage();
		// Unfortunately, we don't really know which "c4jExprVar?" will be returned, because
		// they are defined while iterating over the map entries, which has an undefined order.
		// At best, we can ensure that the value equals one of them, so we strip the number off!
		String e = expected.replaceAll ("c4jExprVar\\d", "c4jExprVar");
		String a = actual.replaceAll   ("c4jExprVar\\d", "c4jExprVar");
		assertEquals (e, a);
	}
	
	public void testExpressionConsideredValidWithNoData() {
		TestContext context = new TestContextImpl("value1", null, null, null, null, null, null, -1);
		doTestExpressionValidWithItemName(context);
	}
	public void testExpressionConsideredValidWithItemNameButNoData() {
		TestContext context = new TestContextImpl("value1", "value1", null, null, null, null, null, -1);
		doTestExpressionValidWithItemName(context);
	}
	public void testExpressionConsideredValidWithItemNameObject() {
		TestContext context = new TestContextImpl("value1", "value1", makeTestFooObject(), null, null, null, null, -1);
		doTestExpressionValidWithItemName(context);
	}
	public void testExpressionConsideredValidWithItemNameObjectTarget() {
		TestContext context = new TestContextImpl("value1", "value1", makeTestFooObject(), makeTestTarget(), null, null, null, -1);
		doTestExpressionValidWithItemName(context);
	}
	public void testExpressionConsideredValidWithItemNameObjectTargetArgs() {
		TestContext context = new TestContextImpl("value1", "value1", makeTestFooObject(), makeTestTarget(), makeTestArgs(), null, null, -1);
		doTestExpressionValidWithItemName(context);
	}
	public void testExpressionConsideredValidWithItemNameObjectTargetArgsResult() {
		TestContext context = new TestContextImpl("value1", "value1", makeTestFooObject(), makeTestTarget(), makeTestArgs(), makeTestResult(), null, -1);
		doTestExpressionValidWithItemName(context);
	}
	public void doTestExpressionValidWithItemName(TestContext context) {
		TestResult testResult = interpreter.validateTestExpression("value1", context); 
		assertTestPassed(testResult);
	}
	
	public void testExpressionInvalidWithDollarThisAndNoData() {
		TestContext context = new TestContextImpl("$this", null, null, null, null, null, null, -1);
		doTestExpressionInvalidWithDollarThis(context);
	}
	public void testExpressionInvalidWithDollarThisAndItemName() {
		TestContext context = new TestContextImpl("$this", "value1", null, null, null, null, null, -1);
		doTestExpressionInvalidWithDollarThis(context);
	}
	public void testExpressionInvalidWithDollarThisItemNameObject() {
		TestContext context = new TestContextImpl("$this", "value1", makeTestFooObject(), null, null, null, null, -1);
		doTestExpressionValidWithDollarThis(context);
	}
	public void testExpressionInvalidWithDollarThisItemNameObjectTarget() {
		TestContext context = new TestContextImpl("$this", "value1", makeTestFooObject(), makeTestTarget(), null, null, null, -1);
		doTestExpressionValidWithDollarThis(context);
	}
	public void testExpressionInvalidWithDollarThisItemNameObjectTargetArgs() {
		TestContext context = new TestContextImpl("$this", "value1", makeTestFooObject(), makeTestTarget(), makeTestArgs(), null, null, -1);
		doTestExpressionValidWithDollarThis(context);
	}
	public void testExpressionInvalidWithDollarThisItemNameObjectTargetArgsResult() {
		TestContext context = new TestContextImpl("$this", "value1", makeTestFooObject(), makeTestTarget(), makeTestArgs(), makeTestResult(), null, -1);
		doTestExpressionValidWithDollarThis(context);
	}
	public void doTestExpressionInvalidWithDollarThis(TestContext context) {
		TestResult testResult = interpreter.validateTestExpression("$this", context); 
		assertTestFailed(testResult, "\"$this\" present, but the \"instance\" is null");
	}
	public void doTestExpressionValidWithDollarThis(TestContext context) {
		TestResult testResult = interpreter.validateTestExpression("$this", context); 
		assertTestPassed(testResult);
	}
	
	public void testExpressionInvalidWithDollarTargetAndNoData() {
		TestContext context = new TestContextImpl("$target", null, null, null, null, null, null, -1);
		doTestExpressionInvalidWithDollarTarget(context);
	}
	public void testExpressionInvalidWithDollarTargetAndItemName() {
		TestContext context = new TestContextImpl("$target", "value1", null, null, null, null, null, -1);
		doTestExpressionInvalidWithDollarTarget(context);
	}
	public void testExpressionInvalidWithDollarTargetItemNameObject() {
		TestContext context = new TestContextImpl("$target", "value1", makeTestFooObject(), null, null, null, null, -1);
		doTestExpressionInvalidWithDollarTarget(context);
	}
	public void testExpressionValidWithDollarTargetItemNameObjectTarget() {
		TestContext context = new TestContextImpl("$target", "value1", makeTestFooObject(), makeTestTarget(), null, null, null, -1);
		doTestExpressionValidWithDollarTarget(context);
	}
	public void testExpressionValidWithDollarTargetItemNameObjectTargetArgs() {
		TestContext context = new TestContextImpl("$target", "value1", makeTestFooObject(), makeTestTarget(), makeTestArgs(), null, null, -1);
		doTestExpressionValidWithDollarTarget(context);
	}
	public void testExpressionValidWithDollarTargetItemNameObjectTargetArgsResult() {
		TestContext context = new TestContextImpl("$target", "value1", makeTestFooObject(), makeTestTarget(), makeTestArgs(), makeTestResult(), null, -1);
		doTestExpressionValidWithDollarTarget(context);
	}
	public void doTestExpressionInvalidWithDollarTarget(TestContext context) {
		TestResult testResult = interpreter.validateTestExpression("$target", context); 
		assertTestFailed(testResult, "\"$target\" present, but the \"target\" (field) is null");
	}
	public void doTestExpressionValidWithDollarTarget(TestContext context) {
		TestResult testResult = interpreter.validateTestExpression("$target", context); 
		assertTestPassed(testResult);
	}
	
	public void testExpressionInvalidWithDollarReturnAndNoData() {
		TestContext context = new TestContextImpl("$return", null, null, null, null, null, null, -1);
		doTestExpressionInvalidWithDollarReturn(context);
	}
	public void testExpressionInvalidWithDollarReturnAndItemName() {
		TestContext context = new TestContextImpl("$return", "return", null, null, null, null, null, -1);
		doTestExpressionInvalidWithDollarReturn(context);
	}
	public void testExpressionInvalidWithDollarReturnItemNameObject() {
		TestContext context = new TestContextImpl("$return", "return", makeTestFooObject(), null, null, null, null, -1);
		doTestExpressionInvalidWithDollarReturn(context);
	}
	public void testExpressionInvalidWithDollarReturnItemNameObjectTarget() {
		TestContext context = new TestContextImpl("$return", "return", makeTestFooObject(), makeTestTarget(), null, null, null, -1);
		doTestExpressionInvalidWithDollarReturn(context);
	}
	public void testExpressionInvalidWithDollarReturnItemNameObjectTargetArgs() {
		TestContext context = new TestContextImpl("$return", "return", makeTestFooObject(), makeTestTarget(), makeTestArgs(), null, null, -1);
		doTestExpressionInvalidWithDollarReturn(context);
	}
	public void testExpressionInvalidWithDollarReturnItemNameObjectTargetArgsResult() {
		TestContext context = new TestContextImpl("$return", "return", makeTestFooObject(), makeTestTarget(), makeTestArgs(), makeTestResult(), null, -1);
		doTestExpressionValidWithDollarReturn(context);
	}
	public void doTestExpressionInvalidWithDollarReturn(TestContext context) {
		TestResult testResult = interpreter.validateTestExpression("$return", context); 
		assertTestFailed(testResult, "\"$return\" present, but the \"result\" is null");
	}
	public void doTestExpressionValidWithDollarReturn(TestContext context) {
		TestResult testResult = interpreter.validateTestExpression("$return", context); 
		assertTestPassed(testResult);
	}
	
	public void testExpressionInvalidWithDollarArg0AndNoData() {
		TestContext context = new TestContextImpl("$args[0]", null, null, null, null, null, null, -1);
		doTestExpressionInvalidWithDollarArgs(context);
	}
	public void testExpressionInvalidWithDollarArg0AndItemName() {
		TestContext context = new TestContextImpl("$args[0]", "$args[0]", null, null, null, null, null, -1);
		doTestExpressionInvalidWithDollarArgs(context);
	}
	public void testExpressionInvalidWithDollarArg0ItemNameObject() {
		TestContext context = new TestContextImpl("$args[0]", "$args[0]", makeTestFooObject(), null, null, null, null, -1);
		doTestExpressionInvalidWithDollarArgs(context);
	}
	public void testExpressionInvalidWithDollarArg0ItemNameObjectTarget() {
		TestContext context = new TestContextImpl("$args[0]", "$args[0]", makeTestFooObject(), makeTestTarget(), null, null, null, -1);
		doTestExpressionInvalidWithDollarArgs(context);
	}
	public void testExpressionInvalidWithDollarArg0ItemNameObjectTargetArgs() {
		TestContext context = new TestContextImpl("$args[0]", "$args[0]", makeTestFooObject(), makeTestTarget(), makeTestArgs(), null, null, -1);
		doTestExpressionValidWithDollarArgs(context);
	}
	public void testExpressionInvalidWithDollarArg0ItemNameObjectTargetArgsResult() {
		TestContext context = new TestContextImpl("$args[0]", "$args[0]", makeTestFooObject(), makeTestTarget(), makeTestArgs(), makeTestResult(), null, -1);
		doTestExpressionValidWithDollarArgs(context);
	}
	public void doTestExpressionInvalidWithDollarArgs(TestContext context) {
		TestResult testResult = interpreter.validateTestExpression("$args[0]", context); 
		assertTestFailed(testResult, "\"$args\" present, but the \"args\" array is null or empty");
	}
	public void doTestExpressionValidWithDollarArgs(TestContext context) {
		TestResult testResult = interpreter.validateTestExpression("$args[0]", context); 
		assertTestPassed(testResult);
	}
	
	public void testExpressionInvalidWithDollarOldWithoutParens() {
		TestContext context = new TestContextImpl("$old", "old", makeTestFooObject(), makeTestTarget(), makeTestArgs(), makeTestResult(), null, -1);
		TestResult testResult = interpreter.validateTestExpression("$old", context); 
		assertTestFailed(testResult, "The \"$old(..)\" keyword 'function' requires a");
	}
	public void testExpressionInvalidWithDollarOldWithEmptyParens() {
		TestContext context = new TestContextImpl("$old()", "old()", makeTestFooObject(), makeTestTarget(), makeTestArgs(), makeTestResult(), null, -1);
		TestResult testResult = interpreter.validateTestExpression("$old()", context); 
		assertTestFailed(testResult, "The \"$old(..)\" keyword argument is empty or contains an invalid value");
	}
	public void testExpressionInvalidWithDollarOldWithDollarThisInsideParens() {
		TestContext context = new TestContextImpl("$old($this)", "$old($this)", makeTestFooObject(), makeTestTarget(), makeTestArgs(), makeTestResult(), null, -1);
		TestResult testResult = interpreter.validateTestExpression("$old($this)", context); 
		assertTestPassed(testResult);
	}
	public void testExpressionInvalidWithDollarOldWithDollarTargetInsideParens() {
		TestContext context = new TestContextImpl("$old($target)", "$old($target)", makeTestFooObject(), makeTestTarget(), makeTestArgs(), makeTestResult(), null, -1);
		TestResult testResult = interpreter.validateTestExpression("$old($target)", context); 
		assertTestPassed(testResult);
	}

	public void testExpressionInvalidWithSpacesBetweenDollarAndThis() {
		doTestExpressionInvalidWithSpacesAfterTheDollar("$ this");
	}
	public void testExpressionInvalidWithSpacesBetweenDollarAndTarget() {
		doTestExpressionInvalidWithSpacesAfterTheDollar("$ target");
	}
	public void testExpressionInvalidWithSpacesBetweenDollarAndReturn() {
		doTestExpressionInvalidWithSpacesAfterTheDollar("$ return");
	}
	public void testExpressionInvalidWithSpacesBetweenDollarAndArgs() {
		doTestExpressionInvalidWithSpacesAfterTheDollar("$ args[0]");
	}
	public void testExpressionInvalidWithSpacesBetweenDollarAndOld() {
		doTestExpressionInvalidWithSpacesAfterTheDollar("$ old");
	}
	public void testExpressionInvalidWithSpacesBetweenDollarAndOldAndThis() {
		doTestExpressionInvalidWithSpacesAfterTheDollar("$ old ( $ this )");
	}
	private void doTestExpressionInvalidWithSpacesAfterTheDollar(String testExpression) {
		TestContext context = new TestContextImpl(testExpression, null, null, null, null, null, null, -1);
		TestResult testResult = interpreter.validateTestExpression(testExpression, context); 
		assertTestFailed(testResult, "Test Expression contains whitespace between \"$\" and one or more keywords");
	}

	public void testExpressionInvalidWithDollarAndUnknownKeywordFoo() {
		doTestExpressionInvalidWithUnknownKeywords("$foo");
	}
	public void testExpressionInvalidWithDollarAndUnknownKeywordObject() {
		doTestExpressionInvalidWithUnknownKeywords("$object");
	}
	public void testExpressionInvalidWithDollarAndMisspelledKeywordThis() {
		doTestExpressionInvalidWithUnknownKeywords("$thisx");
	}
	public void testExpressionInvalidWithDollarAndMisspelledKeywordTarget() {
		doTestExpressionInvalidWithUnknownKeywords("$targetx");
	}
	public void testExpressionInvalidWithDollarAndMisspelledKeywordArgs() {
		doTestExpressionInvalidWithUnknownKeywords("$argsx");
	}
	public void testExpressionInvalidWithDollarAndMisspelledKeywordArgs2() {
		doTestExpressionInvalidWithUnknownKeywords("$arg[0]");
	}
	public void testExpressionInvalidWithDollarAndMisspelledKeywordReturns() {
		doTestExpressionInvalidWithUnknownKeywords("$returnsx");
	}
	public void testExpressionInvalidWithDollarAndUnknownKeywordResult() {
		doTestExpressionInvalidWithUnknownKeywords("$result");
	}
	public void testExpressionInvalidWithDollarAndSeveralUnknownKeyword() {
		doTestExpressionInvalidWithUnknownKeywords("$foo $object $thisx $targetx $argsx $returnx $result");
	}
	private void doTestExpressionInvalidWithUnknownKeywords(String testExpression) {
		// We need to pass an args array, because JRuby won't care if the keyword is misspelled, but it will
		// complain if the args Instance[] is empty!
		Instance[] args = new Instance[] { new Instance("arg1", String.class, "arg1") };
		TestContext context = new TestContextImpl(testExpression, null, null, null, args, null, null, -1);
		TestResult testResult = interpreter.validateTestExpression(testExpression, context); 
		if (SystemUtils.isJRuby()) {
			assertTestPassed(testResult);
		} else {
			assertTestFailed(testResult, "Test Expression contains unrecognized \"$\" keywords");
		}
	}

	private void assertTestFailed(TestResult testResult,
			String expectedErrorMessage) {
		assertFalse(testResult.toString(), testResult.isPassed());
		assertTrue(testResult.toString(), testResult.getMessage().contains(expectedErrorMessage));
	}

	private void assertTestPassed(TestResult testResult) {
		assertTrue(testResult.toString(), testResult.isPassed());
		assertEquals("", testResult.getMessage());
	}

	
	private Instance makeTestFooObject() {
		return new Instance ("Foo", Foo.class, new Foo("object", "object2", 1));
	}
	
	private Instance makeTestStringObject() {
		return new Instance ("Foo", String.class, "object");
	}
	
	private Instance makeTestTarget() {
		return new Instance ("value1", String.class, "value1");
	}
	
	private Instance[] makeTestArgs() {
		return new Instance[] {
				new Instance ("firstArg",  Integer.TYPE, new Integer(1)), 
				new Instance ("secondArg", Integer.TYPE, new Integer(2)),
				new Instance ("thirdArg",  Integer.TYPE, new Integer(3)),
			}; 
	}

	private Instance makeTestResult() {
		return new Instance ("result", String.class, "result");
	}
	
	
	public void testArithmeticAndBooleanExpressionPasses() {
		doTest2 (true, "1+1 == 2", null, null, null, null, null);
		doTest2 (true, "1+1 >  1", null, null, null, null, null);
	}

	public void testArithmeticAndBooleanExpressionWithArgsPasses() {
		Instance[] args = makeTestArgs();
		doTest2 (true, "$args[0] + $args[1] >=  $args[2]",  null, null, null, args, null);
		doTest2 (true, "firstArg + secondArg >= thirdArg",  null, null, null, args, null);
		doTest2 (true, "$args[2] - $args[0] ==  $args[1]",  null, null, null, args, null);
		doTest2 (true, "thirdArg - firstArg ==  secondArg", null, null, null, args, null);
		doTest2 (true, "$args[2] != null",  null, null, null, args, null);
		doTest2 (true, "thirdArg != null",  null, null, null, args, null);
	}
	
	public void testArithmeticAndBooleanExpressionWithTargetAndResultArgPasses() {
		Instance   target    = makeTestTarget();
		Instance   result    = makeTestResult();
		Instance[] resultArg = new Instance[] { new Instance("arg1", String.class, "result")};
		doTest2 (true, "$target.length() == $args[0].length()", null, null,   target, resultArg, null);
		doTest2 (true, "$target.length() == arg1.length()",     null, null,   target, resultArg, null);
		doTest2 (true, "$return.length() == $args[0].length()", null, null,   null,   resultArg, result);
		doTest2 (true, "$return.length() == arg1.length()",     null, null,   null,   resultArg, result);
		doTest2 (true, "$target.length() == $args[0].length()", null, null,   target, resultArg, null);
		doTest2 (true, "$target.length() == arg1.length()",     null, null,   target, resultArg, null);
	}
	
	public void testEqualityOfObjectsPassesWithEqualObjects() {
		Instance   foo       = makeTestFooObject();
		Instance   fooString = makeTestStringObject();
		Instance   target    = makeTestTarget();
		Instance   result    = makeTestResult();
		Instance[] resultArg = new Instance[] { new Instance("arg1", String.class, "result")};
		doTest2 (true,  "$this.equals($target)", null, foo, foo, null, null);
		doTest2 (true,  "$target.equals($this)", null, foo, foo, null, null);
		doTest2 (true,  "$this.equals($target)", null, fooString, fooString, null, null);
		doTest2 (true,  "$target.equals($this)", null, fooString, fooString, null, null);
		doTest2 (false, "$this.equals($target)", null, foo, fooString, null, null);
		doTest2 (false, "$target.equals($this)", null, foo, fooString, null, null);
		doTest2 (false, "$this.equals($target)", null, fooString, foo, null, null);
		doTest2 (false, "$target.equals($this)", null, fooString, foo, null, null);
		doTest2 (true,  "$target.compareTo($return) > 0",   null, null, target, null, result);
		doTest2 (true,  "$return.compareTo($target) < 0",   null, null, target, null, result);
		doTest2 (true,  "$args[0].compareTo($return) == 0", null, null, null, resultArg, result);
		doTest2 (true,  "arg1.compareTo($return)     == 0", null, null, null, resultArg, result);
		doTest2 (true,  "$this.compareTo($target) < 0",     null, fooString, target, resultArg, result);
		doTest2 (true,  "$args[0].compareTo($return) == 0", null, fooString, target, resultArg, result);
		doTest2 (true,  "$this.compareTo($target) < 0 && $args[0].compareTo($return) == 0", null, fooString, target, resultArg, result);
		doTest2 (true,  "$this.compareTo($target) < 0 && arg1.compareTo($return)     == 0", null, fooString, target, resultArg, result);

		doTest2 (true,  "$old($this).compareTo($old($target)) < 0", null, fooString, target, null, null);
	}

	// Notice that treating "simpleName" or "simple_name" as fields doesn't seem to work with JRuby. You have
	// to use "getSimpleName()". This may reflect limitations in JRuby's handling of Java reflection(??).
	public void testUseOfReflectionInTests() {
		Instance fooInstance    = new Instance("foo", Foo.class, new Foo());
		doTest2 (true, "$this.getClass().getSimpleName().equals(\"Foo\")", null, fooInstance, null, null, null);
		if (!SystemUtils.isJRuby()) {
			doTest2 (true, "$this.class.simpleName.equals(\"Foo\")",  null, fooInstance, null, null, null);
		}
	}
	
	public void testReferenceToBareFieldOnlyWorksIfNameMatchesItemName() {
		// A bare reference to a field? Only recognized if the field name matches
		// the itemName and the enclosing object is passed as the instance.
		String value1Equals     = "value1.equals(\"value1\")";
		Instance fooInstance    = new Instance("foo", Foo.class, new Foo());
		Instance value1Instance = new Instance("value1", String.class, new Foo().value1);
		doTest2 (false, value1Equals, null,     fooInstance, null,           null, null);
		doTest2 (true,  value1Equals, "value1", fooInstance, null,           null, null);
		doTest2 (true,  value1Equals, "value1", fooInstance, value1Instance, null, null);
		doTest2 (false, value1Equals, null,     null,        value1Instance, null, null);
		doTest2 (false, value1Equals, "value1", null,        value1Instance, null, null);
	}
	
	public void testReferenceToFieldWithThisOnlyWorksIfNameMatchesItemName() {
		Instance fooInstance    = new Instance("foo", Foo.class, new Foo());
		doTest2 (true, "value1.equals(\"value1\")", "value1", fooInstance, null, null, null);
	}
	
	public void testOldReferenceToFieldWithThisOnlyWorksIfNameMatchesItemName() {
		Instance fooInstance  = new Instance("foo", Foo.class, new Foo());
		doTest2 (true, "$old($this.value1).equals(\"value1\")", "value1", fooInstance, null, null, null);
		doTest2 (true, "$old($this.value1).equals(\"value1\")", null, fooInstance, null, null, null);
		doTest2 (true, "$old(value1).length() == 6 && $old(value2).length() == 6", 
				                                                 null, fooInstance, null, null, null);
		doTest2 (true, "$old(value1).equals(\"value1\")",        null, fooInstance, null, null, null);
		doTest2 (true, "$old($this.value1).equals(\"value1\")",  null, fooInstance, null, null, null);
		doTest2 (true, "$old($this).value1.equals(\"value1\")",  null, fooInstance, null, null, null);
		doTest2 (true, "$old($this.getValue1()).equals(\"value1\")", null, fooInstance, null, null, null);
		doTest2 (true, "$old($this).getValue1().equals(\"value1\")", null, fooInstance, null, null, null);
		doTest2 (true, "$old(value1).equals(\"value1\") && $old(value2).equals(\"value2\")",
				                                                     null, fooInstance, null, null, null);
		doTest2 (true, "$old($this.value1).equals(\"value1\") && $old($this.value2).equals(\"value2\")",
				                                                     null, fooInstance, null, null, null);
	}
	
	public void testOutOfBoundsArgsCauseTestsToFail() {
		Instance[] args = makeTestArgs();
		// TODO figure out why Jexl reports an error *sometimes*; in eclipse it doesn't, but in the ant build it does!
		if (SystemUtils.isJexl()) return;
//		boolean willPass = SystemUtils.isJexl() && !SystemUtils.isBSF() ? true : false; 
		boolean willPass = false; 
		doTest2 (willPass, "$args[3] == null",  null, null, null, args, null);
		doTest2 (willPass, "fourthArg == null", null, null, null, args, null);
	}
	
	public void testBareFieldReferencesFailWithoutThis() {
		Instance fooInstance    = new Instance("foo", Foo.class, new Foo());
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
		TestContext context = new TestContextImpl(itemName, itemName, object, target, args, result, "", 0);
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
		private   int getWithAccessorPrivate()   { return withAccessor; }
		          int getWithAccessorDefault()   { return getWithAccessorPrivate(); }
		protected int getWithAccessorProtected() { return getWithAccessorPrivate(); }
		public    int getWithAccessorPublic()    { return getWithAccessorPrivate(); }
		public    int getWithAccessor()          { return getWithAccessorPrivate(); }
	}
	
	public void testProtectionControlsOfAttributesInExpressions() {
		if (SystemUtils.isJexl())
			return;
		AccessorTester at = new AccessorTester();
		Instance slaInstance = new Instance ("at", AccessorTester.class, at);

		// Only a public getter method will work:
		Instance withAccInstance = new Instance ("withAccessor", Integer.TYPE, at.withAccessor);
		boolean willPass = SystemUtils.isGroovy() || (SystemUtils.isJexl() && SystemUtils.isBSF()); 
		doTest2(willPass, "$this.withAccessorDefault   > 5", "withAccessor", slaInstance, withAccInstance, null, null);
		doTest2(willPass, "$this.withAccessorPrivate   > 5", "withAccessor", slaInstance, withAccInstance, null, null);
		doTest2(willPass, "$this.withAccessorProtected > 5", "withAccessor", slaInstance, withAccInstance, null, null);
		doTest2(true, "$this.withAccessorPublic    > 5", "withAccessor", slaInstance, withAccInstance, null, null);
		doTest2(true, "$this.withAccessor          > 5", "withAccessor", slaInstance, withAccInstance, null, null);

		Instance noAccInstanceDefault = new Instance ("withNoAccessorDefault", Integer.TYPE, at.withNoAccessorDefault);
		doTest2(willPass, "$this.withNoAccessorDefault > 5", "withNoAccessorDefault", slaInstance, noAccInstanceDefault, null, null);
		Instance noAccInstancePrivate = new Instance ("withNoAccessorPrivate", Integer.TYPE, at.withNoAccessorPrivate);
		doTest2(willPass, "$this.withNoAccessorPrivate > 5", "withNoAccessorPrivate", slaInstance, noAccInstancePrivate, null, null);
		Instance noAccInstanceProtected = new Instance ("withNoAccessorProtected", Integer.TYPE, at.withNoAccessorProtected);
		doTest2(willPass, "$this.withNoAccessorProtected > 5", "withNoAccessorProtected", slaInstance, noAccInstanceProtected, null, null);
		Instance noAccInstancePublic = new Instance ("withNoAccessorPublic", Integer.TYPE, at.withNoAccessorPublic);
		doTest2(SystemUtils.isGroovy() || SystemUtils.isJRuby(), 
				"$this.withNoAccessorPublic > 5", "withNoAccessorPublic", slaInstance, noAccInstancePublic, null, null);
	}
	
	/*
	 * Test method for 'org.contract4j5.interpreter.jexl.JexlExpressionInterpreter.captureOldValues(String testExpression, TestContext context)'
	 */
	public void testCaptureOldValues1() {
		Instance object = new Instance ("old_this",   String.class, new String("old_this"));
		Instance target = new Instance ("old_target", String.class, new String("old_target"));
		Instance result = new Instance ("", String.class, new String("result"));
		Instance[] args = makeTestArgs(); 
		TestContext context = 
			new TestContextImpl("itemName", "", object, target, args, result, "", 0);
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

	public void testCaptureOldValuesIgnoresOrFailsWithBogusField() {
		try {
			Map<String, Object> map = interpreter.determineOldValues("$old($this.bogus)", makeTestContextForCaptureOldValueTest());
			if (!SystemUtils.isJexl())
				fail();		// Groovy more strict about parsing these values!
			assertEquals  (1, map.size());
			assertNull    (map.toString(), map.get("$this.bogus"));
		} catch (TestSpecificationError tse) {
			// expected
		}
	}
	
	public void testCaptureOldValuesIgnoresOrFailsWithBogusReference() {
		try {
			Map<String, Object> map = interpreter.determineOldValues("$old(bogus)", makeTestContextForCaptureOldValueTest());
			assertEquals  (1, map.size());
			assertNull    (map.toString(), map.get("bogus"));
			if (!SystemUtils.isJexl())
				fail();		// Groovy more strict about parsing these values!
		} catch (TestSpecificationError tse) {
			// expected
		}
	}
	
	// Even though "number" is a public field in Foo, Jexl doesn't see it! An accessor method is required.
	public void testCaptureOldValuesIgnoresForPrivateField() {
		// TODO More unpredictable Jexl behavior...
		if (SystemUtils.isJexl()) return;
		Map<String, Object> map = interpreter.determineOldValues("$old($this.number)", makeTestContextForCaptureOldValueTest());
		assertEquals  (1, map.size());
		assertNotNull ("engine: "+SystemUtils.getScriptingEngineName()+", map: "+map.toString(), map.get("$this.number"));
	}
	
	public void testCaptureOldValuesIgnoresForPrivateBareField() {
		// TODO More unpredictable Jexl behavior...
		if (SystemUtils.isJexl()) return;
		Map<String, Object> map = interpreter.determineOldValues("$old(number)", makeTestContextForCaptureOldValueTest());
		assertEquals  (1, map.size());
		assertNotNull (map.toString(), map.get("number"));
	}
	
	public void testCaptureOldValuesForMethodCallWithThis() {
		Map<String, Object> map = interpreter.determineOldValues("$old($this.setAndGetNumber(2))", makeTestContextForCaptureOldValueTest());
		assertEquals  (1, map.size());
		assertNotNull (map.toString(), map.get("$this.setAndGetNumber(2)"));
		if (SystemUtils.isJRuby())
			assertEquals  (2L, map.get("$this.setAndGetNumber(2)"));
		else
			assertEquals  (2, map.get("$this.setAndGetNumber(2)"));
	}

	public void testCaptureOldValuesForBareMethodCall() {
		// Same, without "$this."
		Map<String, Object> map = interpreter.determineOldValues("$old(setAndGetNumber(2))", makeTestContextForCaptureOldValueTest());
		assertEquals  (1, map.size());
		assertNotNull (map.toString(), map.get("setAndGetNumber(2)"));
		if (SystemUtils.isJRuby())
			assertEquals  (2L, map.get("setAndGetNumber(2)"));
		else
			assertEquals  (2, map.get("setAndGetNumber(2)"));
	}
	
	public void testCaptureOldValuesForFieldThruGetter() {
		Map<String, Object> map = interpreter.determineOldValues("$old($this.getValue1())", makeTestContextForCaptureOldValueTest());
		assertEquals  (1, map.size());
		assertNotNull (map.toString(), map.get("$this.getValue1()"));
		assertEquals  ("value1", map.get("$this.getValue1()"));
	}
	
	public void testCaptureOldValuesForBareField() {
		Map<String, Object> map = interpreter.determineOldValues("$old($this.value1)", makeTestContextForCaptureOldValueTest());
		assertEquals  (1, map.size());
		assertNotNull (map.toString(), map.get("$this.value1"));
		assertEquals  ("value1", map.get("$this.value1"));
	}
	
	public void testCaptureOldValuesForBareFieldWithThis() {
		Map<String, Object> map = interpreter.determineOldValues("$old(value1)", makeTestContextForCaptureOldValueTest());
		assertEquals  (1, map.size());
		assertNotNull (map.toString(), map.get("value1"));
		assertEquals  ("value1", map.get("value1"));
	}
	
	public void testCaptureOldValuesForBareGetter() {
		Map<String, Object> map = interpreter.determineOldValues("$old(getValue1())", makeTestContextForCaptureOldValueTest());
		assertEquals  (1, map.size());
		assertNotNull (map.toString(), map.get("getValue1()"));
		assertEquals  ("value1", map.get("getValue1()"));
	}
	
	public void testCaptureSeveralOldValues() {
		// TODO More unpredictable Jexl behavior...
		if (SystemUtils.isJexl()) return;
		String expr = "$old($this.number) $old(number) $old($this.getValue1()) $old(getValue1()) " +
		"$old($this.value1) $old(value1) $old($this.setAndGetNumber(3)) $old(setAndGetNumber(4)) $this $target $return $args";
		Map<String, Object> map = interpreter.determineOldValues(expr, makeTestContextForCaptureOldValueTest());
		assertEquals  (8, map.size());
		assertNotNull (map.toString(), map.get("$this.number"));
		assertNotNull (map.toString(), map.get("number"));
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
		if (SystemUtils.isJRuby()) {
			assertEquals  (3L, map.get("$this.setAndGetNumber(3)"));
			assertEquals  (4L, map.get("setAndGetNumber(4)"));
		} else {
			assertEquals  (3, map.get("$this.setAndGetNumber(3)"));
			assertEquals  (4, map.get("setAndGetNumber(4)"));
		}
	}

	private TestContextImpl makeTestContextForCaptureOldValueTest() {
		Instance foo1 = new Instance ("foo1", Foo.class, new Foo());
		Instance foo2 = new Instance ("foo2", Foo.class, new Foo("v21", "v22", 2));
		return new TestContextImpl("number", "", foo1, foo2, null, null, "", 0);
	}

}
