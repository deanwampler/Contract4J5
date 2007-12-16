package org.contract4j5.reporter.test;

import java.util.ArrayList;
import java.util.List;

import org.contract4j5.reporter.CompositeReporter;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.Severity;
import org.contract4j5.reporter.WriterReporter;

import junit.framework.TestCase;

public class CompositeReporterTest extends TestCase {
	public void testCompositeReporterIsEmpty() {
		CompositeReporter reporter = new CompositeReporter();
		assertTrue(reporter.getReporters().isEmpty());
	}

	public void testAddedReportersAreReturned() {
		CompositeReporter reporter = new CompositeReporter();
		reporter.setReporters(makeReporterList());
		List<Reporter> list = reporter.getReporters();
		assertEquals(2, list.size());
	}

	public void testCompositeReporterThresholdIsSameAsSpecifiedInConstructor() {
		CompositeReporter reporter = new CompositeReporter(Severity.FATAL);
		assertEquals(Severity.FATAL, reporter.getThreshold());
	}

	public void testCompositeReporterThresholdIsSameAsSpecifiedInConstructorIndependentOfEnclosedReporters() {
		CompositeReporter reporter = new CompositeReporter(Severity.FATAL, makeReporterList());
		assertEquals(Severity.FATAL, reporter.getThreshold());
	}
	
	public void testThresholdReturnedMatchesTheLowestThresholdOfTheEnclosedReporters() {
		List<Reporter> reporters = makeReporterList();
		CompositeReporter reporter = new CompositeReporter(reporters);
		assertEquals(Severity.WARN, reporter.getThreshold());
	}

	private List<Reporter> makeReporterList() {
		List<Reporter> reporters = new ArrayList<Reporter>();
		reporters.add(new WriterReporter(Severity.WARN));
		reporters.add(new WriterReporter(Severity.ERROR));
		return reporters;
	}
}
