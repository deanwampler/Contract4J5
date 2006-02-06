package org.contract4j5.test;

import org.contract4j5.Invar;
import org.contract4j5.Post;
import org.contract4j5.Pre;

/**
 * Class implementing the interface w/out the "@Contract" annotation. 
 * Note that we can't declare field invariant tests nor constructor tests
 * in interfaces; there is no where to put them. The best you can do is 
 * tests on accessor methods and class invariants.
 * @author Dean Wampler
 */
public class NoContractInterfaceImpl implements NoContractInterface {
	int flag = 0;
	private String name = null;
	@Invar("b == true")
	private boolean b = false;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean getB() {
		return b;
	}
	
	public int getFlag() { return flag; }
	
	public void m(String s) {
		this.name = "";
	}
	
	@Pre("$args[0] != 100")
	@Post("!$this.name.equals(\"bad\")")
	public NoContractInterfaceImpl (int flag) { this.flag = flag; }
}
