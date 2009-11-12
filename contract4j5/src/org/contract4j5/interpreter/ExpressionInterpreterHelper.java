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
import java.util.SortedSet;
import java.util.TreeSet;
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
	public Map<String, Object> determineOldValues(TestContext context) {
		Map<String, Object> map = new HashMap<String, Object>();
		// Find "$old()", "$old($this)", "$old($this.foo)", "$old($this.doFoo(bar,baz))", etc.
		// See Javadocs for parent class declaration.
		// Also handle quoted strings: remove them!
		String regex = "\\$old\\s*\\([^\\(\\)]*(\\([^\\)]*\\))?[^\\(\\)]*\\)";
		String expr2 = removeQuotedStrings(context.getActualTestExpression());
		Pattern p = Pattern.compile(regex); 
		Matcher m = p.matcher(expr2);
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
		Object   obj    =  obji   != null ? obji.getValue()   : null;
		Object   field  =  fieldi != null ? fieldi.getValue() : null;
		recordContextChange ("c4jOldThis",   obj, false);
		recordContextChange ("c4jOldTarget", field, false);
		Object result = doDetermineOldValue (newExprStr, context);
		removeContextChange ("c4jOldThis", false);
		removeContextChange ("c4jOldTarget", false);
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
	 * @see org.contract4j5.interpreter.ExpressionInterpreter#invokeTest(org.contract4j5.context.TestContext)
	 */
	public TestResult invokeTest(TestContext context) {
		TestResult testResult = validateTestExpression (context);
		if (testResult.getMessage().length() > 0) { 
			// Log with INFO level, rather than WARN, because they may not be real issues.
			getReporter().report(Severity.INFO, ExpressionInterpreterHelper.class,
					testResult.getMessage());
		}
		if (testResult.isPassed() == false || empty (context.getActualTestExpression())) {
			return testResult;
		} 
		
		testResult = expandKeywords (context);
		if (testResult.isPassed() == false) {
			return testResult;
		}
		saveDynamicContextData(context);
		
		String expr = context.getInternalTestExpression();
		getReporter().report(Severity.DEBUG, ExpressionInterpreterHelper.class,
				"Invoking test (expanded): " + expr);
		testResult = doTest(expr, context);
		cleanupContext();
		return testResult;
	}

	/* (non-Javadoc)
	 * @see org.contract4j5.interpreter.ExpressionInterpreter#validateTestExpression(java.lang.String, org.contract4j5.TestContext)
	 */
	public TestResult validateTestExpression (TestContext context) {
		String errStr = "";
		String warnStr = "";
		if (context.getInternalTestExpression() != null)
			return new TestResult(true, ""); // we've already been here for this test.

		String expression = context.getActualTestExpression();
		if (empty(expression)) {
			return handleEmptyTestExpression();
		} 
		errStr = checkDollarThis(context, expression, errStr);
		errStr = checkDollarTarget(context, expression, errStr);
		errStr = checkFieldName(context, expression, errStr);
		errStr = checkDollarArgs(context, expression, errStr);
		errStr = checkDollarReturn(context, expression, errStr);
		errStr = checkDollarOld(expression, errStr);
		errStr = checkForInvalidWhitespace(expression, errStr);
		errStr = checkForUnrecognizedKeywords(expression, errStr);
		warnStr = checkForMissingDollarSignsInPossibleKeywords(expression,
				warnStr);
		return makeValidateTestExpressionReturn (warnStr, errStr);
	}

	private String checkForMissingDollarSignsInPossibleKeywords(
			String expression, String warnStr) {
		Pattern p2 = Pattern.compile ("(?:(^|[^\\$]))\\s*(this|target|args|return|old)");
		Matcher m2 = p2.matcher (expression);
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
		return warnStr;
	}

	private String checkForUnrecognizedKeywords(String expression, String errStr) {
		if (getAllowUnrecognizedKeywords())
			return errStr;
		Pattern p1 = Pattern.compile ("\\$\\w+");
		Matcher m1 = p1.matcher (expression);
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
		return errStr;
	}

	private String checkForInvalidWhitespace(String expression, String errStr) {
		if (containsKeyword(expression, "\\s+\\w*")) { // '$' followed by whitespace
			errStr += InvalidTestExpression.INVALID_WHITESPACE_IN_KEYWORD.toString();			
		}
		return errStr;
	}

	private String checkDollarOld(String expression, String errStr) {
		if (containsKeyword(expression, "old")) { 
			if (expression.matches("^.*\\$old\\s*[^\\(]+.*$")) {  // "$old ..." w/out "(..)"
				errStr += InvalidTestExpression.OLD_KEYWORD_NO_ARGS.toString();			
			} else if (expression.matches("^.*\\$old\\s*$.*$")) {  // "$old" w/out "(..)"
				errStr += InvalidTestExpression.OLD_KEYWORD_NO_ARGS.toString();			
			} 
			if (expression.matches("^.*\\$old\\s*\\(\\s*\\).*$")) { // $old() (no arg)
				errStr += InvalidTestExpression.OLD_KEYWORD_INVALID_ARGS.toString();			
			} else { 
				Pattern p = Pattern.compile("^.*(\\$old\\s*\\(\\s*[^\\(\\)]*(\\([^\\)]*\\))?[^\\(\\)]*\\)).*$"); 
				Matcher m = p.matcher(expression);
				while (m.find()) {
					String s1 = m.group(1);
					String expr = s1.substring(s1.indexOf('(')+1, s1.length()-1); // grab everything between '(' and ')'
					expr = expr.trim();
					if (expr.matches("^.*[+\\-*/<>=~\\^|&%#@!,:;?{}].*$")) { // $old(..) with nonvalid arg?
						errStr += InvalidTestExpression.OLD_KEYWORD_INVALID_ARGS.toString();
						break;
					}
				}
			}
		}
		return errStr;
	}

	private String checkDollarArgs(TestContext context, String expression,
			String errStr) {
		Object[] args = context.getMethodArgs();
		if (expression.matches("^.*\\$args.*$") && (args == null || args.length == 0)) {
			errStr += InvalidTestExpression.ARGS_KEYWORD_WITH_NO_ARGS.toString();
		}
		return errStr;
	}

	private String checkDollarReturn(TestContext context, String expression,
			String errStr) {
		if (containsKeyword(expression, "return") && context.getMethodResult() == null) {
			errStr += InvalidTestExpression.RETURN_KEYWORD_WITH_NO_RETURN.toString();
		}
		return errStr;
	}

	private String checkFieldName(TestContext context, String expression,
			String errStr) {
		return errStr;
	}

	private String checkDollarTarget(TestContext context, String expression,
			String errStr) {
		if (containsKeyword(expression, "target")) {
			Instance i = context.getField();
			if (i == null) {
				errStr += InvalidTestExpression.TARGET_KEYWORD_WITH_NO_TARGET.toString();
			}
		}
		return errStr;
	}

	private String checkDollarThis(TestContext context, String expression,
			String errStr) {
		if (containsKeyword(expression, "this")) {
			Instance i = context.getInstance();
			if (i == null) {
				errStr += InvalidTestExpression.THIS_KEYWORD_WITH_NO_INSTANCE.toString();
			}
		}
		return errStr;
	}

	private boolean containsKeyword(String expression, String keyword) {
		return expression.matches("^.*\\$"+keyword+"\\b.*$");
	}
	
	protected TestResult handleEmptyTestExpression() {
		String warnStr = "";
		String errStr  = InvalidTestExpression.EMPTY_EXPRESSION_ERROR.toString();
		if (getTreatEmptyTestExpressionAsValidTest()) { 
			// If here, consider empty test "valid"; just warn about it.
			warnStr = InvalidTestExpression.EMPTY_EXPRESSION_WARNING.toString();
			errStr  = "";
		}
		return makeValidateTestExpressionReturn (warnStr, errStr);
	}
	
	// TODO DELETE
	// For purposes of validation, we only need to save the test expression,
	// file name, and line number. They form a sufficiently unique combination
	// and take up less space than saving the full context.
//	private static class TestCacheEntry {
//		public String testExpression;
//		public String fileName;
//		public int    lineNumber;
//		
//		public TestCacheEntry(String testExpression, TestContext context) {
//			this.testExpression = testExpression;
//			this.fileName = context.getFileName();
//			this.lineNumber = context.getLineNumber();
//		}
//
//		@Override
//		public int hashCode() {
//			final int prime = 31;
//			int result = 1;
//			result = prime * result
//					+ ((fileName == null) ? 0 : fileName.hashCode());
//			result = prime * result + lineNumber;
//			result = prime
//					* result
//					+ ((testExpression == null) ? 0 : testExpression.hashCode());
//			return result;
//		}
//
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (getClass() != obj.getClass())
//				return false;
//			final TestCacheEntry other = (TestCacheEntry) obj;
//			if (fileName == null) {
//				if (other.fileName != null)
//					return false;
//			} else if (!fileName.equals(other.fileName))
//				return false;
//			if (lineNumber != other.lineNumber)
//				return false;
//			if (testExpression == null) {
//				if (other.testExpression != null)
//					return false;
//			} else if (!testExpression.equals(other.testExpression))
//				return false;
//			return true;
//		}
//
//	}
//	
//	private boolean cacheTestExpressionValidations = false;
//	public void setCacheTestExpressionValidations(boolean b) { cacheTestExpressionValidations = b; }
//	
//	private TestResult getTestCacheEntry(String testExpression, TestContext context) {
//		TestResult result = getTestCache().get(new TestCacheEntry(testExpression, context));
//		return result;
//	}
//
//	private void putTestCacheEntry(String testExpression, TestContext context, TestResult testResult) {
//		if (cacheTestExpressionValidations == false)
//			return;
//		getTestCache().put(new TestCacheEntry(testExpression, context), testResult);
//	}
//
//	private HashMap<TestCacheEntry, TestResult> testCache;
//
//	private Map<TestCacheEntry, TestResult> getTestCache() {
//		// Keep it from growing too big, with an arbitrary cutoff.
//		// Better would be an LRU cache.
//		if (testCache == null || testCache.size() > 10000)  
//			testCache = new HashMap<TestCacheEntry, TestResult>();
//		return testCache;
//	}

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
	 * by the substitution of the "dollar" keywords. Does <em>not</em> expand keywords within
	 * quoted strings.
	 * @param context
	 * @return TestResult with {@link TestResult#isPassed()} equals true and the new test 
	 * expression returned by {@link TestResult#getMessage()} or, if an error occurred, 
	 * an error message will be there and {@link TestResult#isPassed()} will return false.
	 * @note We don't call {@link #recordContextChange(String, Object, boolean)} when doing the user
	 * substitutions, because they are just string substitutions without corresponding
	 * objects.
	 */
	public TestResult expandKeywords (TestContext context) {
		if (empty(context.getActualTestExpression())) {
			return new TestResult (false, "No test expression!");
		}
		String internalExpression = context.getInternalTestExpression();
		if (internalExpression == null) {
			internalExpression = context.getActualTestExpression();
			internalExpression = substituteOptionalKeywords(internalExpression);
			internalExpression = expandStaticDollarKeywords(internalExpression, context);
			context.setInternalTestExpression(internalExpression);
		}
		return new TestResult(true, internalExpression);
	}

	protected String substituteOptionalKeywords(String internalExpression) {
		Map<String, String> keyWordSubs = getOptionalKeywordSubstitutions();
		for (Map.Entry<String,String> entry: keyWordSubs.entrySet()) 
			internalExpression = substituteInTestExpression(internalExpression, entry.getKey(), entry.getValue());
		return internalExpression;
	}

	/**
	 * Substitute the <code>$this</code>,
	 * <code>$target</code>, <code>$args[n]</code>, <code>$return</code>, and 
	 * <code>$old(..)</code> (with ".." equal to <code>$this...</code> or <code>$target...</code>)
	 * with final expressions appropriate for most interpreters. Note that any other <code>$...</code>
	 * words would be errors (e.g., mispellings), but they should have already been caught by
	 * {@link #validateTestExpression(TestContext)}, so this method doesn't need to
	 * worry about them! Note that no substitutions are made within quoted strings.
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
	 *   <td><code>c4jExprVar</code>N, where "N" is an an ordered number that will be mapped
	 *   keys in the old values map in the context object. (See also the note for 
	 *   saveDynamicContextData().)</td>
	 * </tr>
	 * <tr>
	 *   <td><i>itemName</i></td>
	 *   <td>If a string matching {@link TestContext#getItemName()} is found without a preceeding
	 *   <code>$this.</code> or <code>$target.</code>, it is treated as a "bare" field and 
	 *   preceeded by <code>c4jThis.</code>, to avoid potential scoping issues.</td>
	 * </tr>
	 * </table>
	 * @param context
	 */
	protected String expandStaticDollarKeywords(String internalTestExpression, TestContext context) {
		internalTestExpression = substituteOldValueKeywords(internalTestExpression, context);
		internalTestExpression = substituteUnhandledOldKeywords(internalTestExpression);
		internalTestExpression = substituteKnownKeywords(internalTestExpression, context);
		internalTestExpression = prependBareItemNamesWithObjectQualifier(internalTestExpression, context);
        return internalTestExpression;
	}

	protected String substituteOldValueKeywords(String internalTestExpression,
			TestContext context) {
		Map<String, Object> ovmap = context.getOldValuesMap();
		if (ovmap != null) {
			int magicSymbolCounter = 1;
			// use a sorted set of keys so the generated var names are consistent
			// with those used in saveDynamicContextData().
			SortedSet<String> keys = new TreeSet<String>(ovmap.keySet());
			for (String key: keys) {
				// Escape regex characters in key:
				String key2 = Pattern.quote (key);
				String magicSymbol = Matcher.quoteReplacement("c4jExprVar"+magicSymbolCounter);
				magicSymbolCounter++;
				internalTestExpression = 
					substituteInTestExpression(internalTestExpression, "\\$old\\s*\\(\\s*"+key2+"\\s*\\)", magicSymbol);
			}
		}
		return internalTestExpression;
	}

	/**
	 *  There *should* be no $old(..) expressions remaining!!
	 */
	protected String substituteUnhandledOldKeywords(
			String internalTestExpression) {
		if (containsKeyword(internalTestExpression, "old")) {
			getReporter().report(Severity.WARN, ExpressionInterpreterHelper.class, 
					"One or more \"$old(..)\" strings remain in test expression \"" +
					internalTestExpression +
					"\" after previous substitutions of known values." +
					" Test results may be inaccurate!");
			internalTestExpression = substituteInTestExpression(internalTestExpression, "\\$old\\s*\\(\\s*\\$this([^\\(\\)]*(\\([^\\)]*\\))?[^\\(\\)]*)\\)",   "c4jThis$1");
			internalTestExpression = substituteInTestExpression(internalTestExpression, "\\$old\\s*\\(\\s*\\$target([^\\(\\)]*(\\([^\\)]*\\))?[^\\(\\)]*)\\)", "c4jTarget$1");
			// If $old(..) and ".." doesn't start with $target or $this, then assume "$this." prefix.
			internalTestExpression = substituteInTestExpression(internalTestExpression, "\\$old\\s*\\(\\s*([^\\(\\)]*(\\([^\\)]*\\))?[^\\(\\)]*)\\)", "c4jThis.$1");
		}
		return internalTestExpression;
	}

	protected String substituteKnownKeywords(String internalTestExpression,
			TestContext context) {
		internalTestExpression = substituteInTestExpression(internalTestExpression, "\\$this",   "c4jThis");
		internalTestExpression = substituteInTestExpression(internalTestExpression, "\\$target", "c4jTarget");
		internalTestExpression = substituteInTestExpression(internalTestExpression, "\\$return", "c4jReturn");
		internalTestExpression = substituteInTestExpression(internalTestExpression, "\\$args",   "c4jArgs"  );
		internalTestExpression = substituteMethodArguments (internalTestExpression, context);
		return internalTestExpression;
	}

	/**
	 *  Look for "bare" items, matching "itemName", but without $this or $target; prepend $this.
	 */
	protected String prependBareItemNamesWithObjectQualifier(
			String internalTestExpression, TestContext context) {
		String itemName = context.getItemName();
	    if (itemName != null && itemName.length() != 0) {
	        internalTestExpression = substituteInTestExpression(internalTestExpression, "(?<!(c4jThis|c4jTarget)\\.)\\b"+itemName+"\\b", "c4jThis."+itemName);
	    }
		return internalTestExpression;
	}

	/**
	 * Save the dynamic (runtime) data referenced in the final "internal" test expression, 
	 * including the "$old(...)" values.
	 * @note For every <code>$old(..)</code> expressions, there should be a corresponding 
	 * mapping in {@link TestContext#getOldValuesMap()}. If not, a warning is issued
	 * and an attempted substitution is made, but any expression evaluations will reflect the
	 * current state, not the "old" state, thereby invalidating the test!
	 */
	protected String saveDynamicContextData(TestContext context) {
		Map<String, Object> ovmap = context.getOldValuesMap();
		if (ovmap != null) {
			int magicSymbolCounter = 1;
			// use a sorted set of keys so the generated var names are consistent
			// with those used in expandStaticDollarKeywords().
			SortedSet<String> keys = new TreeSet<String>(ovmap.keySet());
			for (String key: keys) {
				Object obj = ovmap.get(key);
				String contextKey = "c4jExprVar" + (magicSymbolCounter++);
				recordContextChange (contextKey, obj, false);
			}
		}

		String expression = context.getInternalTestExpression();
		if (expression.contains("c4jThis")) { 
			Instance i = context.getInstance();
			recordContextChange ("c4jThis",   i != null ? i.getValue() : null, false);
		}
		if (expression.contains("c4jTarget")) {
			Instance f = context.getField();
			recordContextChange ("c4jTarget", f != null ? f.getValue() : null, false);
		}
		Instance methodResult = context.getMethodResult();
		if (methodResult != null) {
			recordContextChange ("c4jReturn", methodResult.getValue(), false);
		}
		Instance[] methodArgs = context.getMethodArgs();
		if (methodArgs != null && methodArgs.length > 0) {
			recordContextChange ("c4jArgs", InstanceUtils.getInstanceValues(methodArgs), false);
		}
	    findReferencedObjectsAndLoad(expression, context);

		return expression;
	}
	
	public void findReferencedObjectsAndLoad(String expression, TestContext context) {
	    Pattern pattern = Pattern.compile("([\\w\\.]+)");
	    String message = "";
	    Matcher matcher = pattern.matcher(expression);
	    while (matcher.find()) {
	    	String bareItem = matcher.group(1);
	    	if (bareItem.startsWith("c4j") == false && resolveItem(bareItem, context) == false)
	    		message += bareItem + ", ";
        }
	    if (message.length() > 0) {
	    	getReporter().report(Severity.INFO, context.getClass(), "Expression may contain references to classes or objects ("+message+") that can't be resolved (expression = \""+expression+"\").");
	    }
	}
	
	protected boolean resolveItem(String bareItemName, TestContext context) {
		String name = getPrefix(bareItemName, ".");
		String previousName = bareItemName;
		while (name.length() > 0 && !name.equals(previousName)) {
			if (findAndLoad(name, context))
				return true;
			previousName = name;
			name = getPrefix(name, ".");
		}
		return false;
	}
	
	protected String getPrefix(String name, String separator) {
    	int index  = name.lastIndexOf(separator);
    	return (index <= 0) ? name : name.substring(0, index);
	}
	
	protected boolean findAndLoad(String name, TestContext context) {
		if (getObjectInContext(name) != null)
			return true;
		Instance instance = context.getInstance();
		Class<?>  clazz  = null;
		if (instance != null) {
			clazz = instance.getClazz();
		}
		if (clazz != null && (clazz.getName().equals(name) || clazz.getSimpleName().equals(name))) {
			registerContextObject(name, clazz);
			return true;
		}
		if (loadClassIfPossible(name, name) == true)
			return true;
		if (clazz != null && Character.isUpperCase(name.charAt(0)) && loadClassIfPossible(name, clazz.getPackage().getName()+"."+name))
			return true;
		return false;
	}
	
	protected boolean loadClassIfPossible(String symbol, String className) {
		try {
			Class<?> clazz = Class.forName(className);
			registerContextObject(symbol, clazz);
			return true;
		} catch (Throwable th) {
//			System.err.println("not found: "+className);
		}
		return false;
	}

	/**
	 * Replace words that are method parameters with c4jArgs expressions, 
	 * without making the substitutions in quoted strings.
	 * @param testExpression
	 * @param context
	 * @return the new test expression
	 */
	public String substituteMethodArguments (String testExpression, TestContext context) {
		Instance[] args = context.getMethodArgs();
		if (args == null || args.length == 0) {
			return testExpression;
		}
		String expression = testExpression;
		for (int ia = 0; ia < args.length; ia++) {
			String argName = args[ia].getItemName();
			expression = substituteInTestExpression(expression, 
					"(?<!(c4jThis|c4jTarget)\\.)\\b"+argName+"\\b", "c4jArgs["+ia+"]");			
		}
		return expression;
	}

	public String substituteInTestExpression(String expression, String key,	String value) {
		StringBuffer buff = new StringBuffer();
		int count = 0;
		for (String subExpression: expression.split("(?<!\\\\)\"")) {
			// Only the even ones, starting at zero are NOT quoted strings
			if (count++ % 2 == 0)  
				subExpression = subExpression.replaceAll(key, value);
			else
				subExpression = "\"" + subExpression + "\"";
			buff.append(subExpression);
		}
		return buff.toString();
	}

	public String removeQuotedStrings(String expression) {
		StringBuffer buff = new StringBuffer();
		int count = 0;
		for (String subExpression: expression.split("(?<!\\\\)\"")) {
			// Remove only the odd ones, starting at zero, which are the quoted strings
			if (count++ % 2 == 0)  
				buff.append(subExpression);
		}
		return buff.toString();
	}


	public Object getObjectInContext(String name) {
		return doGetObjectInContext(name);
	}

	abstract protected Object doGetObjectInContext(String name);

	/**
	 * Register an object with the scripting language interpreter' context for one test run.
	 */
	public void registerContextObject(
			String newSymbolName, 
			Object newObject) {
		recordContextChange(newSymbolName, newObject, false);
	}
	
	/**
	 * Register an object globally, not just for one test.
	 * @param newSymbolName
	 * @param newObject
	 */
	public void registerGlobalContextObject(
			String newSymbolName, 
			Object newObject) {
		recordContextChange(newSymbolName, newObject, true);
	}
	
	/**
	 * Remove an object from the scripting language interpreter's context for one test run.
	 */
	public void unregisterContextObject(String existingSymbolName) {
		removeContextChange(existingSymbolName, true);
	}
	
	/**
	 * Remove an object from the scripting language interpreter's context permanently.
	 */
	public void unregisterGlobalContextObject(String existingSymbolName) {
		removeContextChange(existingSymbolName, true);
	}
	
	
	private Map<String, Object> rememberedContextChanges = new HashMap<String, Object>();
	
	/**
	 * A hook that is called when an expression references an object by name and the object
	 * needs to be made available to the interpreter. For a particular interpreter, capture 
	 * this information in the implementation of this method for later use in {@link #doTest(String, TestContext)}.
	 * @param newSymbolName
	 * @param newObject
	 * @param useGlobally is true if this object should be retained for all subsequent tests and not
	 *   discarded at the end of the current test.
	 */
	protected void recordContextChange (
			String newSymbolName, 
			Object newObject, 
			boolean useGlobally) {
		if (useGlobally == false)
			rememberedContextChanges.put(newSymbolName, newObject);
		doRecordContextChange(newSymbolName, newObject);
	}

	/**
	 * A hook that is called when {@link #expandKeywords(TestContext)} substitutes 
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
	 * @param usedGlobally TODO
	 */
	void removeContextChange (
			String oldSymbolName, 
			boolean usedGlobally) {
		if (usedGlobally == false)
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
	
	private Reporter reporter;
	protected Reporter getReporter() {
		return reporter;
	}

	protected String scriptingEngineName;
	public String getScriptingEngineName() { return scriptingEngineName; }	

	protected boolean allowUnrecognizedKeywords = false;
	
	/**
	 * Languages like JRuby allow '$' in variable names (in JRuby, they mark global variables). 
	 * This flag disables the error checking for unrecognized "$word" in expressions.
	 * @return
	 */
	public boolean getAllowUnrecognizedKeywords() { return allowUnrecognizedKeywords; }
	public void    setAllowUnrecognizedKeywords(boolean b) { allowUnrecognizedKeywords = b; }
	
	protected TestResult makeExceptionThrownTestResult(String expression, TestContext context,
			Throwable throwable) {
		String msg = "Evaluation of expression \""+expression+"\" failed: " + makeThrowableMsg(throwable);
		if (isLikelyTestSpecificationError(throwable)) 
			return new TestResult (false, msg, new TestSpecificationError(msg, throwable));
		return new TestResult(false, msg);
	}

	abstract protected boolean isLikelyTestSpecificationError(Throwable throwable);

	protected String makeThrowableMsg(Throwable throwable) {
		Throwable cause  = throwable.getCause();
		if (cause != null)
			return throwable.toString() + " (cause: " + cause.toString() + ")";
		return throwable.toString();
	}

	protected String didNotReturnBooleanErrorMessage(String testExpression, Object value) {
		return "Expression \""+testExpression+"\" did not return a boolean. \""+value.toString()+"\" returned instead.";
	}

	public ExpressionInterpreterHelper(String scriptingEngineName) {
		this(scriptingEngineName, false, new HashMap<String, String>());
	}
	
	public ExpressionInterpreterHelper(
			String scriptingEngineName, 
			boolean treatEmptyTestExpressionAsValid, Map<String, String> optionalKeywordSubstitutions) {
		super();
		this.scriptingEngineName = scriptingEngineName;
		this.treatEmptyTestExpressionAsValidTest = treatEmptyTestExpressionAsValid;
		this.optionalKeywordSubstitutions = optionalKeywordSubstitutions;
	}
	
}
