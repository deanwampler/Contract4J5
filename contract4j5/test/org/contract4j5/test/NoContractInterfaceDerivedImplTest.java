package org.contract4j5.test;

import org.contract4j5.ContractError;

public class NoContractInterfaceDerivedImplTest extends NoContractInterfaceImplTest {
	NoContractInterfaceDerivedImpl obj2 = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		// Override the parent test's object.
		obj2 = new NoContractInterfaceDerivedImpl(1);
		setObj(obj2);
	}
	/*
	 * Test method for 'org.contract4j5.test.NoContractInterfaceDerivedImpl.NoContractInterfaceDerivedImpl(int)'.
	 * Should not fail any tests.
	 */
	public void testClassImplNoContractInterface() {
		try {
			obj2 = new NoContractInterfaceDerivedImpl(100);
			String n = obj2.getName();
			assertNull (n);
			int f = obj2.getFlag();
			assertEquals (100, f);
			doTestClassAndFieldInvariants();
		} catch (ContractError ce) {
			fail();
		}
	}
}
