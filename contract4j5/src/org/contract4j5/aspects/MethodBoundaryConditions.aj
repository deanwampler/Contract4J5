/*
 * Copyright 2005 Dean Wampler. All rights reserved.
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

package org.contract4j.aspects;

import java.lang.annotation.Annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.contract4j.ContractEnforcer;
import org.contract4j.Instance;
import org.contract4j.Post;
import org.contract4j.Pre;
import org.contract4j.TestContext;
import org.contract4j.TestContextImpl;
import org.contract4j.interpreter.TestResult;
import org.contract4j.testexpression.DefaultPostTestExpressionMaker;
import org.contract4j.testexpression.DefaultPreTestExpressionMaker;
import org.contract4j.testexpression.DefaultTestExpressionMaker;
import org.contract4j.testexpression.ParentTestExpressionFinder;
import org.contract4j.testexpression.ParentTestExpressionFinderImpl;
import org.contract4j.testexpression.SimpleStringDefaultTestExpressionMaker;
import org.contract4j.util.MiscUtils;

/**
 * Test the method preconditions and postconditions.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public aspect MethodBoundaryConditions extends Contract4J {
	private DefaultTestExpressionMaker defaultPreTestExpressionMaker = null;
	
	/**
	 * @return the DefaultTestExpressionMaker for preconditions tests for methods
	 */
	public DefaultTestExpressionMaker getDefaultPreTestExpressionMaker() { 
		if (defaultPreTestExpressionMaker == null) {
			defaultPreTestExpressionMaker = new DefaultPreTestExpressionMaker();
		}
		return defaultPreTestExpressionMaker; 
	}
	
	/**
	 * @param maker DefaultTestExpressionMaker for preconditions tests for methods
	 */
	public void setDefaultPreTestExpressionMaker (DefaultTestExpressionMaker maker) { 
		defaultPreTestExpressionMaker = maker; 
	}

	private DefaultTestExpressionMaker defaultPostTestExpressionMaker = null;
	
	/**
	 * @return the DefaultTestExpressionMaker for postcondition tests for
	 * methods not returning void
	 */
	public DefaultTestExpressionMaker getDefaultPostTestExpressionMaker() { 
		if (defaultPostTestExpressionMaker == null) {
			defaultPostTestExpressionMaker = new DefaultPostTestExpressionMaker();
		}
		return defaultPostTestExpressionMaker; 
	}
	
	/**
	 * @param maker DefaultTestExpressionMaker for postcondition tests for
	 * methods not returning void
	 */
	public void setDefaultPostTestExpressionMaker (DefaultTestExpressionMaker maker) { 
		defaultPostTestExpressionMaker = maker; 
	}

	private DefaultTestExpressionMaker defaultPostReturningVoidTestExpressionMaker = null;
	
	/**
	 * @return the DefaultTestExpressionMaker for postcondition tests for
	 * methods returning void. (By default, the test expression itself will be "".)
	 */
	public DefaultTestExpressionMaker getDefaultPostReturningVoidTestExpressionMaker() { 
		if (defaultPostReturningVoidTestExpressionMaker == null) {
			defaultPostReturningVoidTestExpressionMaker = new SimpleStringDefaultTestExpressionMaker();
		}
		return defaultPostReturningVoidTestExpressionMaker; 
	}
	
	/**
	 * @param maker DefaultTestExpressionMaker for postcondition tests for
	 * methods returning void
	 */
	public void setDefaultPostReturningVoidTestExpressionMaker (DefaultTestExpressionMaker maker) { 
		defaultPostReturningVoidTestExpressionMaker = maker; 
	}

	private ParentTestExpressionFinder parentTestExpressionFinder = null;
	
	/**
	 * @return the parentTestExpressionFinder used to determine the text expression
	 * used by the corresponding annotation on the corresponding parent method, if any.
	 */
	public ParentTestExpressionFinder getParentTestExpressionFinder() {
		if (parentTestExpressionFinder == null) {
			parentTestExpressionFinder = new ParentTestExpressionFinderImpl();
		}
		return parentTestExpressionFinder;
	}

	/**
	 * @param parentTestExpressionFinder to use.
	 */
	public void setParentTestExpressionFinder(
			ParentTestExpressionFinder parentTestExpressionFinder) {
		this.parentTestExpressionFinder = parentTestExpressionFinder;
	}

	/**
	 * Method precondition PCD. Ignore static methods!
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut preMethod (Pre pre, ContractMarker obj) :
		preCommon() && !within (MethodBoundaryConditions) &&
		execution (@Pre !static * ContractMarker+.*(..)) &&
		@annotation (pre) && this (obj);

	/**
	 * Method returning void w/ postcondition PCD. Ignore static methods!
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut postVoidMethod (Post post, ContractMarker obj) :
		postCommon() && !within (MethodBoundaryConditions) &&
		execution (@Post !static void ContractMarker+.*(..)) &&
		@annotation (post) && this (obj);

	/**
	 * Method postcondition PCD, excluding the void special case. Ignore
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
		ExprAndContext eandc = 
			doBeforeTest (thisJoinPoint, obj, pre, "Pre", pre.value(), pre.message(),
				getDefaultPreTestExpressionMaker());
		getContractEnforcer().invokeTest(eandc.testExpr, "Pre", pre.message(), eandc.context);
	}

	/**
	 * After advice for methods, excluding methods returning void.
	 */
	Object around (Post post, ContractMarker obj) : postMethod (post, obj) {
		ExprAndContext eandc = 
			doBeforeTest (thisJoinPoint, obj, post, "Post", post.value(), post.message(),
				getDefaultPostTestExpressionMaker());
		eandc.context.setOldValuesMap (determineOldValues (eandc.testExpr, eandc.context));
		Object result = proceed (post, obj);
		eandc.context.getMethodResult().setValue(result);
		getContractEnforcer().invokeTest(eandc.testExpr, "Post", post.message(), eandc.context);
		return result;
	}

	/**
	 * After advice for methods returning void.
	 */
	void around (Post post, ContractMarker obj) : postVoidMethod (post, obj) {
		ExprAndContext eandc = 
			doBeforeTest (thisJoinPoint, obj, post, "Post", post.value(), post.message(),
				getDefaultPostReturningVoidTestExpressionMaker());
		eandc.context.setOldValuesMap (determineOldValues (eandc.testExpr, eandc.context));
		proceed (post, obj);
		getContractEnforcer().invokeTest(eandc.testExpr, "Post", post.message(), eandc.context);
	}
	
	/**
	 * Need to pass both the annotation and the fields extracted from our contract annotations, because
	 * Java won't let us have our annotations implement an interface with these fields, nor any common
	 * interface, so we have to pass everything in.
	 */
	protected ExprAndContext doBeforeTest (
			JoinPoint  thisJoinPoint, 
			Object     obj,
			Annotation anno,
			String     testTypeName,
			String     annoTestExpr, 
			String     testMessage,
			DefaultTestExpressionMaker maker) {
		Signature   signature  = thisJoinPoint.getSignature();
		MethodSignature ms     = (MethodSignature) signature;
		String      methodName = signature.getName();
		Class       clazz      = signature.getDeclaringType();
		String[]    argNames   = ms.getParameterNames();
		Class[]     argTypes   = ms.getParameterTypes();
		Object[]    argValues  = thisJoinPoint.getArgs();
		Instance[]  args       = MiscUtils.makeInstanceArray(argNames, argTypes, argValues);
		SourceLocation loc     = thisJoinPoint.getSourceLocation(); 
		Instance    instance   = new Instance (methodName, clazz, obj);
		// The returned value is set in the advice for post tests for 
		// functions not returning void.
		Instance    returnz    = new Instance ("", ms.getReturnType(), null);
		TestContext context    = 
			new TestContextImpl (methodName, instance, null, args, returnz, null,
					loc.getFileName(), loc.getLine());
		TestResult result = 
			getParentTestExpressionFinder().findParentMethodTestExpressionIfEmpty(
				annoTestExpr, anno, ms.getMethod(), context);
		if (result.isPassed() == false) {
			ContractEnforcer enf = getContractEnforcer();
			String msg = enf.makeFailureMessage (annoTestExpr, testTypeName, result.getMessage(), context, result);
			enf.handleFailure(msg);
		}
		String testExpr = result.getMessage(); 
		testExpr = maker.makeDefaultTestExpressionIfEmpty(testExpr, context);
		return new ExprAndContext (testExpr, context);
	}
}
