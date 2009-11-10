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
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.testexpression.DefaultPreTestExpressionMaker;
import org.contract4j5.testexpression.DefaultTestExpressionMaker;
import org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker;
import org.contract4j5.instance.Instance;
import org.contract4j5.instance.InstanceUtils;

/**
 * Test the constructor preconditions and postconditions. Note that when
 * searching for test methods, we do not search the parent classes. This is
 * because these tests will likely be executed anyway, since in a good design,
 * a constructor always invokes "super(..)", but also the tests are best invoked
 * only in the context of the constructor for which they are defined.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public aspect ConstructorBoundaryConditions extends AbstractConditions {
	
	private DefaultTestExpressionMaker defaultPreTestExpressionMaker; 
	
	public DefaultTestExpressionMaker getDefaultPreTestExpressionMaker() { 
		if (defaultPreTestExpressionMaker == null)
			defaultPreTestExpressionMaker = new DefaultPreTestExpressionMaker();
		return defaultPreTestExpressionMaker; 
	}
	
	public void setDefaultPreTestExpressionMaker (DefaultTestExpressionMaker maker) { 
		defaultPreTestExpressionMaker = maker; 
	}

	private DefaultTestExpressionMaker defaultPostReturningVoidTestExpressionMaker;
	
	public DefaultTestExpressionMaker getDefaultPostReturningVoidTestExpressionMaker() { 
		if (defaultPostReturningVoidTestExpressionMaker == null) 
			defaultPostReturningVoidTestExpressionMaker = new SimpleStringDefaultTestExpressionMaker();
		return defaultPostReturningVoidTestExpressionMaker; 
	}
	
	public void setDefaultPostReturningVoidTestExpressionMaker (DefaultTestExpressionMaker maker) { 
		defaultPostReturningVoidTestExpressionMaker = maker; 
	}

	/**
	 * Constructor precondition PCD.
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut preCtor (Contract contract, Pre pre) :
		preCommon(contract, pre) && !within(ConstructorBoundaryConditions) &&
		execution (@Pre new (..)) && 
		target(Object);

	/**
	 * Constructor postcondition PCD.
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut postCtor (Contract contract, Post post, Object obj) : 
		postCommon(contract, post) && !within(ConstructorBoundaryConditions) &&
		execution (@Post new (..)) &&
		target(obj);

	/**
	 * Before advice for constructors.
	 */
	before (Contract contract, Pre pre): preCtor (contract, pre) {
		doTest (thisJoinPoint, null, pre, "Pre", pre.value(), pre.message(),
				getDefaultPreTestExpressionMaker());
	}

	/**
	 * After advice for constructors.
	 */
	after (Contract contract, Post post, Object obj): postCtor (contract, post, obj) { 
		doTest (thisJoinPoint, obj, post, "Post", post.value(), post.message(),
				getDefaultPostReturningVoidTestExpressionMaker());
	}
	
	/**
	 * Need to pass both the annotation and the fields extracted from our 
	 * contract annotations, because Java won't let us have our annotations 
	 * implement an interface with these fields, nor any common
	 * interface, so we have to pass everything in.
	 */
	protected void doTest (
			JoinPoint  thisJoinPoint, 
			Object     obj, 
			Annotation anno,
			String     testTypeName,
			String     annoTestExpr, 
			String     testMessage,
			DefaultTestExpressionMaker maker) {
		Signature   signature = thisJoinPoint.getSignature();
		ConstructorSignature cs = (ConstructorSignature) signature;
		String[]    argNames  = cs.getParameterNames();
		Class<?>[]  argTypes  = cs.getParameterTypes();
		Object[]    argValues = thisJoinPoint.getArgs();
		Instance[]  args      = InstanceUtils.makeInstanceArray(argNames, argTypes, argValues);
		Class<?>    clazz     = signature.getDeclaringType();
		SourceLocation loc    = thisJoinPoint.getSourceLocation(); 
		Instance    instance  = new Instance (clazz.getName(), clazz, obj);
		TestContext context   = 
			new TestContextImpl (clazz.getName(), clazz.getSimpleName(), instance, null, args, null, null, 
					loc.getFileName(), loc.getLine());
		TestResult  result    = 
			getParentTestExpressionFinder().findParentConstructorTestExpressionIfEmpty(
					annoTestExpr, anno, cs.getConstructor(), context);
		if (result.isPassed() == false) {
			getContractEnforcer().fail(annoTestExpr, testTypeName, result.getMessage(),  
					context, new TestSpecificationError());
		}
		String testExpr = result.getMessage(); 
		testExpr = maker.makeDefaultTestExpressionIfEmpty (testExpr, context);
		getContractEnforcer().invokeTest(testExpr, testTypeName, testMessage, context);
	}
}
