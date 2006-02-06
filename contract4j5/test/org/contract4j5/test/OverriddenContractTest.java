package org.contract4j5.test;

import junit.framework.TestCase;

import org.contract4j5.ContractError;

/**
 * Tests behavior when a base class' contract is overridden by a 
 * derived class. 
 * @note Currently, there is no enforcement of the DbC rules
 * for contract changes under inheritance, which are:
 * <table>
 * <tr><th>Condition</th><th>Allowed Variations</th></tr>
 * <tr><td>Invariant</td><td>No variations allowed</td></tr>
 * <tr><td>Precondition</td><td><i>Contravariant</i>: you can widen them</td></tr>
 * <tr><td>Postcondition</td><td><i>Covariant</i>: you can narrow them</td></tr>
 * </table>
 * Support is planned....
 * @author Dean Wampler
 */
public class OverriddenContractTest extends TestCase {
	protected OverriddenContractBase bases[] = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		ManualSetup.wireC4J();
		bases = new OverriddenContractBase[2];
		try {
			bases[0] = new OverriddenContractBase    ("b1", "b2", "b3");
		} catch (ContractError ce) {
			fail(ce.toString());
		}
		try {
			bases[1] = new OverriddenContractDerived ("d1", "d2", "d3");
		} catch (ContractError ce) {
			fail(ce.toString());
		}
	}

	public void testTypeInvar1() {
		for (int i=0; i<bases.length; i++) {
			try {
				bases[i].setField1(null);
				fail("i="+i);
			} catch (ContractError ce) {
			}
		}
	}
	public void testTypeInvar2() {
		for (int i=0; i<bases.length; i++) {
			try {
				bases[i].setField1("foo");
			} catch (ContractError ce) {
				fail("i="+i);
			}
		}
	}

	public void testFieldInvar() {
		for (int i=0; i<bases.length; i++) {
			try {
				bases[i].setField2(null);
				fail("i="+i);
			} catch (ContractError ce) {
			}
			try {
				bases[i].setField2("foo");
			} catch (ContractError ce) {
				fail("i="+i);
			}
		}
	}

	public void testMethodPrePost() {
		for (int i=0; i<bases.length; i++) {
			try {
				bases[i].setField3(null); // fail pre
				fail("i="+i);
			} catch (ContractError ce) {
			}
			try {
				bases[i].setField3("f"); // also fail pre
				fail("i="+i);
			} catch (ContractError ce) {
			}
			try {
				bases[i].setField3("foobar"); // fail post
				bases[i].getField3();
				fail("i="+i);
			} catch (ContractError ce) {
			}
			try {
				bases[i].setField3("foo");	// pass
			} catch (ContractError ce) {
				fail("i="+i);
			}
			try {
				bases[i].setField3("bar");	// pass
			} catch (ContractError ce) {
				fail("i="+i);
			}
		}
	}
}
