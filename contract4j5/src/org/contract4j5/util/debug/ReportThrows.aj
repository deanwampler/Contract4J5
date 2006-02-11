package org.contract4j5.util.debug;

import org.contract4j5.util.reporter.Reporter;
import org.contract4j5.util.reporter.Severity;
import org.contract4j5.util.reporter.WriterReporter;

public aspect ReportThrows {

	after(Object o) throwing(Throwable th): 
		call (* org.contract4j..*.*(..)) && 
		!within (ReportThrows) &&
		this(o) {
		Class clazz = o != null ? o.getClass() : null;
		getReporter().report(Severity.ERROR, clazz, th.toString());
		th.printStackTrace();
	}
	
	private Reporter reporter;
	public Reporter getReporter() {
		if (this.reporter == null) {
			this.reporter = new WriterReporter();
		}
		return reporter;
	}
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
}
