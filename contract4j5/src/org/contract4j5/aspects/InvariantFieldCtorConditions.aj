package org.contract4j5.aspects;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.contract4j5.Instance;
import org.contract4j5.Invar;
import org.contract4j5.TestContext;
import org.contract4j5.TestContextImpl;
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
	
	public static aspect InvariantFieldCtorConditionsPerCtor extends AbstractConditions percflow(invarFieldCtorCall (ContractMarker)) {

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
		 * Field invariant pointcut within a constructor context. We match on 
		 * the "cflowbelow" of the constructor call and not "within" even though
		 * it's less runtime efficient. Otherwise, set join points nested in 
		 * method calls within the c'tor will be ignored!
		 * @note We prevent recursion into the aspect itself.
		 */
		pointcut invarFieldInCtor (Invar invar, ContractMarker obj, Object field) :
			invarCommon() && !within(InvariantFieldCtorConditions) &&
			cflowbelow (invarFieldCtorCall (ContractMarker)) && 
			set (@Invar * ContractMarker+.*) &&
			@annotation (invar) && target (obj) && args (field);
			
		/**
		 * Observe any annotated field sets within the c'tor and record the 
		 * invariant specification.
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
		 * After the c'tor completes, if there were any annotated fields set, 
		 * then test them.
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
				String testExpr = InvariantFieldCtorConditions.aspectOf().getDefaultFieldInvarTestExpressionMaker()
					.makeDefaultTestExpressionIfEmpty(elem.invar.value(), context);
				getContractEnforcer().invokeTest(testExpr, "Invar", elem.invar.message(), context);
			}
		}
	}
}
	