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
import org.contract4j5.context.TestContextCache;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.contract.Disabled;
import org.contract4j5.contract.Invar;
import org.contract4j5.controller.SystemCaches;
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
	 * Support generics by matching on the annotation. Requires derived classes to apply
	 * the annotation explicitly!!
	 */
	pointcut invarTypeMethodUsingInvarAnno() :
		execution (!static * (@Invar *+).*(..)) && ! execution (!static * (@Disabled *+).*(..)) && 
		! (execution (* (@Invar *+).get*(..)) || execution (* (@Invar *+).set*(..))) &&
		! cflow (execution ((@Invar *+).new(..)));

	/**
	 * PCD for type (class, aspect, ...) invariant tests that should be evaluated
	 * as both before and after advice. (We actually use around advice).
	 * In addition to excluding static methods, we don't advise 
	 * calls to or within constructors here, nor calls to get/set methods, 
	 * all of which may do initializations of data.
	 * These cases are handled separately.
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut invarTypeMethod(Object obj, Invar invar) :
		!within (InvariantTypeConditions) &&
		invarTypeMethodUsingInvarAnno() &&
		@this (invar) && 
		this (obj);

	
	pointcut invarTypeGetSetUsingInvarAnno () :
		(execution (* (@Invar *+).get*(..)) || execution (* (@Invar *+).set*(..))) &&
		! ((execution (* (@Disabled *+).get*(..)) || execution (* (@Disabled *+).set*(..)))) &&
		! cflowbelow (execution ((@Invar *+).new(..)));

	/**
	 * PCD for type (class, aspect, ...) invariant tests that are only evaluated
	 * as after advice, i.e., get/set methods. Constructor cflows are again excluded 
	 * and are handled separately below.
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut invarTypeGetSet (Object obj, Invar invar) :
		!within (InvariantTypeConditions) &&
		invarTypeGetSetUsingInvarAnno() &&
		@this (invar) && 
		this (obj); 

	
	pointcut invarTypeCtorUsingInvarAnno () : 
		execution ((@Invar *+).new(..)) && ! execution ((@Disabled *+).new(..));

	/**
	 * PCD for type (class, aspect, ...) invariant tests for after advice after
	 * c'tor execution.
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut invarTypeCtor (Object obj, Invar invar) : 
		!within (InvariantTypeConditions) &&
		invarTypeCtorUsingInvarAnno() &&
		@this (invar) &&
		this (obj);

	
	Object around (Invar invar, Object obj) : 
		invarCommon() && (invarTypeMethod (obj, invar) || invarTypeGetSet (obj, invar)) {
		MethodSignature ms   = (MethodSignature) thisJoinPointStaticPart.getSignature();
		TestContext context   = null;
		SourceLocation loc    = thisJoinPointStaticPart.getSourceLocation();
		String fileName = loc.getFileName();
		int    lineNum  = loc.getLine();
		TestContextCache.Key key = new TestContextCache.Key("Invar", fileName, lineNum);
		TestContextCache.Entry entry = SystemCaches.testContextCache.get(key);
		if (entry != null) {
			context = entry.testContext;
			Object[]   argValues  = thisJoinPoint.getArgs();
			Instance[] args       = InstanceUtils.makeInstanceArray(entry.argNames, entry.argTypes, argValues);
			context.setMethodArgs(args);
		} else {
			Class<?>   clazz     = obj.getClass();
			String[]   argNames  = ms.getParameterNames();
			Class<?>[] argTypes  = ms.getParameterTypes();
			Object[]   argValues = thisJoinPoint.getArgs();
			Instance[] args      = InstanceUtils.makeInstanceArray(argNames, argTypes, argValues);
			Instance   instance  = new Instance (clazz.getName(), clazz, obj);
			context  = new TestContextImpl (clazz.getName(), clazz.getSimpleName(), instance, 
								null, args, null, null, fileName, lineNum);
			TestResult result  = handleParentExpression(invar.value(), clazz, context);
			String testExpr  = 
				getDefaultTypeInvarTestExpressionMaker().makeDefaultTestExpressionIfEmpty(result.getMessage(), context);
			context.setActualTestExpression(testExpr);
			SystemCaches.testContextCache.put(key, new TestContextCache.Entry(context, argNames, argTypes, null, null));
		}
		context.setOldValuesMap (determineOldValues (context));
		getContractEnforcer().invokeTest("Invar", invar.message(), context);
		Object result2 = proceed(invar, obj);
		context.setMethodResult (new Instance ("", ms.getReturnType(), result2));
		getContractEnforcer().invokeTest("Invar", invar.message(), context);
		return result2;
	}
	
	after(Invar invar, Object obj) returning : 
		invarCommon() && invarTypeCtor (obj, invar) {
		ConstructorSignature cs = 
			(ConstructorSignature) thisJoinPointStaticPart.getSignature();
		TestContext context   = null;
		SourceLocation loc    = thisJoinPointStaticPart.getSourceLocation();
		String fileName = loc.getFileName();
		int    lineNum  = loc.getLine();
		TestContextCache.Key key = new TestContextCache.Key("Invar", fileName, lineNum);
		TestContextCache.Entry entry = SystemCaches.testContextCache.get(key);
		if (entry != null) {
			context = entry.testContext;
			Object[]   argValues  = thisJoinPoint.getArgs();
			Instance[] args       = InstanceUtils.makeInstanceArray(entry.argNames, entry.argTypes, argValues);
			context.setMethodArgs(args);
		} else {
			Class<?>   clazz     = obj.getClass();
			String[]   argNames  = cs.getParameterNames();
			Class<?>[] argTypes  = cs.getParameterTypes();
			Object[]   argValues = thisJoinPoint.getArgs();
			Instance[] args      = InstanceUtils.makeInstanceArray(argNames, argTypes, argValues);
			Instance   instance  = new Instance (clazz.getName(), clazz, obj);
			context = new TestContextImpl (clazz.getSimpleName(), clazz.getSimpleName(), instance, 
								null, args, null, null, fileName, lineNum);
			TestResult result  = handleParentExpression(invar.value(), clazz, context);
			String testExpr  = 
				getDefaultTypeInvarTestExpressionMaker().makeDefaultTestExpressionIfEmpty(result.getMessage(), context);
			context.setActualTestExpression(testExpr);
			SystemCaches.testContextCache.put(key, new TestContextCache.Entry(context, argNames, argTypes, null, null));
		}
		// Capture "old" data (even though there are no old data...).
		context.setOldValuesMap (determineOldValues (context));
		getContractEnforcer().invokeTest("Invar", invar.message(), context);
	}
	
	private TestResult handleParentExpression(String testExpr, Class<?> clazz, TestContext context) {
		TestResult result = 
			getParentTestExpressionFinder().findParentTypeInvarTestExpressionIfEmpty(
					testExpr, clazz, context);
		if (result.isPassed() == false) {
			getContractEnforcer().fail(testExpr, "Invar", result.getMessage(),  
					context, new TestSpecificationError());
		}
		return result;
	}
}
