/*
 * Copyright 2005 2006 Dean Wampler. All rights reserved.
 * http://www.aspectprogramming.com
 *
 * Licensed under the Eclipse Public License - v 1.0; you may not use this
 * software except in compliance with the License. You may obtain a copy of the 
 * License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * A copy is also included with this distribution. See the "LICENSE" file.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */

package org.contract4j5.interpreter;

import org.contract4j5.errors.TestSpecificationError;

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
	 * @return The Throwable associated with a failure or null
	 * if the test passed or it failed, but there was no exception thrown.
	 */
	public Throwable getFailureCause() {
		return failureCause;
	}
	
	/**
	 * @return The actual failure, which will equal the result of 
	 * {@link getFailureCause()}, or a throwable that it may contain or null.
	 */
	public Throwable getActualFailureCause() {
		Throwable failureCause = getFailureCause();
		if (isFailureCauseATestSpecificationFailure()) {
			Throwable nestedCause = failureCause.getCause();
			return nestedCause != null ? nestedCause : failureCause;
		}		
		return failureCause;
	}

	public boolean isFailureCauseATestSpecificationFailure() {
		return failureCause != null && 
		(failureCause.getClass() == TestSpecificationError.class);
	}
	
	public String getFailureCauseMessage() {
		StringBuffer sb = new StringBuffer(256);
		Throwable failureCause = getFailureCause();
		if (isFailureCauseATestSpecificationFailure()) {
			sb.append("Test specification error (invalid, parse failure, ...), actual failure cause = ");
			Throwable actualFailureCause = failureCause.getCause(); 
			sb.append(actualFailureCause !=  null ? actualFailureCause : "<null>");
		} else {
			sb.append("failure cause = ").append(failureCause);
		}
		return sb.toString();
	}
	
	public String getFailureCauseStackTrace() {
		Throwable failureCause = getFailureCause();
		if (isFailureCauseATestSpecificationFailure()) {
			Throwable actualFailureCause = getActualFailureCause();
			return getStackTraceString(actualFailureCause != null ? actualFailureCause : failureCause);
		}
		return getStackTraceString(failureCause);
	}
	
	protected String getStackTraceString(Throwable throwable) {
		if (throwable == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer(256);
		sb.append("\nStack Trace: [");
		StackTraceElement[] elems = getFailureCause().getStackTrace();
		for (StackTraceElement elem: elems) {
			sb.append("\n  ");
			sb.append(elem.toString());
		}
		sb.append("\n]");
		return sb.toString();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(256);
		sb.append("TestResult: passed = ").append(isPassed());
		sb.append(", message = \"").append(getMessage()).append("\"");
		sb.append(getFailureCauseMessage());
		if (failureCause !=  null) {
			sb.append(", failure cause = ").append(getFailureCause());
			sb.append(getFailureCauseStackTrace());
			sb.append("\n");
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