package org.contract4j5.test;

import org.contract4j5.Post;

/**
 * Class subclassing the {@link NoContractInterfaceImpl} class the implements
 * an interface w/out the "@Contract" annotation. 
 * @author Dean Wampler
 */
public class NoContractInterfaceDerivedImpl extends NoContractInterfaceImpl {
	public String getName() {
		return super.getName();
	}
	public void setName(String name) {
		super.setName(name);
	}
	
	public boolean getB() {
		return super.getB();
	}
	
	public int getFlag() { return super.getFlag(); }
	
	public void m(String s) {
		super.m(s);
	}
	
	@Post("")
	public NoContractInterfaceDerivedImpl (int flag) { 
		super(flag);
	}
}
