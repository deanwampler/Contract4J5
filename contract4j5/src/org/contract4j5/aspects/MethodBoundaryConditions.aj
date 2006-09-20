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
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */

package org.contract4j5.aspects;

import java.lang.annotation.Annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.contract4j5.Instance;
import org.contract4j5.Post;
import org.contract4j5.Pre;
import org.contract4j5.TestContext;
import org.contract4j5.TestContextImpl;
import org.contract4j5.TestSpecificationError;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.testexpression.DefaultPostTestExpressionMaker;
import org.contract4j5.testexpression.DefaultPreTestExpressionMaker;
import org.contract4j5.testexpression.DefaultTestExpressionMaker;
import org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker;
import org.contract4j5.util.InstanceUtils;

/**
 * Test the method preconditions and postconditions. Note that static methods 
 * are ignored, because they don't involve object state, the normal purview of
 * DbC. However, you still might want to use the mechanism to assert global,
 * static behavior, so a possible extension is to allow aspects on static methods.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public aspect MethodBoundaryConditions extends AbstractConditions {
	private  DefaultTestExpressionMaker defaultPreTestExpressionMaker;
	
	public DefaultTestExpressionMaker getDefaultPreTestExpressionMaker() { 
		if (defaultPreTestExpressionMaker == null)
			defaultPreTestExpressionMaker = new DefaultPreTestExpressionMaker();
		return defaultPreTestExpressionMaker; 
	}
	
	public void setDefaultPreTestExpressionMaker (DefaultTestExpressionMaker maker) { 
		defaultPreTestExpressionMaker = maker; 
	}

	private DefaultTestExpressionMaker defaultPostTestExpressionMaker;
	
	public DefaultTestExpressionMaker getDefaultPostTestExpressionMaker() { 
		if (defaultPostTestExpressionMaker == null)
			defaultPostTestExpressionMaker = new DefaultPostTestExpressionMaker();
		return defaultPostTestExpressionMaker; 
	}
	
	public void setDefaultPostTestExpressionMaker (DefaultTestExpressionMaker maker) { 
		defaultPostTestExpressionMaker = maker; 
	}

	private DefaultTestExpressionMaker defaultPostReturningVoidTestExpressionMaker;
	
	public DefaultTestExpressionMaker getDefaultPostReturningVoidTestExpressionMaker() { 
		if (defaultPostReturningVoidTestExpressionMaker == null)
			defaultPostReturningVoidTestExpressionMaker = 
				new SimpleStringDefaultTestExpressionMaker();
		return defaultPostReturningVoidTestExpressionMaker; 
	}
	
	public void setDefaultPostReturningVoidTestExpressionMaker (DefaultTestExpressionMaker maker) { 
		defaultPostReturningVoidTestExpressionMaker = maker; 
	}

	/**
	 * Method precondition PCD. Ignores static methods!
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut preMethod (Pre pre, ContractMarker obj) :
		preCommon() && !within (MethodBoundaryConditions) &&
		execution (@Pre !static * ContractMarker+.*(..)) &&
		@annotation (pre) && this (obj);

	/**
	 * Method returning void w/ postcondition PCD. Ignores static methods!
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut postVoidMethod (Post post, ContractMarker obj) :
		postCommon() && !within (MethodBoundaryConditions) &&
		execution (@Post !static void ContractMarker+.*(..)) &&
		@annotation (post) && this (obj);

	/**
	 * Method postcondition PCD, excluding the void special case. Ignores
	 * static methods!
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut postMethod (Post post, ContractMarker obj) :
		postCommon() && !within (MethodBoundaryConditions) &&
		execution (@Post !static !void ContractMarker+.*(..)) &&
		@annotation (post) && this (obj);
	
	/**
	 * Before advice for methods, including methods returning void.
	 */
	before (Pre pre, ContractMarker obj) : preMethod (pre, obj) {
		TestContext context = new TestContextImpl();
		String testExpr = 
			doBeforeTest (context, thisJoinPoint, obj, pre, "Pre", pre.value(), pre.message(),
				getDefaultPreTestExpressionMaker());
		getContractEnforcer().invokeTest (testExpr, "Pre", pre.message(), context);
	}

	/**
	 * After advice for methods, excluding methods returning void.
	 */
	Object around (Post post, ContractMarker obj) : postMethod (post, obj) {
		TestContext context = new TestContextImpl();
		String testExpr = 
			doBeforeTest (context, thisJoinPoint, obj, post, "Post", post.value(), post.message(),
				getDefaultPostTestExpressionMaker());
		context.setOldValuesMap (determineOldValues (testExpr, context));
		Object result = proceed (post, obj);
		context.getMethodResult().setValue(result);
		getContractEnforcer().invokeTest (testExpr, "Post", post.message(), context);
		return result;
	}

	/**
	 * After advice for methods returning void.
	 */
	void around (Post post, ContractMarker obj) : postVoidMethod (post, obj) {
		TestContext context = new TestContextImpl();
		String testExpr = 
			doBeforeTest (context, thisJoinPoint, obj, post, "Post", post.value(), post.message(),
				getDefaultPostReturningVoidTestExpressionMaker());
		context.setOldValuesMap (determineOldValues (testExpr, context));
		proceed (post, obj);
		getContractEnforcer().invokeTest (testExpr, "Post", post.message(), context);
	}
	
	/**
	 * Need to pass both the annotation and the fields extracted from our contract annotations, because
	 * Java won't let us have our annotations implement an interface with these fields, nor any common
	 * interface, so we have to pass everything in.
	 * @return the test expression
	 */
	protected String doBeforeTest (
			TestContext context,
			JoinPoint   thisJoinPoint, 
			Object      obj,
			Annotation  anno,
			String      testTypeName,
			String      annoTestExpr, 
			String      testMessage,
			DefaultTestExpressionMaker maker) {
		Signature   signature  = thisJoinPoint.getSignature();
		MethodSignature ms     = (MethodSignature) signature;
		String      methodName = signature.getName();
		Class       clazz      = signature.getDeclaringType();
		String[]    argNames   = ms.getParameterNames();
		Class[]     argTypes   = ms.getParameterTypes();
		Object[]    argValues  = thisJoinPoint.getArgs();
		Instance[]  args       = InstanceUtils.makeInstanceArray(argNames, argTypes, argValues);
		SourceLocation loc     = thisJoinPoint.getSourceLocation(); 
		Instance    instance   = new Instance (methodName, clazz, obj);
		// The returned value is set in the advice for post tests for 
		// functions not returning void.
		Instance    returnz    = new Instance ("", ms.getReturnType(), null);
		context.setItemName(methodName);
		context.setInstance(instance);
		context.setMethodResult(returnz);
		context.setLineNumber(loc.getLine());
		context.setFileName(loc.getFileName());
		context.setMethodArgs(args);
		TestResult result = 
			getParentTestExpressionFinder().findParentMethodTestExpressionIfEmpty(
				annoTestExpr, anno, ms.getMethod(), context);
		if (result.isPassed() == false) {
			getContractEnforcer().fail(annoTestExpr, testTypeName, result.getMessage(),  
					context, new TestSpecificationError());
		}
		String testExpr = result.getMessage(); 
		testExpr = maker.makeDefaultTestExpressionIfEmpty(testExpr, context);
		return testExpr;
	}
}
