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

import org.contract4j5.configurator.Configurator;
import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.instance.Instance;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.ExpressionInterpreterHelper;
import org.contract4j5.interpreter.TestResult;

public class ExpressionInterpreterHelperTest extends TestCase {
	ExpressionInterpreterHelper interpreter = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		Configurator c = new ConfiguratorForTesting();
		c.configure();
		interpreter = new ExpressionInterpreterStub();
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.ExpressionInterpreterHelper.getOptionalKeywordSubstitutions()'
	 */
	public void testGetOptionalKeywordSubstitutions() {
		assertNull (interpreter.getOptionalKeywordSubstitutions());
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.ExpressionInterpreterHelper.setOptionalKeywordSubstitutions(Map<String, String>)'
	 */
	public void testSetOptionalKeywordSubstitutions() {
		Map<String, String> map = makeOptKeywordSubs(interpreter);
		assertEquals (map, interpreter.getOptionalKeywordSubstitutions());
		assertEquals (2, interpreter.getOptionalKeywordSubstitutions().size());
		assertEquals ("bar", interpreter.getOptionalKeywordSubstitutions().get("foo"));
		assertEquals ("bad", interpreter.getOptionalKeywordSubstitutions().get("good"));
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.ExpressionInterpreterHelper.validateTestExpression(String testExpression, TestContext context)'
	 */
	public void testValidateTestExpression() {
		doTestValidateTestExpression (false, " ", ExpressionInterpreter.InvalidTestExpression.EMPTY_EXPRESSION_ERROR); 
		doTestValidateTestExpression (false, " $this ", ExpressionInterpreter.InvalidTestExpression.THIS_KEYWORD_WITH_NO_INSTANCE);
		doTestValidateTestExpression (false, " $target ", ExpressionInterpreter.InvalidTestExpression.TARGET_KEYWORD_WITH_NO_TARGET);
		doTestValidateTestExpression (false, " $return ", ExpressionInterpreter.InvalidTestExpression.RETURN_KEYWORD_WITH_NO_RETURN);
		doTestValidateTestExpression (false, " $args ", ExpressionInterpreter.InvalidTestExpression.ARGS_KEYWORD_WITH_NO_ARGS);
		doTestValidateTestExpression (false, " $ this ", ExpressionInterpreter.InvalidTestExpression.INVALID_WHITESPACE_IN_KEYWORD);
		doTestValidateTestExpression (false, " $ target ", ExpressionInterpreter.InvalidTestExpression.INVALID_WHITESPACE_IN_KEYWORD);
		doTestValidateTestExpression (false, " $ return ", ExpressionInterpreter.InvalidTestExpression.INVALID_WHITESPACE_IN_KEYWORD);
		doTestValidateTestExpression (false, " $ args ", ExpressionInterpreter.InvalidTestExpression.INVALID_WHITESPACE_IN_KEYWORD);
		doTestValidateTestExpression (false, " $ old ", ExpressionInterpreter.InvalidTestExpression.INVALID_WHITESPACE_IN_KEYWORD);
		doTestValidateTestExpression (false, " $old ", ExpressionInterpreter.InvalidTestExpression.OLD_KEYWORD_NO_ARGS);
		doTestValidateTestExpression (false, " $old ( ) ", ExpressionInterpreter.InvalidTestExpression.OLD_KEYWORD_INVALID_ARGS);
		doTestValidateTestExpression (false, " $old ( x + y ) ", ExpressionInterpreter.InvalidTestExpression.OLD_KEYWORD_INVALID_ARGS);
		doTestValidateTestExpression (false, " $foo ",   ExpressionInterpreter.InvalidTestExpression.UNRECOGNIZED_KEYWORDS);
		doTestValidateTestExpression (false, " $oldd ",  ExpressionInterpreter.InvalidTestExpression.UNRECOGNIZED_KEYWORDS);
		doTestValidateTestExpression (true,  " this ",   ExpressionInterpreter.InvalidTestExpression.MISSING_DOLLAR_SIGN_IN_KEYWORD);
		doTestValidateTestExpression (true,  " target ", ExpressionInterpreter.InvalidTestExpression.MISSING_DOLLAR_SIGN_IN_KEYWORD);
		doTestValidateTestExpression (true,  " return ", ExpressionInterpreter.InvalidTestExpression.MISSING_DOLLAR_SIGN_IN_KEYWORD);
		doTestValidateTestExpression (true,  " args ",   ExpressionInterpreter.InvalidTestExpression.MISSING_DOLLAR_SIGN_IN_KEYWORD);
		doTestValidateTestExpression (true,  " old ",    ExpressionInterpreter.InvalidTestExpression.MISSING_DOLLAR_SIGN_IN_KEYWORD);
	}
	private void doTestValidateTestExpression (boolean shouldPass, String expr, ExpressionInterpreter.InvalidTestExpression expected) {
		TestResult result = interpreter.validateTestExpression(expr, 
				new TestContextImpl("", "", null, null, new Instance[0], null, "", 0));
		String msg = result.getMessage();
		assertEquals (shouldPass, result.isPassed());
		assertTrue   (msg, msg.contains(expected.toString()));
	}
	
	/*
	 * Test method for 'org.contract4j5.interpreter.ExpressionInterpreterHelper.captureOldValues(String testExpression, TestContext context)'
	 */
	public void testCaptureOldValues() {
		// Even though "context" will have an "old values" map with strings like 
		// "old_this", determineOldValues() won't use it, because in fact it is 
		// called to create it in the first place, in normal usage. Instead,
		// the "my_this" instance and "my_target" target will be used.
		TestContext context = makeContext();
		String expr = "$this $old($this) $target $old($target) $return, $args[0], $args[1]";
		Map<String, Object> map = interpreter.determineOldValues(expr, context);
		assertEquals  (map.toString(), 2, map.size());
		assertNotNull (map.toString(), map.get("$this"));
		assertEquals  (map.toString(), "my_this", map.get("$this"));  
		assertNotNull (map.toString(), map.get("$target"));
		assertEquals  (map.toString(), "my_target", map.get("$target"));

		expr = "$this $old($this) $target $return, $args[0], $args[1]";
		map = interpreter.determineOldValues(expr, context);
		assertEquals  (map.toString(), 1, map.size());
		assertNotNull (map.toString(), map.get("$this"));
		assertEquals  (map.toString(), "my_this", map.get("$this"));

		expr = "$this $target $old($target) $return, $args[0], $args[1]";
		map = interpreter.determineOldValues(expr, context);
		assertEquals  (map.toString(), 1, map.size());
		assertNotNull (map.toString(), map.get("$target"));
		assertEquals  (map.toString(), "my_target", map.get("$target"));

		expr = "$this $target $return, $args[0], $args[1]";
		map = interpreter.determineOldValues(expr, context);
		assertEquals  (map.toString(), 0, map.size());
	}
		
	/*
	 * Test method for 'org.contract4j5.interpreter.ExpressionInterpreterHelper.expandKeywords(String, String, Object, Object[], Object)'
	 */
	public void testExpandKeywords() {
		doTestExpandKeywords ("No test expression!", null,        false, makeContext());
		doTestExpandKeywords ("No test expression!", "",          false, makeContext());
		doTestExpandKeywords ("c4jThis",      "$this",            true,  makeContext());
		doTestExpandKeywords ("c4jExprVar1",  "$old ($this)",     true,  makeContext());
		doTestExpandKeywords ("c4jTarget",    "$target",          true,  makeContext());
		doTestExpandKeywords ("c4jExprVar2",  "$old ($target)",   true,  makeContext());
		doTestExpandKeywords ("c4jReturn",    "$return",          true,  makeContext());
		doTestExpandKeywords ("c4jArgs[0]",   "$args[0]",         true,  makeContext());
		doTestExpandKeywords ("c4jArgs[1]",   "$args[1]",         true,  makeContext());
		doTestExpandKeywords ("c4jExprVar1.compareTo(c4jExprVar2) < 0", "$old($this).compareTo($old($target)) < 0", true, makeContext());
		doTestExpandKeywords ("c4jThis.itemName", "itemName",     true,  makeContext());
	}
	
	public void testSubstituteInTestExpression() {
		assertEquals ("X\"def\"X\"jk\\\"l\"X", 
				interpreter.substituteInTestExpression("abc\"def\"ghi\"jk\\\"l\"mno", "[a-z]+", "X"));
		assertEquals ("\"def\"X\"jk\\\"l\"", 
				interpreter.substituteInTestExpression("\"def\"ghi\"jk\\\"l\"", "[a-z]+", "X"));
	}
	
	public void test() {
		assertEquals ("abcghimno", 
				interpreter.removeQuotedStrings("abc\"def\"ghi\"jk\\\"l\"mno"));
		assertEquals ("ghi", 
				interpreter.removeQuotedStrings("\"def\"ghi\"jk\\\"l\""));
	}
	
	// Experiment to confirm our handling of quoted strings w/ embedded escaped quotes.
	public void testJavaRegex() {
		String s = "\"abc\"def\"gh\\\"i\"";
//		System.out.println("start: "+s);
		String[] ss = s.split("(?<!\\\\)\"");
		assertEquals("", ss[0]);
		assertEquals("abc", ss[1]);
		assertEquals("def", ss[2]);
		assertEquals("gh\\\"i", ss[3]);
	}
	public void testExpandKeywordsDoesNotExpandContentsOfQuotedStrings() {
		doTestExpandKeywords ("c4jThis.equals(\"$old($this)\")",  "$this.equals(\"$old($this)\")", true,  makeContext());
		doTestExpandKeywords ("c4jThis.equals(\"$this\")",        "$this.equals(\"$this\")", true,  makeContext());
		doTestExpandKeywords ("\"$this\" c4jArgs[0] \"$target\"", "\"$this\" $args[0] \"$target\"", true,  makeContext());
	}


	private TestContext makeContext() {
		Instance[] args = new Instance[] {
				new Instance ("my_arg1", String.class, new String("my_arg1")), 
				new Instance ("my_arg2", String.class, new String("my_arg2"))
			}; 
		Instance thiz        = new Instance ("my_this",   String.class, "my_this");
		Instance target      = new Instance ("my_target", String.class, "my_target");
		Instance returnz     = new Instance ("my_return", String.class, "my_return");
		String   oldInstance = "old_this";
		String   oldTarget   = "old_target";
		Map<String, Object> oldValuesMap = new HashMap<String, Object>();
		oldValuesMap.put ("$this",   oldInstance);
		oldValuesMap.put ("$target", oldTarget);
		TestContext context = 
			new TestContextImpl("itemName", "itemName", thiz, target, args, returnz, oldValuesMap, "", 0);
		return context;
	}
	
	private void doTestExpandKeywords (String expected, String key, boolean shouldPass, TestContext context) {
		assertEquals (shouldPass, interpreter.expandKeywords(key, context).isPassed());
		String msg = interpreter.expandKeywords(key, context).getMessage();
		assertEquals ("Actual: \""+msg+"\". ", expected,   msg);
	}

	public void testSubstituteArguments() {
		doTestSubstituteArguments ("", null);
		doTestSubstituteArguments ("c4jThis != null", null);
		doTestSubstituteArguments ("c4jThis.my_arg1 != null && c4jThis.my_arg2 != null", null);
		doTestSubstituteArguments (
				"my_arg1 != null && c4jThis.my_arg2 != null", 
				"c4jArgs[0] != null && c4jThis.my_arg2 != null");
		doTestSubstituteArguments (
				"c4jThis.my_arg1 != null && my_arg2 != null", 
				"c4jThis.my_arg1 != null && c4jArgs[1] != null");
		doTestSubstituteArguments (
				"my_arg1 != null && my_arg2 != null", 
				"c4jArgs[0] != null && c4jArgs[1] != null");
	}
	
	protected void doTestSubstituteArguments(String original, String expected) {
		TestContext context = makeContext();
		if (expected == null) { expected = original; }
		String actual = interpreter.substituteArguments(original, context);
		assertEquals (actual, expected, actual);
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.ExpressionInterpreterHelper.invokeTest(String, String, Object, Object[], Object)'
	 */
	public void testInvokeTest() {
		TestContext fooContext = new TestContextImpl("foo", "", null, null, new Instance[0], null, "", 0);
		TestResult result = interpreter.invokeTest(null, fooContext);
		assertFalse (result.getMessage(), result.isPassed());
		TestContext nullContext = new TestContextImpl(null, "", null, null, new Instance[0], null, "", 0);
		result = interpreter.invokeTest("foo", nullContext);
		assertFalse (result.getMessage(), result.isPassed());
		result = interpreter.invokeTest("foo", fooContext);
		assertFalse  (result.getMessage(), result.isPassed());
	}
	
	private Map<String, String> makeOptKeywordSubs (ExpressionInterpreterHelper interpreter) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("foo", "bar");
		map.put("good", "bad");
		interpreter.setOptionalKeywordSubstitutions(map);
		return map;
	}
}
