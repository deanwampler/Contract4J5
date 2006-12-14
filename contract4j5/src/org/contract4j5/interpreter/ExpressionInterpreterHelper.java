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
 * @author Dean Wampler <mailto: dean@aspectprogramming.com>
 */

package org.contract4j5.interpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.contract4j5.context.TestContext;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.instance.Instance;
import org.contract4j5.instance.InstanceUtils;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.Severity;


/**
 * An abstract helper class that implements the common boiler plate for the 
 * expression interpreter. It uses a Template Method pattern and calls abstract 
 * methods to provide specific support for user-provided interpreters.
 * @author Dean Wampler <mailto: dean@aspectprogramming.com>
 */
abstract public class ExpressionInterpreterHelper implements ExpressionInterpreter {

	boolean treatEmptyTestExpressionAsValidTest = false;
	public boolean getTreatEmptyTestExpressionAsValidTest() {
		return treatEmptyTestExpressionAsValidTest;
	}
	
	public void setTreatEmptyTestExpressionAsValidTest(boolean emptyOK) {
		treatEmptyTestExpressionAsValidTest = emptyOK;
	}
	
	private Map<String, String> optionalKeywordSubstitutions = null;
	
	/* (non-Javadoc)
	 * @see org.contract4j5.interpreter.ExpressionInterpreter#getOptionalKeywordSubstitutions()
	 */
	public Map<String, String> getOptionalKeywordSubstitutions() {
		return optionalKeywordSubstitutions;
	}

	/* (non-Javadoc)
	 * @see org.contract4j5.interpreter.ExpressionInterpreter#setOptionalKeywordSubstitutions(java.util.Map)
	 */
	public void setOptionalKeywordSubstitutions(Map<String, String> optionalKeywordSubstitutions) {
		this.optionalKeywordSubstitutions = optionalKeywordSubstitutions;
	}

	/* (non-Javadoc)
	 * @see org.contract4j5.interpreter.ExpressionInterpreter#captureOldValues(java.lang.String, org.contract4j5.TestContext)
	 */
	public Map<String, Object> determineOldValues(
		String      testExpression, 
		TestContext context) {
		Map<String, Object> map = new HashMap<String, Object>();
		// Find "$old()", "$old($this)", "$old($this.foo)", "$old($this.doFoo(bar,baz))", etc.
		// See Javadocs for parent class declaration.
		Pattern p = Pattern.compile("\\$old\\s*\\([^\\(\\)]*(\\([^\\)]*\\))?[^\\(\\)]*\\)"); 
		Matcher m = p.matcher(testExpression);
		while (m.find()) {
			String s1 = m.group();
			String expr = s1.substring(s1.indexOf('(')+1, s1.length()-1); // grab everything between '(' and ')'
			expr = expr.trim();
			map.put(expr, determineOldValue(expr, context)); 
		}
		return map;
	}
	
	/**
	 * From the context, return an "old" object specified by the input expression string.
	 * @param exprStr that specifies at most one object. Do not wrap in "$old(..)".
	 * @param context which is used to determine the object to return.
	 * @return the found object or null if not determinable.
	 */
	protected Object determineOldValue (String exprStr, TestContext context) {
		String newExprStr = null;
		if (exprStr.startsWith("$this")) {
			newExprStr = exprStr.replaceAll("\\$this", "c4jOldThis");
		} else if (exprStr.startsWith("$target")) {
			newExprStr = exprStr.replaceAll("\\$target", "c4jOldTarget");
		} else {
			// Assume it's a "bare" field or method on "this":
			newExprStr = "c4jOldThis." + exprStr; 
		}
		Instance obji   = context.getInstance();
		Instance fieldi = context.getField();
		Object   obj    = obji   != null ? obji.getValue()   : null;
		Object   field  = fieldi != null ? fieldi.getValue() : null;
		recordContextChange ("c4jOldThis",   obj);
		recordContextChange ("c4jOldTarget", field);
		Object result = doDetermineOldValue (newExprStr, context);
		removeContextChange ("c4jOldThis");
		removeContextChange ("c4jOldTarget");
		return result;
	}
	
	/**
	 * After making substitutions, call the interpreter to evaluate the expression
	 * to return an object to save for later comparison. 
	 * @param exprStr that specifies at most one object. Should require no further processing.
	 * @param context which is used to determine the object to return.
	 * @return the found object or null if not determinable.
	 */
	abstract protected Object doDetermineOldValue (String exprStr, TestContext context);

