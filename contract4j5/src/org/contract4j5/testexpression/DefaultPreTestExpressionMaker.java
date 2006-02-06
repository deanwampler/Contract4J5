package org.contract4j5.testexpression;

import org.contract4j5.TestContext;

/**
 * Make default test expressions for precondition tests.
 * @author Dean Wampler
 */
public class DefaultPreTestExpressionMaker extends DefaultTestExpressionMakerHelper {
	/**
	 * Require each non-primitive argument to the method to be non-null.
	 * @see org.contract4j5.testexpression.DefaultTestExpressionMaker#makeDefaultTestExpression(org.contract4j5.TestContext)
	 */
	public String makeDefaultTestExpression(TestContext context) {
		return makeArgsNotNullExpression (context);
	}
}
