package org.contract4j5.reporter.test;

import org.contract4j5.reporter.NullReporter;
import org.contract4j5.reporter.Severity;

import junit.framework.TestCase;

public class NullReporterTest extends TestCase {
	public void testNullReporterIsOff() {
		assertEquals(Severity.OFF, new NullReporter().getThreshold());
	}
	
	public void testSetThresholdDoesNothing() {
		NullReporter reporter = new NullReporter();
		reporter.setThreshold(Severity.FATAL);
		assertEquals(Severity.OFF, reporter.getThreshold());
	}

	public void testSetThresholdWithStringDoesNothing() {
		NullReporter reporter = new NullReporter();
		reporter.setThresholdUsingString("WARN");
		assertEquals(Severity.OFF, reporter.getThreshold());
	}

	public void testReportDoesNothing() {
		NullReporter reporter = new NullReporter();
		reporter.report(Severity.ERROR, String.class, "ignored message");
		// Nothing can be tested, but at least we can cover method with a test!
	}
}
