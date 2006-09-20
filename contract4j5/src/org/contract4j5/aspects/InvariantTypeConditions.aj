package org.contract4j5.aspects;

import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.contract4j5.Contract;
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
 * Tests for type invariants. We have to handle two cases, one where the
 * Invar annotation is explicitly declared on the type and one where it
 * inherits the declaration. We conveniently handle both with ITD of a
 * marker interface.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public aspect InvariantTypeConditions extends AbstractConditions {
	private DefaultTestExpressionMaker defaultTypeInvarTestExpressionMaker; 
	
	public DefaultTestExpressionMaker getDefaultTypeInvarTestExpressionMaker() { 
		if (defaultTypeInvarTestExpressionMaker == null)
			defaultTypeInvarTestExpressionMaker = new SimpleStringDefaultTestExpressionMaker();
		return defaultTypeInvarTestExpressionMaker; 
	}
	
	public void setDefaultTypeInvarTestExpressionMaker (DefaultTestExpressionMaker maker) { 
		defaultTypeInvarTestExpressionMaker = maker; 
	}

	/**
	 * Marker interface that is used with intertype declaration to support the
	 * desired inheritance of type invariants.
	 */
	public static interface InvarMarker {}
	
	declare parents: (@Contract @Invar *) implements InvarMarker;
		
	/**
	 * PCD for type (class, aspect, ...) invariant tests that should be evaluated
	 * as both before and after advice. (We actually use around advice).
	 * In addition to excluding static methods, we don't advise 
	 * calls to or within constructors here, nor calls to get/set methods, 
	 * all of which may do initializations of data.
	 * These cases are handled separately.
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut invarTypeMethod(Invar invar, InvarMarker obj) :
		invarCommon() && !within (InvariantTypeConditions) &&
		execution (!static * InvarMarker+.*(..)) &&
		! (execution (* InvarMarker+.get*(..)) || execution (* InvarMarker+.set*(..))) &&
		! cflow (execution (InvarMarker+.new(..))) &&
		@this (invar) && this (obj);

	/**
	 * PCD for type (class, aspect, ...) invariant tests that are only evaluated
	 * as after advice, i.e., get/set methods. Constructor cflows are again excluded 
	 * and are handled separately below.
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut invarTypeGetSet (Invar invar, InvarMarker obj) :
		invarCommon() && !within (InvariantTypeConditions) &&
		(execution (* InvarMarker+.get*(..)) || execution (* InvarMarker+.set*(..))) &&
		! cflowbelow (execution (InvarMarker+.new(..))) &&
		@this (invar) && this (obj); 

	/**
	 * PCD for type (class, aspect, ...) invariant tests for after advice after
	 * c'tor execution.
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut invarTypeCtor (Invar invar, InvarMarker obj) : 
		invarCommon() && !within (InvariantTypeConditions) &&
		execution (InvarMarker+.new(..)) &&
		@this (invar) && this (obj);

	
	Object around (Invar invar, InvarMarker obj) : 
			invarTypeMethod (invar, obj) || invarTypeGetSet (invar, obj) {
		MethodSignature ms   = (MethodSignature) thisJoinPointStaticPart.getSignature();
		Class      clazz     = obj.getClass();
		String[]   argNames  = ms.getParameterNames();
		Class[]    argTypes  = ms.getParameterTypes();
		Object[]   argValues = thisJoinPoint.getArgs();
		Instance[] args      = InstanceUtils.makeInstanceArray(argNames, argTypes, argValues);
		SourceLocation loc   = thisJoinPointStaticPart.getSourceLocation(); 
		Instance   instance  = new Instance (clazz.getName(), clazz, obj);
		TestContext context  = 
			new TestContextImpl (clazz.getName(), instance, null, args, null, null,
					loc.getFileName(), loc.getLine());
		TestResult result  = handleParentExpression(invar, clazz, context);
		String testExpr = result.getMessage(); 
		testExpr  = 
			getDefaultTypeInvarTestExpressionMaker().makeDefaultTestExpressionIfEmpty(testExpr, context);
		context.setOldValuesMap (determineOldValues (testExpr, context));
		getContractEnforcer().invokeTest(testExpr, "Invar", invar.message(), context);
		Object result2 = proceed(invar, obj);
		context.setMethodResult (new Instance ("", ms.getReturnType(), result2));
		getContractEnforcer().invokeTest(testExpr, "Invar", invar.message(), context);
		return result2;
	}
	
	after(Invar invar, InvarMarker obj) returning () : invarTypeCtor (invar, obj) {
		ConstructorSignature cs = 
			(ConstructorSignature) thisJoinPointStaticPart.getSignature();
		Class      clazz     = obj.getClass();
		String[]   argNames  = cs.getParameterNames();
		Class[]    argTypes  = cs.getParameterTypes();
		Object[]   argValues = thisJoinPoint.getArgs();
		Instance[] args      = InstanceUtils.makeInstanceArray(argNames, argTypes, argValues);
		SourceLocation loc   = thisJoinPointStaticPart.getSourceLocation(); 
		Instance   instance  = new Instance (clazz.getName(), clazz, obj);
		TestContext context  = 
			new TestContextImpl (clazz.getSimpleName(), instance, null, args, null, null,
				loc.getFileName(), loc.getLine());
		TestResult result  = handleParentExpression(invar, clazz, context);
		String testExpr = result.getMessage(); 
		testExpr  = 
			getDefaultTypeInvarTestExpressionMaker().makeDefaultTestExpressionIfEmpty(testExpr, context);
		// Capture "old" data (even though there are no old data...).
		context.setOldValuesMap (determineOldValues (testExpr, context));
		getContractEnforcer().invokeTest(testExpr, "Invar", invar.message(), context);
	}

	private TestResult handleParentExpression(Invar invar, Class clazz, TestContext context) {
		TestResult result = 
			getParentTestExpressionFinder().findParentTypeInvarTestExpressionIfEmpty(
			invar.value(), clazz, context);
		if (result.isPassed() == false) {
			getContractEnforcer().fail(invar.value(), "Invar", result.getMessage(),  
					context, new TestSpecificationError());
		}
		return result;
	}
}
