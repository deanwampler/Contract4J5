package org.contract4j5.testexpression;

import org.contract4j5.TestContext;

/**
 * Helper class that implements the boilerplate method {@link #makeDefaultTestExpressionIfEmpty(String, String, TestContext)}
 * and also provides some convenient helper methods.
 * @author Dean Wampler
 */
abstract public class DefaultTestExpressionMakerHelper implements
		DefaultTestExpressionMaker {

	abstract public String makeDefaultTestExpression(TestContext context);

	/* (non-Javadoc)
	 * @see org.contract4j5.testexpression.DefaultTestExpressionMaker#makeDefaultTestExpressionIfEmpty(java.lang.String, java.lang.String, org.contract4j5.TestContext)
	 */
	public String makeDefaultTestExpressionIfEmpty (
			String testExpression, 
			TestContext context) {
		if (testExpression != null && testExpression.length() > 0) {
			return testExpression;
		}
		return makeDefaultTestExpression(context);
	}
	
	/* (non-Javadoc)
	 * @see org.contract4j5.testexpression.DefaultTestExpressionMaker#makeArgsNotNullExpression(org.contract4j5.TestContext)
	 */
	public String makeArgsNotNullExpression (TestContext context) {
		StringBuffer test = new StringBuffer();
		Object[] args = context.getMethodArgs();
		if (args != null || args.length > 0) {
			boolean first = true;
			for (int i=0; i<args.length; i++) {
				Class argClass = args[i] != null ? args[i].getClass() : null;
				if (isNotPrimitive (argClass)) {
					if (first) { 
						first = false; 
						//test.append("(");
					} else {
						test.append(" && ");
					}
					test.append("$args["+i+"]");
					test.append(" != null");
				}
			}
			if (!first) {  // Did we actually have any expressions?
				//test.append(")");
			}
		}
		return test.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.contract4j5.testexpression.DefaultTestExpressionMaker#isNotPrimitive(java.lang.Class)
	 */
	public boolean isNotPrimitive (Class clazz) {
		return (clazz == null || clazz.isPrimitive() == false) ?
				true: false;
	}
}
