package org.contract4j5.testexpression;

import org.contract4j5.Instance;
import org.contract4j5.TestContext;

/**
 * Make default test expressions for field invariant tests.
 * @author Dean Wampler
 */
public class DefaultFieldInvarTestExpressionMaker extends DefaultTestExpressionMakerHelper {
	/**
	 * Require the "target", which represents the field value, to be non-null.
	 * @see org.contract4j5.testexpression.DefaultTestExpressionMaker#makeDefaultTestExpression(org.contract4j5.TestContext)
	 */
	public String makeDefaultTestExpression(TestContext context) {
		Instance i = context.getField();
		Class clazz = i != null ? i.getClazz() : null;
		return isNotPrimitive(clazz) ?
				"$target != null" : "";
	}
}
