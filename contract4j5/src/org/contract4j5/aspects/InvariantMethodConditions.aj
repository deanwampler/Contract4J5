package org.contract4j5.aspects;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.contract.Invar;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.testexpression.DefaultTestExpressionMaker;
import org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker;
import org.contract4j5.instance.Instance;
import org.contract4j5.instance.InstanceUtils;

/** 
 * Invariant tests for methods. 
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
*/
public aspect InvariantMethodConditions extends AbstractConditions {
	private DefaultTestExpressionMaker defaultMethodInvarTestExpressionMaker;
	
	/*
	 * TODO Make the default test that the object state does not change. This suggests 
	 * the default test expression should be <code>$old($this).equals($this)</code>. 
	 * In order to support this, it will be necessary to use reflection to determine 
	 * each non-transient field and to remember its value in the "context". (We can't
	 * assume that "clone()" is supported on the object!!) However, this is imperfect 
	 * for fields that are mutable references, as they might change!
	 * @return the DefaultTestExpressionMaker for method invariant tests
	 */
	public DefaultTestExpressionMaker getDefaultMethodInvarTestExpressionMaker() { 
		if (defaultMethodInvarTestExpressionMaker == null)
			defaultMethodInvarTestExpressionMaker = new SimpleStringDefaultTestExpressionMaker();
		return defaultMethodInvarTestExpressionMaker; 
	}
	
	public void setDefaultMethodInvarTestExpressionMaker (DefaultTestExpressionMaker maker) { 
		defaultMethodInvarTestExpressionMaker = maker; 
	}

	/**
	 * Method invariant before and after PCD.
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut invarMethod (Invar invar, ContractMarker obj) :
		invarCommon() && !within (InvariantMethodConditions) &&
		execution (@Invar !static * ContractMarker+.*(..)) && 
		@annotation (invar) && this (obj);

	Object around (Invar invar, Object obj) : invarMethod (invar, obj) {
		Signature  signature  = thisJoinPointStaticPart.getSignature();
		MethodSignature ms    = (MethodSignature) signature;
		String     methodName = signature.getName();
		Class      clazz      = obj.getClass();
		String[]   argNames   = ms.getParameterNames();
		Class[]    argTypes   = ms.getParameterTypes();
		Object[]   argValues  = thisJoinPoint.getArgs();
		Instance[] args       = InstanceUtils.makeInstanceArray(argNames, argTypes, argValues);
		SourceLocation loc    = thisJoinPointStaticPart.getSourceLocation(); 
		Instance   instance   = new Instance (clazz.getName(), clazz, obj);
		TestContext context   = 
			new TestContextImpl (methodName, instance, null, args, null, null,
				loc.getFileName(), loc.getLine());
		TestResult result  = 
			getParentTestExpressionFinder().findParentMethodTestExpressionIfEmpty(
				invar.value(), invar, ms.getMethod(), context);
		if (result.isPassed() == false) {
			getContractEnforcer().fail(invar.value(), "Invar", result.getMessage(),  
					context, new TestSpecificationError());
		}
		String testExpr = result.getMessage(); 
		testExpr = 
			getDefaultMethodInvarTestExpressionMaker().makeDefaultTestExpressionIfEmpty(testExpr, context);
		context.setOldValuesMap (determineOldValues (testExpr, context));
		getContractEnforcer().invokeTest(testExpr, "Invar", invar.message(), context);
		Object result2 = proceed (invar, obj);
		// Use "ms.getReturnType()", not "result2.getClass()", since result2 might be null!
		context.setMethodResult (new Instance ("", ms.getReturnType(), result2));
		getContractEnforcer().invokeTest(testExpr, "Invar", invar.message(), context);
		return result2;
	}

}

