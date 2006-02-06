package org.contract4j5.util.reporter;


/**
 * Uses the "Null Object Pattern" to define reporter that does nothing. 
 * This object is primarily useful for eliminating the need for 
 * clients to always test for null.
 * @author Dean Wampler
 */
public class NullReporter implements Reporter {

	public void report(Severity level, Class clazz, String message) {}
	public Severity getThreshold() {
		return Severity.OFF;
	}
	public void setThreshold(Severity level) {}
}
