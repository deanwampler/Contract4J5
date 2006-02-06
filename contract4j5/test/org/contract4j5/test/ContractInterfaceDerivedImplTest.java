package org.contract4j5.test;

import org.contract4j5.ContractError;

public class ContractInterfaceDerivedImplTest extends ContractInterfaceImplTest {
	private ContractInterfaceImpl obj2 = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		// Override the parent test's object.
		obj2 = new ContractInterfaceDerivedImpl(1);
		setObj(obj2);
	}

	/*
	 * Test method for 'org.contract4j5.test.ContractInterfaceDerivedImpl.ClassImplContractInterfaceDerivedImpl(int)'.
	 */
	public void testClassImplContractInterface1() {
		try {
			obj2 = new ContractInterfaceDerivedImpl(0);  // Fail class invariant
			fail();
		} catch (ContractError ce) {
			// Expected
		}
	}

	public void testClassImplContractInterface2() {
		try {
			obj2 = new ContractInterfaceDerivedImpl(100);  // Fail the c'tor precondition.
			fail();
		} catch (ContractError ce) {
			// Expected
		}
	}
	
	public void testClassImplContractInterface3() {
		try {
			obj2 = new ContractInterfaceDerivedImpl(1, 2); // Fail the c'tor postcondition
			fail();
		} catch (ContractError ce) {
			// Expected
		}
	}
	
	public void testClassImplContractInterface4() {
		try {
			obj2 = new ContractInterfaceDerivedImpl(1, 4); // Fail the getName postcondition
		} catch (ContractError ce) {
			fail();		// shouldn't have failed yet!!
		}
		try {
			obj2.getName();
			fail();
		} catch (ContractError ce) {
			// Expected	to now fail!
		}
	}
	
	public void testClassImplContractInterface5() {
		try {
			obj2 = new ContractInterfaceDerivedImpl(1, 5);  // Fail the "b" field invariant
			fail();
		} catch (ContractError ce) {
			// Expected
		}
	}
}
