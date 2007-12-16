package org.contract4j5.interpreter.jruby;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.contract4j5.context.TestContext;
import org.contract4j5.interpreter.ExpressionInterpreterHelper;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.utils.StringUtils;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.GlobalVariable;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * For the V0.8.0 release of Contract4J, I attempted to upgrade to JRuby 1.0, but ran into
 * problems that appear to be related to different versions of BSF, where JRuby uses v2.3 and
 * v2.4 is required for the other engines and Contract4J itself. To remedy this situation, I
 * started to bypass BSF altogether for JRuby. However, since I decided to deprecate JRuby, too,
 * the V0.8 release still uses BSF. This class is an incomplete implementation...
 * @deprecated
 */
public class JRubyExpressionInterpreter extends ExpressionInterpreterHelper {
    final Ruby runtime = Ruby.getDefaultInstance();
	private boolean requiredJava = false;
    
	public JRubyExpressionInterpreter() {
		this(false, new HashMap<String, String>());
	}
	
	public JRubyExpressionInterpreter(
			boolean treatEmptyTestExpressionAsValid) {
		this(treatEmptyTestExpressionAsValid, new HashMap<String, String>());
	}
	
	public JRubyExpressionInterpreter(
			boolean treatEmptyTestExpressionAsValid, 
			Map<String, String> optionalKeywordSubstitutions) {
		super("ruby", treatEmptyTestExpressionAsValid, optionalKeywordSubstitutions);
		setAllowUnrecognizedKeywords(true);
	}

	@Override
	protected Object doDetermineOldValue(String exprStr, TestContext context) {
		IRubyObject iro = runtime.evalScript(mungeTestExpression(exprStr));
		return JavaEmbedUtils.rubyToJava(runtime, iro, iro.getClass());
	}

	@Override
	protected Object doGetObjectInContext(String name) {
		IRubyObject iro = runtime.evalScript(name);
		return JavaEmbedUtils.rubyToJava(runtime, iro, iro.getClass());
//		return iro.dataGetStruct();
	}

	@Override
	protected void doRecordContextChange(String newSymbolName, Object newObject) {
        if (newObject instanceof Class) {
        	if (requiredJava == false) {
        		requiredJava = true;
        		IRubyObject result = runtime.evalScript("require \"java\"" + newline());
        		System.err.println("requiring java, result: "+result);
        	}
    		Class<?> clazz = (Class<?>) newObject;
			IRubyObject result = runtime.evalScript("include_class \""+clazz.getName()+"\""+newline());
			System.err.println("including class:" +clazz.getName()+", result: "+result);
        } else {
        	IRubyObject rubyObject = JavaUtil.convertJavaToRuby(runtime, newObject);
        	runtime.defineVariable(new GlobalVariable(runtime, newSymbolName, rubyObject));
        }
	}
	
	protected String newline() { return StringUtils.newline(); }

	@Override
	protected void doRemoveContextChange(String oldSymbolName) {
		// TODO
	}

	@Override
	protected TestResult doTest(String testExpression, TestContext context) {
		try {
			IRubyObject iro = runtime.evalScript(mungeTestExpression(testExpression));
			Boolean b = (Boolean) JavaEmbedUtils.rubyToJava(runtime, iro, Boolean.class);
			return new TestResult(b.booleanValue());
		} catch (ClassCastException e) {
			return new TestResult(false, "Translated test expression \""+mungeTestExpression(testExpression)+"\" did not return a boolean!");
		} catch (Throwable th) {
			return new TestResult(false, "Exception "+th.toString()+" thrown while executing test expression \""+mungeTestExpression(testExpression)+"\""+newline()+Arrays.toString(th.getStackTrace()));
		}
	}

	protected String mungeTestExpression (String testExpression) {
		String expr1 = testExpression.replaceAll("c4j", "\\$c4j");
		String expr2 = expr1.replaceAll("null", "nil");
		String expr3 = expr2.replaceAll("equals\\s*\\(", "eql\\?\\(");
		String expr  = expr3.replaceAll("compareTo\\s*\\(", "\\<=\\>\\(");
		return expr;
	}

	@Override
	protected boolean isLikelyTestSpecificationError(
			Throwable throwable) {
		// TODO what JRuby exceptions should we observe here?
		return false;
	}
}
