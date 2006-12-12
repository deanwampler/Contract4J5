package org.contract4j5.configurator.ensurer;

import org.contract4j5.controller.Contract4J;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.Severity;

import junit.framework.TestCase;

public class ReporterDependencyInitializerTest extends TestCase {
	private StubReporter stubReporter;
	static class StubReporter implements Reporter {
		public Severity getThreshold() { return null; }
		StringBuffer buff = new StringBuffer();
		public void report(Severity level, Class<?> clazz, String message) {
			buff.append(message+"\t");
		}
		public void setThreshold(Severity level) {}

		public void setThresholdUsingString(String level)
				throws IllegalArgumentException {}
	}
	
	static class ObjectWithUninitializedReporter {
		private Reporter reporter;

		public ObjectWithUninitializedReporter (String msg) {
			reporter.report(Severity.ERROR, this.getClass(), msg);
		}
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		stubReporter = new StubReporter();
		Contract4J.getInstance().setReporter(stubReporter);
	}
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		Contract4J.getInstance().setReporter(null);  // unset for next test(?)
	}
	
	public void testObjectWithUninitializedReporterUsesContract4JReporter() {
		new ObjectWithUninitializedReporter("hello");
		assertEquals("hello\t", stubReporter.buff.toString());
	}
}
