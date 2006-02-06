package org.contract4j5.interpreter;

/**
 * Value object for a test result, not only pass or fail, but the cause of a failure.
 */
public class TestResult {
	boolean passed = true;
	/**
	 * @return true if the test passed.
	 */
	public boolean isPassed() {
		return passed;
	}
	public void setPassed(boolean passed) {
		this.passed = passed;
	}
	
	String  message = "";
	/**
	 * @return Returns a corresponding message, which will usually be "" if the 
	 * test passed, except in documented circumstances. However, even for a failed
	 * test, it may still be "". It is never null.
	 */
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	Throwable failureCause = null;
	/**
	 * @return Returns the Throwable associated with a failure or null
	 * if the test passed or it failed, but there was no exception thrown.
	 */
	public Throwable getFailureCause() {
		return failureCause;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(256);
		sb.append("TestResult: passed = ").append(isPassed());
		sb.append(", message = \"").append(getMessage()).append("\"");
		if (getFailureCause() !=  null) {
			sb.append(", failure cause = ").append(getFailureCause());
			sb.append("\nStack Trace: [");
			StackTraceElement[] elems = getFailureCause().getStackTrace();
			for (StackTraceElement elem: elems) {
				sb.append("\n  ");
				sb.append(elem.toString());
			}
			sb.append("\n]\n");
		}
		return sb.toString();
	}
	
	public boolean equals (Object o) {
		if (o == null || !(o instanceof TestResult)) {
			return false;
		}
		TestResult tr = (TestResult) o;
		return (isPassed() == tr.isPassed() &&
				getMessage().equals(tr.getMessage()));
	}
	
	public TestResult () {
		this.passed       = true;
		this.message      = "";
		this.failureCause = null;
	}
	
	public TestResult (boolean passed) {
		this.passed       = passed;
		this.message      = "";
		this.failureCause = null;
	}
	
	public TestResult (boolean passed, String message) {
		this.passed       = passed;
		this.message      = message;
		this.failureCause = null;
	}
	
	public TestResult (boolean passed, String message, Throwable failureCause) {
		this.passed       = passed;
		this.message      = message;
		this.failureCause = failureCause;
	}
}