package org.contract4j5.util.reporter;

import java.util.ArrayList;
import java.util.List;

/**
 * A reporter that encapsulates several, permitting output to multiple "sinks".
 * To add or remove reporters from the list, use {@link #getReporters()} to retrieve
 * the list and use normal list operations.
 * @author Dean Wampler
 */
public class CompositeReporter extends ReporterHelper {
	private List<Reporter> reporters = new ArrayList<Reporter>();
	
	/**
	 * @return the list of reporters.
	 */
	public List<Reporter> getReporters() {
		return reporters;
	}

	/**
	 * @param reporters The reporters to use. The minimum threshold
	 * for the composite will be reset to the lowest threshold in the
	 * list.
	 */
	public void setReporters(List<Reporter> reporters) {
		this.reporters = reporters;
		setThreshold(determineMinThreshold(reporters));
	}

	@Override
	protected void reportSupport(Severity level, Class clazz, String message) {
		for (Reporter reporter: reporters) {
			reporter.report(level, clazz, message);
		}
	}
	
	public CompositeReporter() {
		super();
	}

	/**
	 * Start with a list of reporters. The threshold for the composite will
	 * be set to the the lowest threshold in the reporter list.
	 * @param listOfReporters
	 */
	public CompositeReporter(List<Reporter> listOfReporters) {
		super(determineMinThreshold(listOfReporters));
		setReporters(listOfReporters);
	}

	/**
	 * Start with a list of reporters. The threshold for the composite will
	 * be set to the input threshold, regardless of the individual reporters
	 * threshold. This may mean that some reporters won't log events they would
	 * otherwise log, if their thresholds are lower than the composite's. 
	 * Conversely, individual thresholds above the composite's will still log
	 * based on their thresholds.
	 * @param threshold
	 * @param listOfReporters
	 */
	public CompositeReporter(Severity threshold, List<Reporter> listOfReporters) {
		super(threshold);
		setReporters(listOfReporters);
	}
	/**
	 * See {@link #CompositeReporter(Severity, List)} for information on threshold
	 * handling.
	 * @param threshold
	 */
	public CompositeReporter(Severity threshold) {
		super(threshold);
	}
	
	static protected Severity determineMinThreshold (List<Reporter> reporters) {
		Severity threshold = Severity.OFF;
		for (Reporter reporter: reporters) {
			Severity t = reporter.getThreshold();
			if (t.compareTo(threshold) < 0) {
				threshold = t;
			}
		}
		return threshold;
	}
}
