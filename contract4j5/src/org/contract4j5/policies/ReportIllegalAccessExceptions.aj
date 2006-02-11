package org.contract4j5.policies;

import org.contract4j5.aspects.Contract4J;
import org.contract4j5.util.reporter.Reporter;
import org.contract4j5.util.reporter.Severity;

/**
 * Report when {@link IllegalAccessException}'s are caught.
 * @author Dean Wampler
 * TODO Rather than using the object that threw the exception for the context,
 * which is pretty lowlevel
 */
public aspect ReportIllegalAccessExceptions {
	before (IllegalAccessException iae) :
		handler (IllegalAccessException) && 
		within (org.contract4j..*) && 
		args (iae) {
		Reporter reporter = Contract4J.getReporter();
		if (reporter != null) {
			reporter.report(Severity.ERROR, Contract4J.class, iae.toString());
		}
	}
}
