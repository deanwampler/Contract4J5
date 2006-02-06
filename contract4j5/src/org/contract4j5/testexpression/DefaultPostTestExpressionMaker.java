package org.contract4j5.testexpression;

import org.contract4j5.TestContext;

/**
 * Make default test expressions for postcondition tests for methods that don't return void.
 * @author Dean Wampler
 */
public class DefaultPostTestExpressionMaker extends DefaultTestExpressionMakerHelper {
	/**
	 * Require the return value, if not a primitive, to be non-null.
	 * @see org.contract4j5.testexpression.DefaultTestExpressionMaker#makeDefaultTestExpression(org.contract4j5.TestContext)
	 */
	public String makeDefaultTestExpression(TestContext context) {
		Object r = context.getMethodResult();
		Class clazz = r != null ? r.getClass() : null;
		return isNotPrimitive(clazz) ?
				"$return != null" : "";
	}
}
