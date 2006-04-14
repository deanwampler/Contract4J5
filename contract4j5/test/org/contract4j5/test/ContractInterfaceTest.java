package org.contract4j5.test;

import junit.framework.TestCase;

import org.contract4j5.ContractError;

/** 
 * Tests and demonstrates behavior of a class that implements a contract-defining
 * Interface, without any annotations in the class. The method tests are not 
 * inherited unless the annotations are used. No tests fail that should fail 
 * even though the contract is violated.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class ContractInterfaceTest extends TestCase {

	public static class ContractInterfaceSimpleImpl implements ContractInterface {
		private String name = null;
		public void setName (String s) { name = s; }
		public String getName () { return name; }
		public void m (String s) {}
		public boolean getB() {	return true; }
		public int getFlag() { return 0; }
	}
		
	private ContractInterfaceSimpleImpl impl = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		impl = new ContractInterfaceSimpleImpl();
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.setName(String)'
	 */
	public void testSetNameWithNull() {
		try {
			impl.setName(null);  // Breaks contract!
		} catch (ContractError e) {
			fail();
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.setName(String)'
	 */
	public void testSetNameWithNonNull() {
		try {
			impl.setName("ok");
		} catch (ContractError e) {
			fail();
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.getName()'
	 */
	public void testGetNameWithNull() {
		try {
			impl.getName(); // Breaks contract!
		} catch (ContractError e) {
			fail();
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.getName()'
	 */
	public void testGetNameWithNonNull() {
		try {
			impl.setName("ok");
			impl.getName();
		} catch (ContractError e) {
			fail();
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.m(String)'
	 */
	public void testMWithNullName() {
		try {
			impl.m("toss"); // Breaks contract because name never initialized!
		} catch (ContractError e) {
			fail();
		}
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceImpl.m(String)'
	 */
	public void testMWithEmptyName() {
		try {
			impl.setName("");
			impl.m("toss"); // Breaks contract because name is empty!
		} catch (ContractError e) {
			fail();
		}
	}

}