	/** 
	 * Template method that provides some services, then calls abstract methods to complete
	 * test string parsing and test invocation.
	 * @see org.contract4j5.interpreter.ExpressionInterpreter#invokeTest(java.lang.String, org.contract4j5.context.TestContext)
	 */
	public TestResult invokeTest(
			String      testExpression, 
			TestContext context) {
		TestResult testResult = validateTestExpression (testExpression, context);
		if (testResult.getMessage().length() > 0) { 
			// Log with INFO level, rather than WARN, because they may not be real issues.
			getReporter().report(Severity.INFO, ExpressionInterpreterHelper.class,
					testResult.getMessage());
		}
		if (testResult.isPassed() == false || empty (testExpression)) {
			return testResult;
		} 
		testResult = expandKeywords (testExpression, context);
		if (testResult.isPassed() == false) {
			return testResult;
		}
		String expr = testResult.getMessage();
		getReporter().report(Severity.DEBUG, ExpressionInterpreterHelper.class,
				"Invoking test (expanded): "+expr);
		testResult = doTest(expr, context);
		cleanupContext();
		return testResult;
	}

	/* (non-Javadoc)
	 * @see org.contract4j5.interpreter.ExpressionInterpreter#validateTestExpression(java.lang.String, org.contract4j5.TestContext)
	 */
	public TestResult validateTestExpression (
			String      testExpression, 
			TestContext context) {
		String errStr = "";
		String warnStr = "";
		if (empty(testExpression)) {
			warnStr = "";
			errStr  = InvalidTestExpression.EMPTY_EXPRESSION_ERROR.toString();
			if (getTreatEmptyTestExpressionAsValidTest()) { 
				// If here, consider empty test "valid"; just warn about it.
				warnStr = InvalidTestExpression.EMPTY_EXPRESSION_WARNING.toString();
				errStr  = "";
			}
			return makeValidateTestExpressionReturn (warnStr, errStr);
		} 
		testExpression = testExpression.trim();
		
		if (testExpression.contains("$this")) {
			Instance i = context.getInstance();
			if (i == null || i.getValue() == null) {
				warnStr += InvalidTestExpression.THIS_KEYWORD_WITH_NO_INSTANCE.toString();
			}
		}
		if (testExpression.contains("$target")) {
			Instance i = context.getField();
			if (i == null || i.getValue() == null) {
				warnStr += InvalidTestExpression.TARGET_KEYWORD_WITH_NO_TARGET.toString();
			}
		}
		if (testExpression.contains("$return") && context.getMethodResult() == null) {
			warnStr += InvalidTestExpression.RETURN_KEYWORD_WITH_NO_RETURN.toString();
		}
		Object[] args = context.getMethodArgs();
		if (testExpression.contains("$args") && (args == null || args.length == 0)) {
			warnStr += InvalidTestExpression.ARGS_KEYWORD_WITH_NO_ARGS.toString();
		}
		if (testExpression.contains("$old")) {
			if (testExpression.matches("\\$old\\s*[^\\(]+")) {  // "$old ..." w/out "(..)"
				errStr += InvalidTestExpression.OLD_KEYWORD_NO_ARGS.toString();			
			} else if (testExpression.matches("\\$old\\s*$")) {  // "$old" w/out "(..)"
				errStr += InvalidTestExpression.OLD_KEYWORD_NO_ARGS.toString();			
			} 
			if (testExpression.matches("\\$old\\s*\\(\\s*\\)")) { // $old() (no arg)
				errStr += InvalidTestExpression.OLD_KEYWORD_INVALID_ARGS.toString();			
			} else { 
				Pattern p = Pattern.compile("\\$old\\s*\\(\\s*[^\\(\\)]*(\\([^\\)]*\\))?[^\\(\\)]*\\)"); 
				Matcher m = p.matcher(testExpression);
				while (m.find()) {
					String s1 = m.group();
					String expr = s1.substring(s1.indexOf('(')+1, s1.length()-1); // grab everything between '(' and ')'
					expr = expr.trim();
					if (expr.matches(".*[+\\-*/<>=~\\^|&%#@!,:;?{}].*")) { // $old(..) with nonvalid arg?
						errStr += InvalidTestExpression.OLD_KEYWORD_INVALID_ARGS.toString();
						break;
					}
				}
			}
		}
		if (testExpression.matches("\\$\\s+.*")) { // '$' followed by whitespace
			errStr += InvalidTestExpression.INVALID_WHITESPACE_IN_KEYWORD.toString();			
		}
		Pattern p1 = Pattern.compile ("\\$\\w+");
		Matcher m1 = p1.matcher (testExpression);
		boolean firstBad = true;
		String badKeyWords = "";
		while (m1.find()) {
			String match = m1.group();
			if (match.equals("$this")   || match.equals("$target") ||
				match.equals("$return") || match.equals("$args")   ||
				match.equals("$old")) {
				continue;
			}
			if (firstBad) {
				firstBad = false;
			} else {
				badKeyWords += ", ";
			}
			badKeyWords += match;
		}
		if (badKeyWords.length() > 0) {
			errStr += InvalidTestExpression.UNRECOGNIZED_KEYWORDS.toString()+badKeyWords+". ";			
		}
		Pattern p2 = Pattern.compile ("(?:(^|[^\\$]))\\s*(this|target|args|return|old)");
		Matcher m2 = p2.matcher (testExpression);
		boolean firstBadDollar = true;
		String  badDollarKeyWords = "";
		while (m2.find()) {
			String match   = m2.group();
			//String keyWord = match.replaceAll("(^|[^\\$])\\s*", "");
			if (firstBadDollar) {
				firstBadDollar = false;
			} else {
				badDollarKeyWords += ", ";
			}
			badDollarKeyWords += match;
		}
		if (badDollarKeyWords.length() > 0) {
			warnStr += InvalidTestExpression.MISSING_DOLLAR_SIGN_IN_KEYWORD.toString()+badDollarKeyWords+". ";			
		}
		return makeValidateTestExpressionReturn (warnStr, errStr);
	}
	
