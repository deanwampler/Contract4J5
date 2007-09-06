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

import java.lang.reflect.Field;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.contract4j5.testexpression.DefaultFieldInvarTestExpressionMaker;
import org.contract4j5.testexpression.DefaultTestExpressionMaker;
import org.contract4j5.testexpression.ParentTestExpressionFinder;
import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.contract.Invar;
import org.contract4j5.instance.Instance;

/** 
 * Test for field invariants in non-constructor contexts.  
 * The test is only executed after join point execution, to allow lazy 
 * evaluation, etc. 
 * @note A test expression of the form <code>$old(field).equals(field)</code>
 * effectively makes the field final, but it's better to use the <code>final</code>
 * keyword explicitly instead!
 * @note Unlike other aspects, the advice doesn't need to use a {@link 
 * ParentTestExpressionFinder} to locate a parent's test expression because 
 * fields aren't overridden by derived classes. The tests only need to be 
 * defined in the class once and the inheritance issues with type and method
 * tests aren't an issue here.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public aspect InvariantFieldConditions extends AbstractConditions {
	private DefaultTestExpressionMaker defaultFieldInvarTestExpressionMaker;

	public DefaultTestExpressionMaker getDefaultFieldInvarTestExpressionMaker() { 
		if (defaultFieldInvarTestExpressionMaker == null)
			defaultFieldInvarTestExpressionMaker = new DefaultFieldInvarTestExpressionMaker();
		return defaultFieldInvarTestExpressionMaker; 
	}

	public void setDefaultFieldInvarTestExpressionMaker (DefaultTestExpressionMaker maker) { 
		defaultFieldInvarTestExpressionMaker = maker; 
	}

	/**
	 * Field invariant PCD for non-constructor contexts, which are handled 
	 * separately below because the fields are by definition being 
	 * initialized within the constructor, so special handling is required.
	 * @note We use cflowbelow() to exclude the constructor execution, rather than 
	 * withincode(), because the constructor may call other methods.
	 * @note We prevent recursion into the aspect itself.
	 */
	pointcut invarFieldCommon (Invar invar, Object obj) :
		invarCommon() && ! cflowbelow (execution (*.new(..))) &&
		!within (InvariantFieldConditions) &&
		@annotation (invar) && target (obj);
	
	pointcut invarSetField (Invar invar, Object obj, Object arg) :
		invarFieldCommon (invar, obj) && set (@Invar * *.*) && args(arg); 

	pointcut invarGetField (Invar invar, Object obj) :
		invarFieldCommon (invar, obj) && get (@Invar * *.*); 

	void around (Invar invar, Object obj, Object arg) : invarSetField (invar, obj, arg) {
		// Set up the context so we can retrieve any "old" values, before proceeding.
		TestContext context = doBeforeTest (thisJoinPoint, 
					obj, arg, "Invar", invar.value(), invar.message(),
					getDefaultFieldInvarTestExpressionMaker());
		String testExpr = getDefaultFieldInvarTestExpressionMaker().makeDefaultTestExpressionIfEmpty(context.getTestExpression(), context);
		context.setOldValuesMap (determineOldValues (testExpr, context));
		proceed (invar, obj, arg);
		getContractEnforcer().invokeTest(testExpr, "Invar", invar.message(), context);
	}

	/** 
	 * Advice for field "gets". 
	 * @note When we set up the context to retrieve "old" values, if any,
	 * in the test expression, we can't get the actual field value until 
	 * after executing the "proceed". This should be okay, as it doesn't
	 * make much sense to write a test expression with "old" and new values
	 * for this field, at least.
	 */
	Object around (Invar invar, Object obj) : invarGetField (invar, obj) {
		TestContext context = doBeforeTest (thisJoinPoint, 
					obj, null, "Invar", invar.value(), invar.message(),
					getDefaultFieldInvarTestExpressionMaker());
		String testExpr = getDefaultFieldInvarTestExpressionMaker().makeDefaultTestExpressionIfEmpty(context.getTestExpression(), context);
		context.setOldValuesMap (determineOldValues (testExpr, context));
		Object fieldValue2 = proceed (invar, obj);
		// Actually use the "new" value of the field for the test.
		context.getField().setValue (fieldValue2);  // The field is the target...
		context.setMethodResult (context.getField());  // ... and the return value!
		getContractEnforcer().invokeTest(testExpr, "Invar", invar.message(), context);
		return fieldValue2;
	}

	protected TestContext doBeforeTest (
			JoinPoint   thisJoinPoint, 
			Object      obj,
			Object      fieldValue,
			String      testTypeName,
			String      annoTestExpr, 
			String      testMessage,
			DefaultTestExpressionMaker maker) {
		Signature sig = thisJoinPoint.getSignature();
		assert (sig instanceof FieldSignature);
		String fieldName     = sig.getName();
		Class  clazz         = sig.getDeclaringType();
		SourceLocation loc   = thisJoinPoint.getSourceLocation(); 
		// Get the "old" value of the field. We need it now, even though we
		// don't test with it, so that the default test expression can be
		// constructed properly.
		Field  field        = ((FieldSignature) sig).getField();
		Class  fieldClass   = field.getType();
		Instance instance   = new Instance(clazz.getName(), clazz, obj);
		Instance fieldInstance = new Instance(fieldName, fieldClass, fieldValue);
		TestContext context = new TestContextImpl(annoTestExpr, fieldName,
				instance, fieldInstance, null, null, loc.getFileName(), loc.getLine());
		return context;
	}
}
