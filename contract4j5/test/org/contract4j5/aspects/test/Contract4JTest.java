package org.contract4j5.aspects.test;

import junit.framework.TestCase;
import org.contract4j5.aspects.*;

public class Contract4JTest extends TestCase {
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		// Leave test with all enabled statically.
		Contract4J.setEnabled(Contract4J.TestType.Pre,   true);
		Contract4J.setEnabled(Contract4J.TestType.Post,  true);
		Contract4J.setEnabled(Contract4J.TestType.Invar, true);
	}
	
	public void testEnableAll() {
		doSetCheckOldStyle(true, true, true);
		doSetCheckNewStyle(true, true, true);
	}
	
	public void testDisableAll() {
		doSetCheckOldStyle(false, false, false);
		doSetCheckNewStyle(false, false, false);
	}
	
	public void testEnablePre() {
		doSetCheckOldStyle(true, false, false);
		doSetCheckNewStyle(true, false, false);
	}
	
	public void testEnablePost() {
		doSetCheckOldStyle(false, true, false);
		doSetCheckNewStyle(false, true, false);
	}
	
	public void testEnableInvar() {
		doSetCheckOldStyle(false, false, true);
		doSetCheckNewStyle(false, false, true);
	}
	
	private static void doSetCheckOldStyle(boolean pre, boolean post, boolean invar) {
		Contract4J.setEnabled(Contract4J.TestType.Pre,   pre);
		Contract4J.setEnabled(Contract4J.TestType.Post,  post);
		Contract4J.setEnabled(Contract4J.TestType.Invar, invar);
		assertEquals(pre, Contract4J.isEnabled(Contract4J.TestType.Pre));
		assertEquals(post, Contract4J.isEnabled(Contract4J.TestType.Post));
		assertEquals(invar, Contract4J.isEnabled(Contract4J.TestType.Invar));
	}
	private static void doSetCheckNewStyle(boolean pre, boolean post, boolean invar) {
		Contract4J.setPreTestsEnabled(pre);
		Contract4J.setPostTestsEnabled(post);
		Contract4J.setInvarTestsEnabled(invar);
		assertEquals(pre, Contract4J.isPreTestsEnabled());
		assertEquals(post, Contract4J.isPostTestsEnabled());
		assertEquals(invar, Contract4J.isInvarTestsEnabled());
	}
}
