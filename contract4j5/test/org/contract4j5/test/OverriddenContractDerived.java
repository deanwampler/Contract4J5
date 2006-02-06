/**
 * 
 */
package org.contract4j5.test;

import org.contract4j5.Contract;
import org.contract4j5.Invar;
import org.contract4j5.Post;
import org.contract4j5.Pre;

@Contract
@Invar
public class OverriddenContractDerived extends OverriddenContractBase {
	public String getField1() {
		return super.getField1();
	}
	public void setField1(String field1) {
		super.setField1(field1);
	}

	// Covariant postcondition (narrowed)
	@Post("$this.field.equals(\"foo\")")
	public String getField3() {
		return super.getField3();
	}
	// Contravariant precondition (widened)
	@Pre ("$args[0] != null && $args[0].length() >= 1")
	public void setField3(String field3) {
		super.setField3(field3);
	}
	
	@Invar
	public void doNothing() {
		super.doNothing();
	}

	@Invar
	// Contravariant precondition (widened)
	@Pre  ("$args[0] != null")
	// Covariant postcondition (narrowed)
	@Post ("$this.postFlag > 1")
	public OverriddenContractDerived (String f1, String f2, String f3) {
		super(f1, f2, f3);
		setPostFlag (2);
	}
}