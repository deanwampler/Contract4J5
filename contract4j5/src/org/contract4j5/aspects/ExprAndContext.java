package org.contract4j5.aspects;

import org.contract4j5.TestContext;

/**
 * A value object used as a function return value in several 
 * of the aspects' support methods.
 */
public class ExprAndContext {
	public String      testExpr = "";
	public TestContext context  = null;

	public ExprAndContext (String testExpr, TestContext context) {
		this.testExpr = testExpr;
		this.context  = context;
	}
}
