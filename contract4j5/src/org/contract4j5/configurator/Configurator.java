package org.contract4j5.configurator;

import org.contract4j5.util.reporter.Reporter;

public interface Configurator {
	void configure();
	
	/**
	 * Set the reporter used by default. Implementers of this method should
	 * override any reporters that have been set individually on other "beans"!
	 * @param reporter
	 */
	void     setReporter(Reporter r);
	Reporter getReporter();
}
