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
	pointcut invarCtor (Contract contract, Invar invar, Object obj) : 
		invarCommon(contract, invar) && !within (InvariantCtorConditions) &&
		execution (@Invar *.new(..)) && 
		this (obj);

	after (Contract contract, Invar invar, Object obj) returning : invarCtor (contract, invar, obj) {
		ConstructorSignature cs = (ConstructorSignature) thisJoinPointStaticPart.getSignature();
		Class<?>    clazz     = obj.getClass();
		String[]    argNames  = cs.getParameterNames();
		Class<?>[]  argTypes  = cs.getParameterTypes();
		Object[]    argValues = thisJoinPoint.getArgs();
		Instance[]  args      = InstanceUtils.makeInstanceArray(argNames, argTypes, argValues);
		SourceLocation loc    = thisJoinPointStaticPart.getSourceLocation(); 
		Instance    instance  = new Instance (clazz.getName(), clazz, obj);
		TestContext context = 
			new TestContextImpl (clazz.getSimpleName(), clazz.getSimpleName(), instance, null, args, null, null,
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


