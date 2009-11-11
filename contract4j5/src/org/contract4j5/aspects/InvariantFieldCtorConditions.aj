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
import java.util.HashMap;
import java.util.Map.Entry;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextCache;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.instance.Instance;
import org.contract4j5.testexpression.DefaultFieldInvarTestExpressionMaker;
import org.contract4j5.testexpression.DefaultTestExpressionMaker;

/** 
 * Test for field invariants in a constructor context. There is no pointcut 
 * defined for the before case since the state should only be checked after 
 * the object is initialized. After the c'tor completes, all field invariants 
 * are tested. The rationale is that if a field invariant is defined, then 
 * it must hold after construction. However, this forces c'tors to always 
 * initialize all such fields, rather than allow lazy evaluation later. 
 * Nevertheless, that isn't a practical problem because a lazy evaluation 
 * will be performed inside an accessor, so in this case the invariant 
 * should be written as a test on the accessor.
 * @note The advice doesn't need to use a {@lnk ParentTestExpressionFinder}
 * to locate a parent's test expression because fields aren't overridden by
 * derived classes!
 * @note To make it easy to wire an <i>instance</i> field for the {@link 
 * DefaultTestExpressionMaker}, we declare this aspect with the default singleton
 * instantiation, then use a nested percflow aspect for the real advising. This
 * gives us wiring semantics that are consistent with the other C4J aspects.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public aspect InvariantFieldCtorConditions {
	private DefaultTestExpressionMaker defaultFieldInvarTestExpressionMaker;

	public DefaultTestExpressionMaker getDefaultFieldInvarTestExpressionMaker() { 
		if (defaultFieldInvarTestExpressionMaker == null)
			defaultFieldInvarTestExpressionMaker = new DefaultFieldInvarTestExpressionMaker();
		return defaultFieldInvarTestExpressionMaker; 
	}

	public void setDefaultFieldInvarTestExpressionMaker (DefaultTestExpressionMaker maker) { 
		defaultFieldInvarTestExpressionMaker = maker; 
	}
	
	public static aspect InvariantFieldCtorConditionsPerCtor 
		extends AbstractConditions percflow(invarFieldCtorCall (Contract, Invar, Object)) {

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
		// keep the last assignment, not any potentially-invalid intermediate
		// assignments.
		private HashMap<String, ListElem> listOfAnnosFound = null;
		
		/**
		 * The enclosing scope of a ctor call. 
		 * @note We prevent recursion into the aspect itself.
		 */
		pointcut invarFieldCtorCall (Contract contract, Invar invar, Object obj) : 
			invarCommon(contract, invar) && !within(InvariantFieldCtorConditions) &&
			execution (*.new(..)) && target (obj);
				
		/**
		 * Field invariant pointcut within a constructor context. We match on 
		 * the "cflowbelow" of the constructor call and not "within" even though
		 * it's less runtime efficient. Otherwise, set join points nested in 
		 * method calls within the c'tor will be ignored!
		 * @note We prevent recursion into the aspect itself.
		 */
		pointcut invarFieldInCtor (Contract contract, Invar invar, Object obj, Object field) :
			!within(InvariantFieldCtorConditions) &&
			cflowbelow (invarFieldCtorCall (contract, invar, obj)) && 
			set (@Invar * *.*) && args (field);
			
		/**
		 * Observe any annotated field sets within the c'tor and record the 
		 * invariant specification.
		 */
		after (Contract contract, Invar invar, Object obj, Object newFieldValue) returning : 
			invarFieldInCtor (contract, invar, obj, newFieldValue) {
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
		
		static protected class Bucket {
			public String testExpr;
			public TestContext context;
			public Bucket(String expr, TestContext context) {
				this.testExpr = expr;
				this.context = context;
			}
		}
		
		/**
		 * After the c'tor completes, if there were any annotated fields set, 
		 * then test them.
		 */
		after(Contract contract, Invar invar, Object obj) returning : invarFieldCtorCall(contract, invar, obj) {
			if (listOfAnnosFound == null) {
				return;
			}
			for (Entry<String, ListElem> entry: listOfAnnosFound.entrySet()) {
				ListElem elem = entry.getValue();
				Bucket bucket = getOrMakeTestContextAndTestExpr(thisJoinPointStaticPart, obj, entry.getKey(), elem);
				TestContext context = bucket.context;
				String testExpr = bucket.testExpr;
				getContractEnforcer().invokeTest(testExpr, "Invar", elem.invar.message(), context);
			}
		}
		
		protected Bucket getOrMakeTestContextAndTestExpr(
				JoinPoint.StaticPart thisJoinPointStaticPart, 
				Object obj, String elemKey, ListElem elem) {
			TestContext context   = null;
			String testExpr       = "";
			SourceLocation loc    = thisJoinPointStaticPart.getSourceLocation();
			String fileName = loc.getFileName();
			int    lineNum  = loc.getLine();
			TestContextCache.Key key = new TestContextCache.Key("Invar", fileName, lineNum);
			TestContextCache.Entry entry = contextCache.get(key);
			if (context != null) {
				context = entry.testContext;
				testExpr = entry.testExpression;
				Instance fieldInstance = new Instance(entry.fieldName, entry.fieldType, elem.field);
				context.setField(fieldInstance);
			} else {
				Instance instance = new Instance(obj.getClass().getName(), obj.getClass(), obj);
				context = new TestContextImpl (elemKey, elemKey, instance, 
								elem.field, null, null, null, fileName, lineNum);
				testExpr = InvariantFieldCtorConditions.aspectOf().getDefaultFieldInvarTestExpressionMaker()
					.makeDefaultTestExpressionIfEmpty(elem.invar.value(), context);
				contextCache.put(key, new TestContextCache.Entry(context, testExpr, null, null, null, null));
			}
			return new Bucket(testExpr, context);
		}
	}
}
	