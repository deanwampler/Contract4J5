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

import org.aspectj.lang.Signature;
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
	pointcut invarMethod (Invar invar, Object obj) :
		invarCommon() && ! within (InvariantMethodConditions) &&
		execution (@Invar ! static * *.*(..)) && ! execution (@Disabled ! static * *.*(..)) &&
		this (obj) && @annotation(invar);

	Object around (Invar invar, Object obj) : invarMethod (invar, obj) {
		Signature  signature  = thisJoinPointStaticPart.getSignature();
		MethodSignature ms    = (MethodSignature) signature;
		TestContext context   = null;
		SourceLocation loc    = thisJoinPoint.getSourceLocation();
		String fileName = loc.getFileName();
		int    lineNum  = loc.getLine();
		TestContextCache.Key key = new TestContextCache.Key("Invar", fileName, lineNum);
		TestContextCache.Entry entry = SystemCaches.testContextCache.get(key);
		if (entry != null) {
			context = entry.testContext;
			context.getInstance().setValue(obj);
			Object[]   argValues  = thisJoinPoint.getArgs();
			Instance[] args       = InstanceUtils.makeInstanceArray(entry.argNames, entry.argTypes, argValues);
			context.setMethodArgs(args);
		} else {
			String     methodName = signature.getName();
			Class<?>   clazz      = obj.getClass();
			String[]   argNames   = ms.getParameterNames();
			Class<?>[] argTypes   = ms.getParameterTypes();
			Object[]   argValues  = thisJoinPoint.getArgs();
			Instance[] args       = InstanceUtils.makeInstanceArray(argNames, argTypes, argValues);
			Instance   instance   = new Instance (clazz.getName(), clazz, obj);
			context = new TestContextImpl (invar.value(), methodName, instance, 
							null, args, null, null, fileName, lineNum);
			TestResult result  = 
				getParentTestExpressionFinder().findParentMethodTestExpressionIfEmpty(
					invar.value(), invar, ms.getMethod(), context);
			if (result.isPassed() == false) {
				getContractEnforcer().fail(invar.value(), "Invar", result.getMessage(),  
						context, new TestSpecificationError());
			}
			String actualTestExpr = 
				getDefaultMethodInvarTestExpressionMaker().makeDefaultTestExpressionIfEmpty(result.getMessage(), context);
			context.setActualTestExpression(actualTestExpr);
			SystemCaches.testContextCache.put(key, new TestContextCache.Entry(context, argNames, argTypes, null, null));
		}
		context.setOldValuesMap (determineOldValues (context));
		getContractEnforcer().invokeTest("Invar", invar.message(), context);
		Object result2 = proceed (invar, obj);
		// Use "ms.getReturnType()", not "result2.getClass()", since result2 might be null!
		context.setMethodResult (new Instance ("", ms.getReturnType(), result2));
		getContractEnforcer().invokeTest("Invar", invar.message(), context);
		return result2;
	}
}
