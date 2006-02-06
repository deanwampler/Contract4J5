/*
 * Copyright 2005 Dean Wampler. All rights reserved.
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
 * @author Dean Wampler <dean@aspectprogramming.com>
 */

package org.contract4j5.interpreter;

import java.util.Map;

import org.contract4j5.TestContext;
import org.contract4j5.util.reporter.Reporter;

/**
 * The interface for the expression interpreter used to evaluate test expressions.
 * @author Dean Wampler
 */
public interface ExpressionInterpreter {
	/**
	 * Common invalid test expression errors that can be used by 
	 * implementers of this interface. Note that space at end so more than one
	 * can be concatenated together. Note that if implementers or clients of
	 * interpreters define a default test expression when the expression is
	 * empty, then the first message may never be used.
	 * The messages that end in ":" are designed to support appending the 
	 * offending substrings.
	 */
	static enum InvalidTestExpression {
		EMPTY_EXPRESSION               ("The test expression is empty! "),
		THIS_KEYWORD_WITH_NO_INSTANCE  ("\"$this\" present, but the \"instance\" is null. "),
		TARGET_KEYWORD_WITH_NO_TARGET  ("\"$target\" present, but the \"target\" is null. "),
		OLD_KEYWORD_NO_ARGS            ("The \"$old(..)\" keyword 'function' requires a field, method call, \"$this\", or \"$target\" argument, in parentheses. "),
		OLD_KEYWORD_INVALID_ARGS       ("The \"$old(..)\" keyword argument is empty or contains an invalid value. It requires a field, method call, \"$this\", or \"$target\" argument. "),
		RETURN_KEYWORD_WITH_NO_RETURN  ("\"$return\" present, but the \"result\" is null. "),
		ARGS_KEYWORD_WITH_NO_ARGS      ("\"$args\" present, but the \"args\" array is null or empty. "),
		INVALID_WHITESPACE_IN_KEYWORD  ("Test Expression contains whitespace between \"$\" and one or more keywords. "),
		MISSING_DOLLAR_SIGN_IN_KEYWORD ("Test Expression contains possible keywords without the leading \"$\": "),
		UNRECOGNIZED_KEYWORDS          ("Test Expression contains unrecognized \"$\" keywords (mispellings?): "),
		DUPLICATE_INVAR_TEST_EXPRESSIONS ("Two or more invariant test expressions found on corresponding types, methods, or constructors in the type hierarchy that appear to be different. They must be equal (ignoring whitespace): ");
		
		public String toString() { return message; }

		private String message;
		private InvalidTestExpression (String message) { this.message = message; }
	};

	/**
	 * Set whether or not an empty test expression should be treated as "no test", meaning it is
	 * ignored and the test "passes" in all instances. By default, implementers should treat
	 * empty tests as a failure, as they are likely to indicate incomplete test specifications,
	 * thereby reducing the ability of Contract4J to detect errors.
	 * @param emptyOK
	 */
	void setTreatEmptyTestExpressionAsValidTest (boolean emptyOK);
	
	/**
	 * Get whether or not an empty test expression should be treated as "no test", meaning it is
	 * ignored and the test "passes" in all instances. By default, implementers should treat
	 * empty tests as a failure, as they are likely to indicate incomplete test specifications,
	 * thereby reducing the ability of Contract4J to detect errors.
	 * @return true if empty expressions are allowed
	 */
	boolean getTreatEmptyTestExpressionAsValidTest ();
	