	private TestResult makeValidateTestExpressionReturn (String warnStr, String errStr) {
		boolean pass = (errStr.length() == 0) ? true : false;
		StringBuffer sb = new StringBuffer(256);
		if (errStr.length() > 0 || warnStr.length() > 0) {
			sb.append("Test expression");
			if (errStr.length() > 0) {
				sb.append(" ERRORS: ");
				sb.append(errStr);
			}
			if (warnStr.length() > 0) {
				sb.append(" WARNINGS: ");
				sb.append(warnStr);
			}
		}
		Throwable testExprErr = pass ? null : new TestSpecificationError();
		return new TestResult (pass, sb.toString(), testExprErr);
	}
	
	/**
	 * Expands all keywords, using the user-supplied optional mappings, if defined, followed
	 * by the substitution of the "dollar" keywords.
	 * @param testExpression
	 * @param context
	 * @return TestResult with {@link TestResult#isPassed()} equals true and the new test 
	 * expression returned by {@link TestResult#getMessage()} or, if an error occurred, 
	 * an error message will be there and {@link TestResult#isPassed()} will return false.
	 * @note We don't call {@link #recordContextChange(String, Object)} when doing the user
	 * substitutions, because they are just string substitutions without corresponding
	 * objects.
	 */
	public TestResult expandKeywords (
			String      testExpression, 
			TestContext context) {
		if (testExpression == null || testExpression.length() == 0) {
			return new TestResult (false, testExpression);
		}
		testExpression = testExpression.trim();
		Map<String, String> exprs = getOptionalKeywordSubstitutions();
		if (exprs != null) {
			for (Map.Entry<String,String> entry: exprs.entrySet()) {
				testExpression = testExpression.replaceAll(entry.getKey(), entry.getValue());
			}
		}
		return expandDollarKeywords(testExpression, context);
	}

	
	/**
	 * After making the user-specified substitutions, substitute the <code>$this</code>,
	 * <code>$target</code>, <code>$args[n]</code>, <code>$return</code>, and 
	 * <code>$old(..)</code> (with ".." equal to <code>$this...</code> or <code>$target...</code>)
	 * with final expressions appropriate for most interpreters. Note that any other <code>$...</code>
	 * words would be errors (e.g., mispellings), but they should have already been caught by
	 * {@link #validateTestExpression(String, TestContext)}, so this method doesn't need to
	 * worry about them!
	 * <br/>On the assumption that some interpreters may not be able to handle <code>$foo</code>
	 * keywords, we make the following substitutions:
	 * <table>
	 * <tr><th>Expression</th><th>Substitution</th></tr>
	 * <tr>
	 *   <td><code>$this</code></td>
	 *   <td><code>c4jThis</code></td>
	 * </tr>
	 * <tr>
	 *   <td><code>$target</code></td>
	 *   <td><code>c4jTarget</code></td>
	 * </tr>
	 * <tr>
	 *   <td><code>$args</code></td>
	 *   <td><code>c4jArgs</code></td>
	 * </tr>
	 * <tr>
	 *   <td><code>$return</code></td>
	 *   <td><code>c4jReturn</code></td>
	 * </tr>
	 * <tr>
	 *   <td><code>$old(..)</code></td>
	 *   <td><code>c4jExprVar</code>N, where "N" is an arbitrary number. A corresponding value
	 *   must exist in the {@link TestContext#getOldValuesMap()}. If there is no corresponding
	 *   value, <code>c4jThis...</code> or </code>c4jTarget...</code> is used, but this will
	 *   be inaccurate, as these values reflect current, not past state!</td>
	 * </tr>
	 * <tr>
	 *   <td><i>itemName</i></td>
	 *   <td>If a string matching {@link TestContext#getItemName()} is found without a preceeding
	 *   <code>$this.</code> or <code>$target.</code>, it is treated as a "bare" field and 
	 *   preceeded by <code>c4jThis.</code>, to avoid potential scoping issues.</td>
	 * </tr>
	 * </table>
	 * @param testExpression
	 * @param context
	 * @return TestResult with {@link TestResult#isPassed()} equals true and the new test 
	 * expression returned by {@link TestResult#getMessage()} or, if an error occurred, 
	 * an error message will be there and {@link TestResult#isPassed()} will return false.
	 * @note For every <code>$old(..)</code> expressions, there should be a corresponding 
	 * mapping in {@link TestContext#getOldValuesMap()}. If not, a warning is issued
	 * and an attempted substitution is made, but any expression evaluations will reflect the
	 * current state, not the "old" state, thereby invalidating the test!
	 */
	protected TestResult expandDollarKeywords(
			String      testExpression, 
			TestContext context) {
		Map<String, Object> ovmap = context.getOldValuesMap();
		int magicSymbolCounter = 1;
		if (ovmap != null) {
			for (Map.Entry<String, Object> entry: ovmap.entrySet()) {
				Object obj = entry.getValue();
				String key = Pattern.quote (entry.getKey());  // escape regex characters!!
				String magicSymbol = Matcher.quoteReplacement("c4jExprVar"+magicSymbolCounter);
				magicSymbolCounter++;
				testExpression = testExpression.replaceAll("\\$old\\s*\\(\\s*"+key+"\\s*\\)", magicSymbol);
				recordContextChange (magicSymbol, obj);
			}
		}
		
		// There *should* be no $old(..) expressions remaining!!
		if (testExpression.contains ("$old")) {
			getReporter().report(Severity.WARN, ExpressionInterpreterHelper.class, 
					"One or more \"$old(..)\" strings remain in test expression \"" +
					testExpression +
					"\" after previous substitutions of known values." +
					" Test results may be inaccurate!");
			testExpression = testExpression.replaceAll ("\\$old\\s*\\(\\s*\\$this([^\\(\\)]*(\\([^\\)]*\\))?[^\\(\\)]*)\\)",   "c4jThis$1");
			testExpression = testExpression.replaceAll ("\\$old\\s*\\(\\s*\\$target([^\\(\\)]*(\\([^\\)]*\\))?[^\\(\\)]*)\\)", "c4jTarget$1");
			// If $old(..) and ".." doesn't start with $target or $this, then assume "$this." prefix.
			testExpression = testExpression.replaceAll ("\\$old\\s*\\(\\s*([^\\(\\)]*(\\([^\\)]*\\))?[^\\(\\)]*)\\)", "c4jThis.$1");
		}
		testExpression = testExpression.replaceAll ("\\$this",   "c4jThis");
		testExpression = testExpression.replaceAll ("\\$target", "c4jTarget");
		testExpression = testExpression.replaceAll ("\\$return", "c4jReturn");
		testExpression = testExpression.replaceAll ("\\$args",   "c4jArgs"  );
		Instance i = context.getInstance();
		Instance f = context.getField();
		recordContextChange ("c4jThis",   i != null ? i.getValue() : null);
		recordContextChange ("c4jTarget", f != null ? f.getValue() : null);
		if (context.getMethodResult() != null) {
			recordContextChange ("c4jReturn", context.getMethodResult().getValue());
		}
		recordContextChange ("c4jArgs",   InstanceUtils.getInstanceValues(context.getMethodArgs()));

		testExpression = substituteArguments (testExpression, context);

		// Look for "bare" items, matching "itemName", but without $this or $target; prepend $this.
		String itemName = context.getItemName();
	    if (itemName != null && itemName.length() != 0) {
	        testExpression = testExpression.replaceAll ("(?<!(c4jThis|c4jTarget)\\.)\\b"+itemName+"\\b", "c4jThis."+itemName);
	    }
		return new TestResult (true, testExpression);
	}

