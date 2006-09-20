package org.contract4j5;

/**
 * The special case of ContractError that is thrown when a test itself is 
 * invalid, <i>e.g.,</i> because the test expression was empty and no default
 * value could be inferred and the option to allow empty tests was false. This
 * error is also thrown if the expression interpreter fails to parse or 
 * evaluate the test expression.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class TestSpecificationError extends ContractError {
	private static final long serialVersionUID = -2721715704273663735L;

	public TestSpecificationError () {
		super();
	}
	public TestSpecificationError (String s) {
		super(s);
	}
	public TestSpecificationError (String s, Throwable t) {
		super(s, t);
	}
}