	/**
	 * @return the map of optional keyword substitutions, consisting of arbitrary
	 * key strings, which can be regular-expression, and substitutions used to convert
	 * the keys to executable expressions. The keys can be any string and may contain the
	 * special keywords discussed in the following table. One use for these mappings is
	 * to create convenient test shortcut expressions.
	 * <table>
	 *   <tr><td><code>$this</code></td>
	 *   <td>The <code>this</code> object. The corresponding value for this key should
	 *   contain <code>$this</code> so Contract4J can substitute the correct object.
	 *   The reason this mapping is supported here is so additional information can be
	 *   added to the expansion.</td></tr>
	 *   <tr><td><code>$target</code></td>
	 *   <td>The <code>target</code> object. The corresponding value for this key should
	 *   contain <code>$target</code>, like the <code>$this</code>.</td></tr>
	 *   <tr><td><code>$old($this)</code>, <code>$old($target)</code>, 
	 *   or </code>$old(</code><i>field</i><code>)</code></td>
	 *   <td>The "old" value of <code>$this</code>, <code>$target</code>, or an instance field 
	 *   value before a method invocation, <i>etc.</i></td></tr>
	 *   <tr><td><code>$return</code></td>
	 *   <td>The object returned from a method. The corresponding value for this key should
	 *   contain <code>$return</code>, like the <code>$this</code>.</td></tr>
	 *   <tr><td><code>$args[n]</code></td>
	 *   <td>The "nth" argument (0 indexed) to a method. The corresponding value for this key 
	 *   should contain <code>$args[n]</code>, like the <code>$this</code>.</td></tr>
	 * </table>
	 * These special keywords and substitutions are the default substitutions that are made on the 
	 * test expression strings.
	 */
	Map<String, String> getOptionalKeywordSubstitutions();

	/**
	 * @param optionalKeywordSubstitutions map of keywords and regular-expression strings used to
	 * convert those keywords to executable expressions.
	 */
	void setOptionalKeywordSubstitutions (Map<String, String> optionalKeywordSubstitutions);

	/** 
	 * Determine the "old" values required for a test expression and same them in a map so they 
	 * can be compared with new values later. 
	 * The following <code>$old(..)</code> expressions are supported:
	 * <table>
	 * <tr><th>Expression</th><th>Maps To...</th><th>Comments</th></tr>
	 * <tr>
	 *   <td><code>$old($this)</code></td>
	 *   <td>The instance in the <code>context</code></td>
	 *   <td>Not recommended, because <code>this</code> may be changed by the test.</td>
	 * </tr>
	 * <tr>
	 *   <td><code>$old($this.foo)</code></td>
	 *   <td>The <code>foo</code> field (if accessible...) in the instance in the <code>context</code></td>
	 *   <td>Most useful if <code>foo</code> is a primitive or a reference to an immutable object, so that 
	 *   its value won't be changed by the test.</td>
	 * </tr>
	 * <tr>
	 *   <td><code>$old($this.doSomething(bar, baz))</code></td>
	 *   <td>The result returned by the <code>doSomething()</code> method(if accessible...) in the instance 
	 *   in the <code>context</code></td>
	 *   <td>Most useful if <code>doSomething()</code> returns a primitive or a reference to an immutable 
	 *   object, so that the returned value won't be changed by the test. To keep the implementation 
	 *   reasonably simply (<i>e.g.</i>, parsing of "()"), the method call can take arguments, but can't embed
	 *   calls to other methods of any kind.</td>
	 * </tr>
	 * <tr>
	 *   <td><code>$old($target)</code></td>
	 *   <td>The target in the <code>context</code></td>
	 *   <td>Not recommended, because <code>target</code> may be changed by the test.</td>
	 * </tr>
	 * <tr>
	 *   <td><code>$old($target.foo)</code></td>
	 *   <td>The <code>foo</code> field (if accessible...) in the target in the <code>context</code></td>
	 *   <td>Most useful if <code>foo</code> is a primitive or a reference to an immutable object, so that 
	 *   its value won't be changed by the test.</td>
	 * </tr>
	 * <tr>
	 *   <td><code>$old($target.doSomething(bar, baz))</code></td>
	 *   <td>The result returned by the <code>doSomething()</code> method(if accessible...) in the target 
	 *   in the <code>context</code></td>
	 *   <td>Most useful if <code>doSomething()</code> returns a primitive or a reference to an immutable 
	 *   object, so that the returned value won't be changed by the test. The same limitations on method
	 *   arguments described for <code>$old($this.doSomething(bar, baz))</code> apply here.</td>
	 * </tr>
	 * <tr>
	 *   <td><code>$old(foo)</code></td>
	 *   <td><code>$old($this.foo)</code></td>
	 *   <td>Assumed to be a field of <code>this.</code>. Hence, considered equivalent to 
	 *   <code>$old($this.foo)</code></td>
	 * </tr>
	 * <tr>
	 *   <td><code>$old(doSomething())</code></td>
	 *   <td><code>$old($this.doSomething())</code></td>
	 *   <td>Assumed to be a method of <code>this.</code>. Hence, considered equivalent to 
	 *   <code>$old($this.doSomething())</code></td>
	 * </tr>
	 * </table>
	 * In summary, <code>$old()</code> may contain <code>$this</code>, <code>$target</code>, a field reference on 
	 * either or a single method call on either, with <code>$this</code> assumed if neither keyword is present. 
	 * No other expressions are supported. Using them in test expressions will result in unpredictable results 
	 * (depending on the implementation). Note that whitespace is ignored, except that there can be no whitespace
	 * between the "$" characters and their keywords.
	 * @param testExpression that may contain references to "old" values.
	 * @param context of the test.
	 * @return map of the old values and their corresponding names.
	 * @note Only "$old(..)" are processed; other keywords are ignored.
	 */
	Map<String, Object> determineOldValues (String testExpression, TestContext context);

