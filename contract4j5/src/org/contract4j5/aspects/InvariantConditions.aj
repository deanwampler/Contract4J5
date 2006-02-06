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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.contract4j.Contract;
import org.contract4j.ContractEnforcer;
import org.contract4j.Instance;
import org.contract4j.Invar;
import org.contract4j.TestContext;
import org.contract4j.TestContextImpl;
import org.contract4j.interpreter.TestResult;
import org.contract4j.testexpression.DefaultFieldInvarTestExpressionMaker;
import org.contract4j.testexpression.DefaultTestExpressionMaker;
import org.contract4j.testexpression.ParentTestExpressionFinder;
import org.contract4j.testexpression.ParentTestExpressionFinderImpl;
import org.contract4j.testexpression.SimpleStringDefaultTestExpressionMaker;
import org.contract4j.util.MiscUtils;

/**
 * Test the type (class, aspect, ...), method, and fields invariants.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public aspect InvariantConditions extends Contract4J {
	/** 
	 * Test for field invariants in non-ctor contexts.  
	 * The test is only executed after execution, to allow lazy evaluation, etc. 
	 * @note A test expression of the form <code>$old(field).equals(field)</code>
	 * effectively makes the field final, but it's better to use the <code>final</code>
	 * keyword explicitly instead!
	 * @note The advice doesn't need to use a {@lnk ParentTestExpressionFinder} to locate
	 * a parent's test expression because fields aren't overridden by derived classes!
	 * @note We declare a separate nested aspect so we can define the precedence.
	 */
	public static aspect InvariantFieldConditions extends Contract4J {
		private static DefaultTestExpressionMaker defaultFieldInvarTestExpressionMaker = null;

		/**
		 * @return the DefaultTestExpressionMaker for default field invariant test 
		 */
		public static DefaultTestExpressionMaker getDefaultFieldInvarTestExpressionMaker() { 
			if (defaultFieldInvarTestExpressionMaker == null) {
				defaultFieldInvarTestExpressionMaker = new DefaultFieldInvarTestExpressionMaker();
			}
			return defaultFieldInvarTestExpressionMaker; 
		}

		/**
		 * @param maker DefaultTestExpressionMaker for default field invariant test 
		 */
		public static void setDefaultFieldInvarTestExpressionMaker (DefaultTestExpressionMaker maker) { 
			defaultFieldInvarTestExpressionMaker = maker; 
		}

		/**
		 * Field invariant PCD for non-ctor contexts, which are handled separately
		 * below because the fields are by definition being initialized within the
		 * constructor.
		 * @note We use cflowbelow() to exclude the constructor execution, rather than 
		 * withincode(), because the constructor may call other methods.
		 * @note We prevent recursion into the aspect itself.
		 */
		pointcut invarFieldCommon (Invar invar, ContractMarker obj) :
			invarCommon() && ! cflowbelow (execution (ContractMarker+.new(..))) &&
			!within (InvariantFieldConditions) &&
			@annotation (invar) && target (obj);
		
		pointcut invarSetField (Invar invar, ContractMarker obj, Object arg) :
			invarFieldCommon (invar, obj) && set (@Invar * ContractMarker+.*) && args(arg); 

		pointcut invarGetField (Invar invar, ContractMarker obj) :
			invarFieldCommon (invar, obj) && get (@Invar * ContractMarker+.*); 

		void around (Invar invar, Object obj, Object arg) : invarSetField (invar, obj, arg) {
			// Set up the context so we can retrieve any "old" values, if any. Note that we
			// don't actually invoke any tests until after proceeding.
			ExprAndContext eandc = 
				doBeforeTest (thisJoinPoint, obj, arg, "Invar", invar.value(), invar.message(),
						getDefaultFieldInvarTestExpressionMaker());
			eandc.context.setOldValuesMap (determineOldValues (eandc.testExpr, eandc.context));
			proceed (invar, obj, arg);
			getContractEnforcer().invokeTest(eandc.testExpr, "Invar", invar.message(), eandc.context);
		}

		Object around (Invar invar, Object obj) : invarGetField (invar, obj) {
			// Set up the context so we can retrieve any "old" values, if any. 
			// We can't get the actual field value until after executing the "proceed".
			// Note that we don't actually invoke any tests until after proceeding.
			ExprAndContext eandc = 
				doBeforeTest (thisJoinPoint, obj, null, "Invar", invar.value(), invar.message(),
						getDefaultFieldInvarTestExpressionMaker());
			eandc.context.setOldValuesMap (determineOldValues (eandc.testExpr, eandc.context));
			Object fieldValue2 = proceed (invar, obj);
			// Actually use the "new" value of the field for the test.
			eandc.context.getField().setValue (fieldValue2);  // The field is the target...
			eandc.context.setMethodResult (eandc.context.getField());  // ... and the return value!
			getContractEnforcer().invokeTest(eandc.testExpr, "Invar", invar.message(), eandc.context);
			return fieldValue2;
		}

		protected ExprAndContext doBeforeTest (
				JoinPoint thisJoinPoint, 
				Object    obj,
				Object    fieldValue,
				String    testTypeName,
				String    annoTestExpr, 
				String    testMessage,
				DefaultTestExpressionMaker maker) {
			Signature sig = thisJoinPoint.getSignature();
			assert (sig instanceof FieldSignature);
			String fieldName     = sig.getName();
			Class  clazz         = sig.getDeclaringType();
			SourceLocation loc   = thisJoinPoint.getSourceLocation(); 
			// Get the "old" value of the field. We need it now, even though we
			// don't test with it, so that the default test expression can be
			// constructed properly.
			Field  field         = ((FieldSignature) sig).getField();
			Class  fieldClass    = field.getType();
			Instance i           = new Instance(clazz.getName(), clazz, obj);
			Instance f           = new Instance(fieldName, fieldClass, fieldValue);
			TestContext context  = 
				new TestContextImpl (fieldName, i, f, null, null, null,
						loc.getFileName(), loc.getLine());
			String testExpr = 
				getDefaultFieldInvarTestExpressionMaker().makeDefaultTestExpressionIfEmpty(annoTestExpr, context);
			return new ExprAndContext (testExpr, context);
		}
	}
	
	/** 
	 * Test for field invariants in ctor contexts. There is no pointcut defined for 
	 * the before case since the state should only be checked after the object is initialized.
	 * After the c'tor completes, all field invariants are tested. The rationale is that
	 * if a field invariant is defined, then it must hold after construction. 
	 * However, this forces c'tors to always initialize all such fields, rather than 
	 * allow lazy evaluation later. Nevertheless, that isn't a practical problem because a
	 * lazy evaluation will be performed inside an accessor, so in this case the invariant 
	 * should be written as a test on the accessor.
	 * @note The advice doesn't need to use a {@lnk ParentTestExpressionFinder} to locate
	 * a parent's test expression because fields aren't overridden by derived classes!
	 * @note As before, we declare a separate nested aspect so we can define the precedence.
	 */
	public static aspect InvariantFieldCtorConditions extends Contract4J percflow(invarFieldCtorCall (ContractMarker)) {
		private static DefaultTestExpressionMaker defaultFieldInvarTestExpressionMaker = null;

		/**
		 * @return the DefaultTestExpressionMaker for default field invariant test 
		 */
		public static DefaultTestExpressionMaker getDefaultFieldInvarTestExpressionMaker() { 
			if (defaultFieldInvarTestExpressionMaker == null) {
				defaultFieldInvarTestExpressionMaker = new DefaultFieldInvarTestExpressionMaker();
			}
			return defaultFieldInvarTestExpressionMaker; 
		}

		/**
		 * @param maker DefaultTestExpressionMaker for default field invariant test 
		 */
		public static void setDefaultFieldInvarTestExpressionMaker (DefaultTestExpressionMaker maker) { 
			defaultFieldInvarTestExpressionMaker = maker; 
		}

		private static class ListElem {
			public Invar    invar;
			public Instance field;  // The object assigned to the field
			public ListElem (Invar invar, Instance field) {
				this.invar = invar;
				this.field = field;
			}
		}
		
		// Remember the annotated fields set inside the c'tor call. Why a map?
		// Because the field may be assigned several times. In fact, the initial
		// assignments in the declarations, which may be deliberately invalid, will
		// also be picked up. Using a map, rather than a list, means me will only
		// keep the last assignement, not any potentially-invalid intermediate
		// assignments.
		private HashMap<String, ListElem> listOfAnnosFound = null;
		
		/**
		 * The enclosing scope of a ctor call. 
		 * @note We prevent recursion into the aspect itself.
		 */
		pointcut invarFieldCtorCall (ContractMarker obj) : 
			invarCommon() && !within(InvariantFieldCtorConditions) &&
			execution (ContractMarker+.new(..)) && target (obj);
				
		/**
		 * Field invariant pointcut within c'tor context. We match on the "cflowbelow" of the
		 * constructor call and "within" even though it's less runtime efficient. Otherwise, set join points
		 * nested in method calls within the c'tor will be ignored!
		 * @note We prevent recursion into the aspect itself.
		 */
		pointcut invarFieldInCtor (Invar invar, ContractMarker obj, Object field) :
			invarCommon() && !within(InvariantFieldCtorConditions) &&
			cflowbelow (invarFieldCtorCall (ContractMarker)) && 
			set (@Invar * ContractMarker+.*) &&
			@annotation (invar) && target (obj) && args (field);
			
		/**
		 * Observe any annotated field sets within the c'tor and record the invariant specification.
		 */
		after (Invar invar, ContractMarker obj, Object newFieldValue) returning : 
			invarFieldInCtor (invar, obj, newFieldValue) {
			if (listOfAnnosFound == null) {
				listOfAnnosFound = new HashMap<String,ListElem>();
			}
			Signature sig = thisJoinPointStaticPart.getSignature();
			assert (sig instanceof FieldSignature);
 			FieldSignature  fsig = (FieldSignature) sig;
 			Field          field = fsig.getField();
 			String          name = field.getName();
 			Instance    instance = new Instance(name, field.getType(), newFieldValue);
			listOfAnnosFound.put (name, new ListElem(invar, instance));
		}
		
		/**
		 * After the c'tor completes, if there were any annotated fields set, then test them.
		 */
		after(ContractMarker obj) returning : invarFieldCtorCall(obj) {
			if (listOfAnnosFound == null) {
				return;
			}
			Instance instance = new Instance(obj.getClass().getName(), obj.getClass(), obj);
			for (Map.Entry<String,ListElem> entry: listOfAnnosFound.entrySet()) {
				ListElem elem = entry.getValue();
				SourceLocation loc = thisJoinPointStaticPart.getSourceLocation(); 
				TestContext context =
					new TestContextImpl (entry.getKey(), instance, elem.field, null, null, null,
							loc.getFileName(), loc.getLine());
				String testExpr = 
					getDefaultFieldInvarTestExpressionMaker().makeDefaultTestExpressionIfEmpty(elem.invar.value(), context);
				getContractEnforcer().invokeTest(testExpr, "Invar", elem.invar.message(), context);
			}
		}
	}
	
	/** 
	 * Tests for methods, 
	 * @note We declare a separate nested aspect so we can define the precedence.
	 */
	public static aspect InvariantMethodConditions extends Contract4J {
		private	DefaultTestExpressionMaker defaultMethodInvarTestExpressionMaker = null;
		
		/**
		 * Return the default method invariant test expression maker.
		 * TODO Make the default test that the object state does not change. This suggests 
		 * the default test expression should be <code>$old($this).equals($this)</code>. 
		 * In order to support this, it will be necessary to use reflection to determine 
		 * each non-transient field and to remember its value in the "context". (We can't
		 * assume that "clone()" is supported on the object!!) However, this is imperfect 
		 * for fields that are mutable references, as they might change!
		 * @return the DefaultTestExpressionMaker for method invariant tests
		 */
		public DefaultTestExpressionMaker getDefaultMethodInvarTestExpressionMaker() { 
			if (defaultMethodInvarTestExpressionMaker == null) {
				defaultMethodInvarTestExpressionMaker = new SimpleStringDefaultTestExpressionMaker();
			}
			return defaultMethodInvarTestExpressionMaker; 
		}
		
		/**
		 * @return the DefaultTestExpressionMaker for method invariant tests
		 */
		public void setDefaultMethodInvarTestExpressionMaker (DefaultTestExpressionMaker maker) { 
			defaultMethodInvarTestExpressionMaker = maker; 
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
		 * Method invariant before and after PCD.
		 * @note We prevent recursion into the aspect itself.
		 */
		pointcut invarMethod (Invar invar, ContractMarker obj) :
			invarCommon() && !within (InvariantMethodConditions) &&
			execution (@Invar !static * ContractMarker+.*(..)) && 
			@annotation (invar) && this (obj);

		Object around (Invar invar, Object obj) : invarMethod (invar, obj) {
			Signature  signature  = thisJoinPointStaticPart.getSignature();
			MethodSignature ms    = (MethodSignature) signature;
			String     methodName = signature.getName();
			Class      clazz      = obj.getClass();
			String[]   argNames   = ms.getParameterNames();
			Class[]    argTypes   = ms.getParameterTypes();
			Object[]   argValues  = thisJoinPoint.getArgs();
			Instance[] args       = MiscUtils.makeInstanceArray(argNames, argTypes, argValues);
			SourceLocation loc    = thisJoinPointStaticPart.getSourceLocation(); 
			Instance   instance   = new Instance (clazz.getName(), clazz, obj);
			TestContext context   = 
				new TestContextImpl (methodName, instance, null, args, null, null,
					loc.getFileName(), loc.getLine());
			TestResult result  = 
				getParentTestExpressionFinder().findParentMethodTestExpressionIfEmpty(
					invar.value(), invar, ms.getMethod(), context);
			if (result.isPassed() == false) {
				ContractEnforcer enf = getContractEnforcer();
				String msg = enf.makeFailureMessage (invar.value(), "Invar", result.getMessage(), context, result);
				enf.handleFailure(msg);
			}
			String testExpr = result.getMessage(); 
			testExpr = 
				getDefaultMethodInvarTestExpressionMaker().makeDefaultTestExpressionIfEmpty(testExpr, context);
			context.setOldValuesMap (determineOldValues (testExpr, context));
			getContractEnforcer().invokeTest(testExpr, "Invar", invar.message(), context);
			Object result2 = proceed (invar, obj);
			// Use "ms.getReturnType()", not "result2.getClass()", since result2 might be null!
			context.setMethodResult (new Instance ("", ms.getReturnType(), result2));
			getContractEnforcer().invokeTest(testExpr, "Invar", invar.message(), context);
			return result2;
		}

	}
	
	/** 
	 * Tests for constructors.
	 * @note We declare a separate nested aspect so we can define the precedence.
	 */
	public static aspect InvariantCtorConditions extends Contract4J {
		private DefaultTestExpressionMaker defaultCtorInvarTestExpressionMaker = null;

		/**
		 * @return the DefaultTestExpressionMaker for consructor invariant tests. (Defaults to an
		 * empty string.) 
		 */
		public DefaultTestExpressionMaker getDefaultCtorInvarTestExpressionMaker() { 
			if (defaultCtorInvarTestExpressionMaker == null) {
				defaultCtorInvarTestExpressionMaker = new SimpleStringDefaultTestExpressionMaker();
			}
			return defaultCtorInvarTestExpressionMaker; 
		}
		
		/**
		 * @return the DefaultTestExpressionMaker  for consructor invariant tests 
		 */
		public void setDefaultCtorInvarTestExpressionMaker (DefaultTestExpressionMaker maker) { 
			defaultCtorInvarTestExpressionMaker = maker; 
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
		 * Constructor invariant PCD.
		 * @note We prevent recursion into the aspect itself.
		 */
		pointcut invarCtor (Invar invar, ContractMarker obj) : 
			invarCommon() && !within (InvariantCtorConditions) &&
			execution (@Invar ContractMarker+.new(..)) && 
			@annotation (invar) && this (obj);

		after (Invar invar, ContractMarker obj) returning : invarCtor (invar, obj) {
			ConstructorSignature cs = (ConstructorSignature) thisJoinPointStaticPart.getSignature();
			Class       clazz     = obj.getClass();
			String[]    argNames  = cs.getParameterNames();
			Class[]     argTypes  = cs.getParameterTypes();
			Object[]    argValues = thisJoinPoint.getArgs();
			Instance[]  args      = MiscUtils.makeInstanceArray(argNames, argTypes, argValues);
			SourceLocation loc    = thisJoinPointStaticPart.getSourceLocation(); 
			Instance    instance  = new Instance (clazz.getName(), clazz, obj);
			TestContext context = 
				new TestContextImpl (clazz.getSimpleName(), instance, null, args, null, null,
						loc.getFileName(), loc.getLine());
			TestResult result = 
				getParentTestExpressionFinder().findParentConstructorTestExpressionIfEmpty(
					invar.value(), invar, cs.getConstructor(), context);
			if (result.isPassed() == false) {
				ContractEnforcer enf = getContractEnforcer();
				String msg = enf.makeFailureMessage (invar.value(), "Invar", result.getMessage(), context, result);
				enf.handleFailure(msg);
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
	
	/** 
	 * Tests for type invariants. We have to handle two cases, one where the
	 * Invar annotation is explicitly declared on the type and one where it
	 * inherits the declaration. We conveniently handle both with ITD of a
	 * marker interface.
	 * @note We call {@link Object#clone()} on the instance to snapshot the "before"
	 * state. If this throws a {@link CloneNotSupportedException}, then any tests
	 * that try to compare the before and after state will give potentially misleading
	 * results!
	 * @note The invariant type checking method looks identical to an invariant
	 * test for a default c'tor, but in fact it isn't, because
	 * these type tests are not static, while c'tor tests are static!
	 * @note We considered using "pertypewithin(...Invarmarker+)", but the problem is
	 * that the aspects inherited properties, e.g., the contract enforcer are not wired
	 * for each instance (unless you use a container like Spring's IoC/DI container). 
	 * Therefore, we use a singleton instantiation model and explicit "this()" JPs.
	 * @note We declare a separate nested aspect so we can define the precedence.
	 */
	public static aspect InvariantTypeConditions extends Contract4J {
		private DefaultTestExpressionMaker defaultTypeInvarTestExpressionMaker = null;
		
		/**
		 * @return the DefaultTestExpressionMaker for type invariant tests.
		 * (By default, the test expression itself will be "".)
		 */
		public DefaultTestExpressionMaker getDefaultTypeInvarTestExpressionMaker() { 
			if (defaultTypeInvarTestExpressionMaker == null) {
				defaultTypeInvarTestExpressionMaker = new SimpleStringDefaultTestExpressionMaker();
			}
			return defaultTypeInvarTestExpressionMaker; 
		}
		
		/**
		 * @return the DefaultTestExpressionMaker for type invariant tests
		 */
		public void setDefaultTypeInvarTestExpressionMaker (DefaultTestExpressionMaker maker) { 
			defaultTypeInvarTestExpressionMaker = maker; 
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
		 * Marker interface that is used with intertype declaration to support the
		 * desired inheritance of type invariants.
		 */
		public static interface InvarMarker {}
		
		declare parents: (@Contract @Invar *) implements InvarMarker;
			
		/**
		 * PCD for type (class, aspect, ...) invariant tests that should be evaluated
		 * as both before and after advice. (We actually use around advice).
		 * In addition to excluding static methods, we don't advise 
		 * calls to or within constructors here, nor calls to get/set methods, 
		 * all of which may do initializations of data.
		 * These cases are handled separately.
		 * @note We prevent recursion into the aspect itself.
		 */
		pointcut invarTypeMethod(Invar invar, InvarMarker obj) :
			invarCommon() && !within (InvariantTypeConditions) &&
			execution (!static * InvarMarker+.*(..)) &&
			! (execution (* InvarMarker+.get*(..)) || execution (* InvarMarker+.set*(..))) &&
			! cflow (execution (InvarMarker+.new(..))) &&
			@this (invar) && this (obj);

		/**
		 * PCD for type (class, aspect, ...) invariant tests that are only evaluated
		 * as after advice, i.e., get/set methods. Constructor cflows are again excluded 
		 * and are handled separately below.
		 * @note We prevent recursion into the aspect itself.
		 */
		pointcut invarTypeGetSet (Invar invar, InvarMarker obj) :
			invarCommon() && !within (InvariantTypeConditions) &&
			(execution (* InvarMarker+.get*(..)) || execution (* InvarMarker+.set*(..))) &&
			! cflowbelow (execution (InvarMarker+.new(..))) &&
			@this (invar) && this (obj); 

		/**
		 * PCD for type (class, aspect, ...) invariant tests for after advice after
		 * c'tor execution.
		 * @note We prevent recursion into the aspect itself.
		 */
		pointcut invarTypeCtor (Invar invar, InvarMarker obj) : 
			invarCommon() && !within (InvariantTypeConditions) &&
			execution (InvarMarker+.new(..)) &&
			@this (invar) && this (obj);

		
		Object around (Invar invar, InvarMarker obj) : 
				invarTypeMethod (invar, obj) || invarTypeGetSet (invar, obj) {
			MethodSignature ms   = (MethodSignature) thisJoinPointStaticPart.getSignature();
			Class      clazz     = obj.getClass();
			String[]   argNames  = ms.getParameterNames();
			Class[]    argTypes  = ms.getParameterTypes();
			Object[]   argValues = thisJoinPoint.getArgs();
			Instance[] args      = MiscUtils.makeInstanceArray(argNames, argTypes, argValues);
			SourceLocation loc   = thisJoinPointStaticPart.getSourceLocation(); 
			Instance   instance  = new Instance (clazz.getName(), clazz, obj);
			TestContext context  = 
				new TestContextImpl (clazz.getName(), instance, null, args, null, null,
						loc.getFileName(), loc.getLine());
			TestResult result = 
				getParentTestExpressionFinder().findParentTypeInvarTestExpressionIfEmpty(
					invar.value(), clazz, context);
			if (result.isPassed() == false) {
				ContractEnforcer enf = getContractEnforcer();
				String msg = enf.makeFailureMessage (invar.value(), "Invar", result.getMessage(), context, result);
				enf.handleFailure(msg);
			}
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
		
		after(Invar invar, InvarMarker obj) returning () : invarTypeCtor (invar, obj) {
			ConstructorSignature cs = 
				(ConstructorSignature) thisJoinPointStaticPart.getSignature();
			Class      clazz     = obj.getClass();
			String[]   argNames  = cs.getParameterNames();
			Class[]    argTypes  = cs.getParameterTypes();
			Object[]   argValues = thisJoinPoint.getArgs();
			Instance[] args      = MiscUtils.makeInstanceArray(argNames, argTypes, argValues);
			SourceLocation loc   = thisJoinPointStaticPart.getSourceLocation(); 
			Instance   instance  = new Instance (clazz.getName(), clazz, obj);
			TestContext context  = 
				new TestContextImpl (clazz.getSimpleName(), instance, null, args, null, null,
					loc.getFileName(), loc.getLine());
			TestResult result  = 
				getParentTestExpressionFinder().findParentTypeInvarTestExpressionIfEmpty(
					invar.value(), clazz, context);
			if (result.isPassed() == false) {
				ContractEnforcer enf = getContractEnforcer();
				String msg = enf.makeFailureMessage (invar.value(), "Invar", result.getMessage(), context, result);
				enf.handleFailure(msg);
			}
			String testExpr = result.getMessage(); 
			testExpr  = 
				getDefaultTypeInvarTestExpressionMaker().makeDefaultTestExpressionIfEmpty(testExpr, context);
			// Capture "old" data (even though there are no old data...).
			context.setOldValuesMap (determineOldValues (testExpr, context));
			getContractEnforcer().invokeTest(testExpr, "Invar", invar.message(), context);
		}
	}
}
