package org.contract4j5.test;

import org.contract4j5.Contract;
import org.contract4j5.Invar;
import org.contract4j5.Post;
import org.contract4j5.Pre;

/**
 * Class subclassing {@link ContractInterfaceImpl}, which 
 * implements the interface w/ the "@Contract" annotation. 
 * This class does not change the annotations.
 * Note that the setName and getName methods below are commented out.
 * Jexl apparently can't resolve "$this.name" for objects of this class,
 * if name isn't an attribute. It doesn't seem to automatically convert to
 * getName(). However, this works for the parent class where the attribute exists.
 * Also, the two commented methods below cause an infinite recursion and stack 
 * overflow, even though the aspects explicitly try to prevent this!
 * @author Dean Wampler
 */
@Contract
@Invar
public class ContractInterfaceDerivedImpl extends ContractInterfaceImpl {
//	public void setName(String name) {
//		super.setName(name);
//	}
//	public String getName() {
//		return getName();
//	}
	public boolean getB() {
		return super.getB();
	}
	public int getFlag() { 
		return super.getFlag();
	}
	public void m (String s) {
		super.m(s);
	}
	
	public ContractInterfaceDerivedImpl (int flag) { 
		super(flag); 
		setName("ContractInterfaceDerivedImpl obj2");
	}
	
	public ContractInterfaceDerivedImpl (int flag, int whichFailure) { 
		super(flag, whichFailure); 
	}
}