	/**
	 * Validate the input test expression by checking that if it contains any of the 
	 * keywords, there are corresponding valid objects to insert for those keywords. 
	 * "Error" and "warning" conditions are detected. The returned {@link TestResult#isPassed()}
	 * will return false if any error conditions were detected. Otherwise it returns true.
	 * The {@link TestResult#getMessage()} will only be empty if no warnings or errors
	 * were detected.
	 * For example, a warning is reported in the message string if <code>$this</code> is 
	 * present, but "instance" is null. (This can't be treated as an error because, for 
	 * example, the test might <code>$this == null</code> and the input object might 
	 * intentionally be null!) Similarly for <code>$target</code>. However, if
	 * <code>$target</code> is present, then "itemName" can't be null or empty.  
	 * A warning is issued if <code>$return</code> is present and "result" is null. 
	 * A warning is issued iff <code>$args[..]</code> is present and "args" is null or 
	 * the array is empty. 
	 * Errors include unrecognized "keywords", e.g., <code>$retrn</code> mispellings, 
	 * invalid <code>$old</code> expressions, such as those missing parentheses or having
	 * empty parentheses, and expressions with whitespace after the <code>$</code> signs.
	 * If any validation fails, a non-empty string with an appropriate message is returned
	 * as the message returned by {@link TestResult#getMessage()} called on the
	 * returned object. If any of the validation failures are errors, then
	 * {@link TestResult#isPassed()} will return false. If all validations passed with no
	 * warnings, the returned {@link TestResult#isPassed()} will be true and 
	 * the failure message will be empty (but not null!).
	 * @param testExpression
	 * @param context
	 * @return TestResult with any errors in the expression returned by
	 * {@link TestResult#getMessage()} and {@link TestResult#isPassed()} will
	 * be false. If the expression is valid, the "failure message" will be "" and
	 * {@link TestResult#isPassed()} will be true.
	 * TODO also check that there are enough items in the args array to satisfy all 
	 * the array element references in the expression.
	 */
	TestResult validateTestExpression (
			String      testExpression, 
			TestContext context);

	/**
	 * Parse the test expression and invoke the test.
	 * @param testExpression
	 * @param context information including the itemName under test (class, method, or field),
	 * the old and new instance pair of the class under test (may only be null for constructor 
	 * tests or class-wide invariants, the old and new target pair (e.g., a field test), the
	 * array of args passed to the method for method tests (otherwise null or empty), and the
	 * result returned by the method (it will be null except for postcondition tests of methods
	 * not returning void).
	 * @return TestResult indicating whether or not the test passed and an associated message
	 *   and or Throwable.
	 * @throws ExpressionInterpreterError if the expression can't be parsed or the resulting
	 * test can't be executed.
	 */
	TestResult invokeTest (String testExpression, TestContext context);
	
	Reporter getReporter();

	void setReporter (Reporter reporter);
}
