package org.contract4j5.configurator.ensurer;

import org.contract4j5.controller.Contract4J;
import org.contract4j5.reporter.Reporter;

/**
 * Solves the problem of initializing a dependent throughout the
 * application where the same object should be used. This aspect
 * watches for reads of a {@link Reporter} instance and either
 * returns it or if null, returns the global object.
 */
public aspect ReporterDependencyInitializer {
	
	Reporter around() : get(Reporter org.contract4j5..*.*) &&
		!cflow(call(Reporter Contract4J.getReporter())) {
		Reporter reporter = proceed();
		return reporter == null ? Contract4J.getInstance().getReporter() : reporter;
	}

}
