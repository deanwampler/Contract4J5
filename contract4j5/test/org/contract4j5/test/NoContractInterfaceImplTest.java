package org.contract4j5.test;

import junit.framework.TestCase;

import org.contract4j5.ContractError;

public class NoContractInterfaceImplTest extends TestCase {
	private NoContractInterfaceImpl obj = null;
	public NoContractInterfaceImpl getObj() {
		return obj;
	}
	public void setObj(NoContractInterfaceImpl obj) {
		this.obj = obj;
	}

	protected void setUp() throws Exception {
		super.setUp();
		ManualSetup.wireC4J();
		obj = new NoContractInterfaceImpl(1);
	}

	/*
	 * Test method for 'org.contract4j5.test.NoContractInterfaceImpl.getName()'.
	 * Should not fail the postcondition test and the class and field ("b") invariant tests.
	 */
	public void testGetName() {
		try {
			String n = obj.getName();
			assertNull (n);
			doTestClassAndFieldInvariants();
		} catch (ContractError ce) {
			fail();
		}		
	}

	/*
	 * Test method for 'org.contract4j5.test.NoContractInterfaceImpl.setName(String)'
	 * Should not fail the precondition test and the class and field ("b") invariant tests.
	 */
	public void testSetName() {
		try {
			obj.setName(null);
			String n = obj.getName();
			assertNull (n);
			doTestClassAndFieldInvariants();
		} catch (ContractError ce) {
			fail();
		}		
	}

	/*
	 * Test method for 'org.contract4j5.test.NoContractInterfaceImpl.getFlag()'.
	 * Should not fail the class and field ("b") invariant tests.
	 */
	public void testGetFlag() {
		try {
			int f = obj.getFlag();
			assertEquals (1, f);
			doTestClassAndFieldInvariants();
		} catch (ContractError ce) {
			fail();
		}		
	}

	/*
	 * Test method for 'org.contract4j5.test.NoContractInterfaceImpl.m()'.
	 * Should not fail the class invariant test nor the method invariant test.
	 */
	public void testM() {
		try {
			obj.m("foo");
			String n = obj.getName();
			assertNotNull (n, n);
			assertEquals  ("", n);
			doTestClassAndFieldInvariants();
		} catch (ContractError ce) {
			fail();
		}		
	}

	/*
	 * Test method for 'org.contract4j5.test.NoContractInterfaceImpl.ClassImplNoContractInterface(int)'.
	 * Should not fail any tests.
	 */
	public void testClassImplNoContractInterface() {
		try {
			obj = new NoContractInterfaceImpl(100);
			String n = obj.getName();
			assertNull (n);
			int f = obj.getFlag();
			assertEquals (100, f);
			doTestClassAndFieldInvariants();
		} catch (ContractError ce) {
			fail();
		}
	}

	protected void doTestClassAndFieldInvariants () {
		int f = obj.getFlag();
		assertTrue (f > 0);
		boolean b = obj.getB();
		assertFalse (b);
		
	}
}
