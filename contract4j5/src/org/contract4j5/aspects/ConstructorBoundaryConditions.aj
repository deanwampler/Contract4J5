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
import org.contract4j5.context.TestContextCache;
import org.contract4j5.contract.Disabled;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.controller.SystemCaches;
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
	pointcut preCtor (Pre pre) :
		preCommon() && !within(ConstructorBoundaryConditions) &&
		execution (@Pre new (..)) && ! withincode (@Disabled new (..)) &&
		target(Object) && @annotation(pre);

	/**
	 * Constructor postcondition PCD.
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut postCtor (Post post, Object obj) : 
		postCommon() && !within(ConstructorBoundaryConditions) &&
		execution (@Post new (..)) && ! withincode (@Disabled new (..)) &&
		target(obj) && @annotation(post) ;

	/**
	 * Before advice for constructors.
	 */
	before (Pre pre): preCtor (pre) {
		doTest (thisJoinPoint, null, pre, "Pre", pre.value(), pre.message(),
				getDefaultPreTestExpressionMaker());
	}

	/**
	 * After advice for constructors.
	 */
	after (Post post, Object obj): postCtor (post, obj) { 
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
		Object[]    argValues = thisJoinPoint.getArgs();
		TestContext context   = null;
		SourceLocation loc    = thisJoinPoint.getSourceLocation();
		String fileName = loc.getFileName();
		int    lineNum  = loc.getLine();
		TestContextCache.Key key = new TestContextCache.Key(testTypeName, fileName, lineNum);
		TestContextCache.Entry entry = SystemCaches.testContextCache.get(key);
		if (entry != null) {
			context = entry.testContext;
			Instance[]  args = InstanceUtils.makeInstanceArray(entry.argNames, entry.argTypes, argValues);
			context.setMethodArgs(args);
		} else {
			Signature   signature = thisJoinPoint.getSignature();
			ConstructorSignature cs = (ConstructorSignature) signature;
			Class<?>    clazz     = signature.getDeclaringType();
			Instance    instance  = new Instance (clazz.getName(), clazz, obj);
			String[]    argNames  = cs.getParameterNames();
			Class<?>[]  argTypes  = cs.getParameterTypes();
			Instance[]  args      = InstanceUtils.makeInstanceArray(argNames, argTypes, argValues);
			context   = new TestContextImpl (annoTestExpr, clazz.getSimpleName(), 
								instance, null, args, null, null, fileName, lineNum);
			TestResult  result    = 
				getParentTestExpressionFinder().findParentConstructorTestExpressionIfEmpty(
						annoTestExpr, anno, cs.getConstructor(), context);
			if (result.isPassed() == false) {
				getContractEnforcer().fail(annoTestExpr, testTypeName, result.getMessage(),  
						context, new TestSpecificationError());
			}
			String actualTestExpr = maker.makeDefaultTestExpressionIfEmpty (result.getMessage(), context);
			context.setActualTestExpression(actualTestExpr);
			SystemCaches.testContextCache.put(key, new TestContextCache.Entry(context, argNames, argTypes, null, null));
		}
		getContractEnforcer().invokeTest(testTypeName, testMessage, context);
	}
}
