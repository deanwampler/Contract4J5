package org.contract4j5.aspects;

import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.contract4j5.Instance;
import org.contract4j5.Invar;
import org.contract4j5.TestContext;
import org.contract4j5.TestContextImpl;
import org.contract4j5.TestSpecificationError;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.testexpression.DefaultTestExpressionMaker;
import org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker;
import org.contract4j5.util.InstanceUtils;

/** 
 * Invariant tests for constructors.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public aspect InvariantCtorConditions extends AbstractConditions {
	private DefaultTestExpressionMaker defaultCtorInvarTestExpressionMaker; 

	public DefaultTestExpressionMaker getDefaultCtorInvarTestExpressionMaker() { 
		if (defaultCtorInvarTestExpressionMaker == null)
			defaultCtorInvarTestExpressionMaker = new SimpleStringDefaultTestExpressionMaker();
		return defaultCtorInvarTestExpressionMaker; 
	}
	
	public void setDefaultCtorInvarTestExpressionMaker (DefaultTestExpressionMaker maker) { 
		defaultCtorInvarTestExpressionMaker = maker; 
	}

	/**
	 * Constructor invariant PCD.
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut invarCtor (Invar invar, ContractMarker obj) : 
		invarCommon() && !within (InvariantCtorConditions) &&
		execution (@Invar ContractMarker+.new(..)) && 
		@annotation (invar) && this (obj);

	after (Invar invar, ContractMarker obj) returning : invarCtor (invar, obj) {
		ConstructorSignature cs = (ConstructorSignature) thisJoinPointStaticPart.getSignature();
		Class       clazz     = obj.getClass();
		String[]    argNames  = cs.getParameterNames();
		Class[]     argTypes  = cs.getParameterTypes();
		Object[]    argValues = thisJoinPoint.getArgs();
		Instance[]  args      = InstanceUtils.makeInstanceArray(argNames, argTypes, argValues);
		SourceLocation loc    = thisJoinPointStaticPart.getSourceLocation(); 
		Instance    instance  = new Instance (clazz.getName(), clazz, obj);
		TestContext context = 
			new TestContextImpl (clazz.getSimpleName(), instance, null, args, null, null,
					loc.getFileName(), loc.getLine());
		TestResult result = 
			getParentTestExpressionFinder().findParentConstructorTestExpressionIfEmpty(
				invar.value(), invar, cs.getConstructor(), context);
		if (result.isPassed() == false) {
			getContractEnforcer().fail(invar.value(), "Invar", result.getMessage(),  
					context, new TestSpecificationError());
		}
		String testExpr = result.getMessage(); 
		testExpr = 
			getDefaultCtorInvarTestExpressionMaker().makeDefaultTestExpressionIfEmpty(testExpr, context);
		// Capture "old" data. There aren't any, but in case the expression uses "$old(..)" expressions
		// we want to capture the "new" values as if old...
		context.setOldValuesMap (determineOldValues (testExpr, context));
		getContractEnforcer().invokeTest(testExpr, "Invar", invar.message(), context);
	}
}


