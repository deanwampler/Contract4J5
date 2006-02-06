package org.contract4j5.interpreter.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.contract4j5.Instance;
import org.contract4j5.TestContext;
import org.contract4j5.TestContextImpl;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.ExpressionInterpreterHelper;
import org.contract4j5.interpreter.TestResult;

public class ExpressionInterpreterHelperTest extends TestCase {
	ExpressionInterpreterHelper interpreter = null;
	
	protected void setUp() throws Exception {
		super.setUp();
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
		doTestValidateTestExpression (false, " ", ExpressionInterpreter.InvalidTestExpression.EMPTY_EXPRESSION); 
		doTestValidateTestExpression (true,  " $this ", ExpressionInterpreter.InvalidTestExpression.THIS_KEYWORD_WITH_NO_INSTANCE);
		doTestValidateTestExpression (true,  " $target ", ExpressionInterpreter.InvalidTestExpression.TARGET_KEYWORD_WITH_NO_TARGET);
		doTestValidateTestExpression (true,  " $return ", ExpressionInterpreter.InvalidTestExpression.RETURN_KEYWORD_WITH_NO_RETURN);
		doTestValidateTestExpression (true,  " $args ", ExpressionInterpreter.InvalidTestExpression.ARGS_KEYWORD_WITH_NO_ARGS);
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
		TestResult result = interpreter.validateTestExpression(expr, new TestContextImpl());
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
		doTestExpandKeywords (null,           null,               false, makeContext());
		doTestExpandKeywords ("",             "",                 false, makeContext());
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
			new TestContextImpl("itemName", thiz, target, args, returnz, oldValuesMap);
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
		TestResult result = interpreter.invokeTest(null, new TestContextImpl("foo", null, null, null, null, null));
		assertFalse (result.getMessage(), result.isPassed());
		result = interpreter.invokeTest("foo", new TestContextImpl(null, null, null, null, null, null));
		assertFalse (result.getMessage(), result.isPassed());
		result = interpreter.invokeTest("foo", new TestContextImpl("foo", null, null, null, null, null));
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
