package org.contract4j5.testexpression;

import org.contract4j5.TestContext;

/**
 * A default test expression maker that returns a simple string, specified for the maker instance.
 * The string defaults to "".
 * @author Dean Wampler
 */
public class SimpleStringDefaultTestExpressionMaker extends DefaultTestExpressionMakerHelper {
	private String expression = "";
	/**
	 * @return the fixed expression string, which is never null.
	 */
	public String getExpression() {
		return expression;
	}
	/**
	 * @param expression to return for all requests for the default test expression. If null
	 * is specified, "" is used instead.
	 */
	public void setExpression(String expression) {
		this.expression = expression;
		if (this.expression == null) {
			this.expression = "";
		}
	}
	/**
	 * No default is defined; return an empty (not null) string.
	 * @see org.contract4j5.testexpression.DefaultTestExpressionMaker#makeDefaultTestExpression(org.contract4j5.TestContext)
	 */
	public String makeDefaultTestExpression(TestContext context) {
		return getExpression();
	}
	
	/**
	 * Use "" as the test string.
	 */
	public SimpleStringDefaultTestExpressionMaker() {}

	/**
	 * @param expression to return for all default expression requests. If null
	 * is specified, "" is used instead.
	 */
	public SimpleStringDefaultTestExpressionMaker(String expression) {
		setExpression(expression);
	}
	
}