	/**
	 * Replace words that are method parameters with c4jArgs expressions.
	 * We have to handle quoted strings that might contain the same words. A complication
	 * is the possibility of escaped double quotes. We do this by tokenizing the expression
	 * on '"'. Ignoring "\"", every odd-numbered token in the resulting array of tokens
	 * would be the contents of a string. We detect "\"", by looking for '\' characters at
	 * the end of tokens, then we put those strings back together.
	 * @param testExpression
	 * @param context
	 * @return the new test expression
	 * @note This method is public so we can test it!
	 */
	public String substituteArguments (String testExpression, TestContext context) {
		Instance[] args = context.getMethodArgs();
		if (args == null || args.length == 0) {
			return testExpression;
		}
		StringTokenizer st     = new StringTokenizer(testExpression, "\"");
		String[]        tokens = new String[st.countTokens()];  // maximum # we might have.
		int count = 0;
		loop:
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			while (token.charAt(token.length()-1) == '\\') {
				tokens[count] += token;  // concat to previous string
				tokens[count] += '"';    // don't forget to put the '"' back on!
				if (!st.hasMoreTokens()) {
					break loop;
				}
				token = st.nextToken();
			}
			tokens[count] = token;
			count++;
	    }
		StringBuffer newExpr = new StringBuffer();
		for (int it = 0; it < count; it++) {
			String token = tokens[it];
			// The odd-numbered tokens are quoted strings. Don't do
			// the arg substitution, but do resurround with the quotes!
			if (it % 2 == 1) {
				newExpr.append('"');
				newExpr.append(token);
				newExpr.append('"');
			} else {
				for (int ia = 0; ia < args.length; ia++) {
					String argName = args[ia].getItemName();
			        token = token.replaceAll ("(?<!(c4jThis|c4jTarget)\\.)\\b"+argName+"\\b", "c4jArgs["+ia+"]");			
				}
				newExpr.append(token);
			}
		}
		return newExpr.toString();
	}

	private Map<String, Object> rememberedContextChanges = new HashMap<String, Object>();
	
	/**
	 * A hook that is called when {@link #expandKeywords(String, TestContext)} substitutes 
	 * an expression with a new symbol name and a corresponding object exists that holds its
	 * value. for a particular interpreter, capture this information in the implementation 
	 * of this method for later use in {@link #doTest(String, TestContext)}.
	 * @param newSymbolName
	 * @param newObject
	 */
	protected void recordContextChange (
			String newSymbolName, 
			Object newObject) {
		rememberedContextChanges.put(newSymbolName, newObject);
		doRecordContextChange(newSymbolName, newObject);
	}

	/**
	 * A hook that is called when {@link #expandKeywords(String, TestContext)} substitutes 
	 * an expression with a new symbol name and a corresponding object exists that holds its
	 * value. for a particular interpreter, capture this information in the implementation 
	 * of this method for later use in {@link #doTest(String, TestContext)}.
	 * @param newSymbolName
	 * @param newObject
	 */
	abstract protected void doRecordContextChange (
			String newSymbolName, 
			Object newObject);

	/**
	 * A hook that is called when a previous context change should be "forgotten", so it
	 * doesn't potentially cause confusion later on.
	 * @param oldSymbolName
	 */
	void removeContextChange (
			String oldSymbolName) {
		rememberedContextChanges.remove(oldSymbolName);
		doRemoveContextChange(oldSymbolName);
	}

	/**
	 * A hook that is called when a previous context change should be "forgotten", so it
	 * doesn't potentially cause confusion later on.
	 * @param oldSymbolName
	 */
	abstract protected void doRemoveContextChange (
			String oldSymbolName);

	/**
	 * After running a test, remove the context changes so they don't potentially cause problems
	 * later.
	 */
	protected void cleanupContext() {
		for (Map.Entry<String, Object> entry: rememberedContextChanges.entrySet()) {
			doRemoveContextChange(entry.getKey());
		}
		rememberedContextChanges.clear();
	}
	
	/**
	 * Execute the test, returning success (<code>true</code>) or failure
	 * (<code>false</code>).
	 * @param testExpression
	 * @param context
	 * @return TestResult with {@link TestResult#isPassed()} equals true if the test passed.
	 */
	abstract protected TestResult doTest (
			String      testExpression, 
			TestContext context);
	
	private boolean empty (String s) {
		return s == null || s.trim().length() == 0;
	}
	
	public ExpressionInterpreterHelper() {
		super();
	}

	private Reporter reporter;
	protected Reporter getReporter() {
		return reporter;
	}	
}
