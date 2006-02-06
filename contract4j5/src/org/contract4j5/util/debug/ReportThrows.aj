package org.contract4j.util.debug;

import org.contract4j.util.reporter.Reporter;
import org.contract4j.util.reporter.Severity;
import org.contract4j.util.reporter.WriterReporter;

public aspect ReportThrows {

	after(Object o) throwing(Throwable th): 
		call (* org.contract4j..*.*(..)) && 
		!within (ReportThrows) &&
		this(o) {
		Class clazz = o != null ? o.getClass() : null;
		getReporter().report(Severity.Error, clazz, th.toString());
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
