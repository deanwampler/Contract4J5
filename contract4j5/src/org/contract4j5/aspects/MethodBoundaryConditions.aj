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
import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextCache;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.testexpression.DefaultPostTestExpressionMaker;
import org.contract4j5.testexpression.DefaultPreTestExpressionMaker;
import org.contract4j5.testexpression.DefaultTestExpressionMaker;
import org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker;
import org.contract4j5.instance.Instance;
import org.contract4j5.instance.InstanceUtils;

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
	pointcut preMethod (Contract contract, Pre pre, Object obj) :
		preCommon(contract, pre) && !within (MethodBoundaryConditions) &&
		execution (@Pre !static * *.*(..)) &&
		this (obj);

	/**
	 * Method returning void w/ postcondition PCD. Ignores static methods!
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut postVoidMethod (Contract contract, Post post, Object obj) :
		postCommon(contract, post) && !within (MethodBoundaryConditions) &&
		execution (@Post !static void *.*(..)) &&
		this (obj);

	/**
	 * Method postcondition PCD, excluding the void special case. Ignores
	 * static methods!
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut postMethod (Contract contract, Post post, Object obj) :
		postCommon(contract, post) && !within (MethodBoundaryConditions) &&
		execution (@Post !static !void *.*(..)) &&
		this (obj);
	
	static protected class Bucket {
		public String testExpr;
		public TestContext context;
		public Bucket(String expr, TestContext context) {
			this.testExpr = expr;
			this.context = context;
		}
	}
	
	/**
	 * Before advice for methods, including methods returning void.
	 */
	before (Contract contract, Pre pre, Object obj) : preMethod (contract, pre, obj) {
		Bucket bucket = doBeforeTest (thisJoinPoint, obj, pre, "Pre", pre.value(), pre.message(),
				getDefaultPreTestExpressionMaker());
		TestContext context = bucket.context;
		getContractEnforcer().invokeTest (bucket.testExpr, "Pre", pre.message(), context);
	}

	/**
	 * After advice for methods, excluding methods returning void.
	 */
	Object around (Contract contract, Post post, Object obj) : postMethod (contract, post, obj) {
		Bucket bucket = doBeforeTest (thisJoinPoint, obj, post, "Post", post.value(), post.message(),
				getDefaultPostTestExpressionMaker());
		TestContext context = bucket.context;
		context.setOldValuesMap (determineOldValues (context.getTestExpression(), context));
		Object result = proceed (contract, post, obj);
		context.getMethodResult().setValue(result);
		getContractEnforcer().invokeTest (bucket.testExpr, "Post", post.message(), context);
		return result;
	}

	/**
	 * After advice for methods returning void.
	 */
	void around (Contract contract, Post post, Object obj) : postVoidMethod (contract, post, obj) {
		Bucket bucket = doBeforeTest (thisJoinPoint, obj, post, "Post", post.value(), post.message(),
				getDefaultPostReturningVoidTestExpressionMaker());
		TestContext context = bucket.context;
		context.setOldValuesMap (determineOldValues (context.getTestExpression(), context));
		proceed (contract, post, obj);
		getContractEnforcer().invokeTest (bucket.testExpr, "Post", post.message(), context);
	}
	
	/**
	 * Need to pass both the annotation and the fields extracted from our contract annotations, because
	 * Java won't let us have our annotations implement an interface with these fields, nor any common
	 * interface, so we have to pass everything in.
	 */
	protected Bucket doBeforeTest (
			JoinPoint   thisJoinPoint, 
			Object      obj,
			Annotation  anno,
			String      testTypeName,
			String      annoTestExpr, 
			String      testMessage,
			DefaultTestExpressionMaker maker) {
		Signature   signature  = thisJoinPoint.getSignature();
		MethodSignature ms     = (MethodSignature) signature;
		
		TestContext context   = null;
		String testExpr       = "";
		SourceLocation loc    = thisJoinPoint.getSourceLocation();
		String fileName = loc.getFileName();
		int    lineNum  = loc.getLine();
		TestContextCache.Key key = new TestContextCache.Key("Invar", fileName, lineNum);
		TestContextCache.Entry entry = contextCache.get(key);
		if (context != null) {
			context = entry.testContext;
			testExpr = entry.testExpression;
			Object[]    argValues  = thisJoinPoint.getArgs();
			Instance[]  args       = InstanceUtils.makeInstanceArray(entry.argNames, entry.argTypes, argValues);
			context.setMethodArgs(args);
		} else {
			String      methodName = signature.getName();
			Class<?>    clazz      = signature.getDeclaringType();
			String[]    argNames   = ms.getParameterNames();
			Class<?>[]  argTypes   = ms.getParameterTypes();
			Object[]    argValues  = thisJoinPoint.getArgs();
			Instance[]  args       = InstanceUtils.makeInstanceArray(argNames, argTypes, argValues);
			Instance    instance   = new Instance (methodName, clazz, obj);
			// The returned value is set in the advice for post tests for 
			// functions not returning void.
			Instance    returnz    = new Instance ("", ms.getReturnType(), null);
			TestResult result = 
				getParentTestExpressionFinder().findParentMethodTestExpressionIfEmpty(
					annoTestExpr, anno, ms.getMethod(), null);
			context = new TestContextImpl(annoTestExpr, methodName, instance, 
								null, args, returnz, fileName, lineNum);
			if (result.isPassed() == false) {
				getContractEnforcer().fail(annoTestExpr, testTypeName, result.getMessage(),  
						context, new TestSpecificationError());
			}
			testExpr = maker.makeDefaultTestExpressionIfEmpty(result.getMessage(), context);
			context.setTestExpression(testExpr);
			contextCache.put(key, new TestContextCache.Entry(context, testExpr, argNames, argTypes, null, null));
		}
		return new Bucket(testExpr, context);
	}
}
