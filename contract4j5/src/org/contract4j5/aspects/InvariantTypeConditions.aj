/*
 * Copyright 2005, 2006 Dean Wampler. All rights reserved.
 * http://www.contract4j.org
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
package org.contract4j5.aspects;

import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.testexpression.DefaultTestExpressionMaker;
import org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker;
import org.contract4j5.instance.Instance;
import org.contract4j5.instance.InstanceUtils;

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
	 * Preferred PCD; match on the marker interface or subclasses. This does not work with
	 * generics (AJ bug??).
	 */
	pointcut invarTypeMethodUsingMarker() :
		execution (!static * InvarMarker+.*(..)) &&
		! (execution (* InvarMarker+.get*(..)) || execution (* InvarMarker+.set*(..))) &&
		! cflow (execution (InvarMarker+.new(..)));
	
	/**
	 * Support generics, by matching on the annotation. Requires derived classes to apply
	 * the annotation explicitly!!
	 */
	pointcut invarTypeMethodUsingInvarAnno() :
		execution (!static * (@Invar *).*(..)) &&
		! (execution (* (@Invar *).get*(..)) || execution (* (@Invar *).set*(..))) &&
		! cflow (execution ((@Invar *).new(..)));

	/**
	 * PCD for type (class, aspect, ...) invariant tests that should be evaluated
	 * as both before and after advice. (We actually use around advice).
	 * In addition to excluding static methods, we don't advise 
	 * calls to or within constructors here, nor calls to get/set methods, 
	 * all of which may do initializations of data.
	 * These cases are handled separately.
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut invarTypeMethod(Invar invar, Object obj) :
		invarCommon() && !within (InvariantTypeConditions) &&
		(invarTypeMethodUsingMarker() || invarTypeMethodUsingInvarAnno()) &&
		@this (invar) && this (obj);


	pointcut invarTypeGetSetUsingMarker () :
		(execution (* InvarMarker+.get*(..)) || execution (* InvarMarker+.set*(..))) &&
		! cflowbelow (execution (InvarMarker+.new(..)));

	pointcut invarTypeGetSetUsingInvarAnno () :
		(execution (* (@Invar *).get*(..)) || execution (* (@Invar *).set*(..))) &&
		! cflowbelow (execution ((@Invar *).new(..)));

	/**
	 * PCD for type (class, aspect, ...) invariant tests that are only evaluated
	 * as after advice, i.e., get/set methods. Constructor cflows are again excluded 
	 * and are handled separately below.
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut invarTypeGetSet (Invar invar, Object obj) :
		invarCommon() && !within (InvariantTypeConditions) &&
		(invarTypeGetSetUsingMarker() || invarTypeGetSetUsingInvarAnno()) &&
		@this (invar) && this (obj); 

	
	pointcut invarTypeCtorUsingMarker () : 
		execution (InvarMarker+.new(..));

	pointcut invarTypeCtorUsingInvarAnno () : 
		execution ((@Invar *).new(..));

	/**
	 * PCD for type (class, aspect, ...) invariant tests for after advice after
	 * c'tor execution.
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut invarTypeCtor (Invar invar, Object obj) : 
		invarCommon() && !within (InvariantTypeConditions) &&
		(invarTypeCtorUsingMarker() || invarTypeCtorUsingInvarAnno()) &&
		@this (invar) && this (obj);

	
	Object around (Invar invar, Object obj) : 
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
			new TestContextImpl (clazz.getName(), clazz.getSimpleName(), instance, null, args, null, null,
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
	
	after(Invar invar, Object obj) returning () : invarTypeCtor (invar, obj) {
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
			new TestContextImpl (clazz.getSimpleName(), clazz.getSimpleName(), instance, null, args, null, null,
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
