package org.contract4j5.configurator.ensurer;

import org.apache.bsf.BSFException;
import org.contract4j5.configurator.Configurator;
import org.contract4j5.configurator.properties.PropertiesConfigurator;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.enforcer.defaultimpl.DefaultContractEnforcer;
import org.contract4j5.interpreter.bsf.jexl.JexlBSFExpressionInterpreter;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.Severity;
import org.contract4j5.reporter.WriterReporter;

/**
 * "Last resort" initialization; User's of C4J should explicitly initialize C4J
 * as described elsewhere. This aspect detects whether or not configuration was
 * ever done and if not, does so with a default configuration on the fly. The
 * purpose for doing this is to make it as easy as possible for users of C4J to
 * drop it into their environment with minimal effort.
 */
public aspect Contract4JConfigurationEnsurer {

	pointcut thisAspect(): within(Contract4JConfigurationEnsurer);
	pointcut getContract4JState(Contract4J c4j): 
		!withincode(*    Contract4J+.getSystemConfigurator(..)) && 
		!withincode(void Contract4J+.setSystemConfigurator(..)) &&
		get(* Contract4J+.*) &&
		this(c4j); 
	pointcut configureC4JFirstIfNotConfigured(Contract4J c4j): 
		if(c4j.getSystemConfigurator() == null) 
			&& getContract4JState(c4j) && !cflow(thisAspect());

	before(Contract4J c4j): configureC4JFirstIfNotConfigured(c4j) {
		doLazyConfiguration(c4j);
	}
	
	protected void doLazyConfiguration(Contract4J c4j) { 
		if (c4j.getSystemConfigurator() == null) {
			doDefaultConfiguration(c4j);
		}
	}
	
	/**
	 * Last resort configuration; use a {@link PropertiesConfigurator}, then
	 * if not initialized, use a {@link ContractEnforcerImpl}, with a {@link
	 * JexlBSFExpressionInterpreter} and a {@link WriterReporter}. 
	 * @note Lots of ugly violations of the Law of Demeter here!
	 */
	protected void doDefaultConfiguration(Contract4J c4j) {
		Configurator configurator = c4j.getSystemConfigurator();
		if (configurator == null) {
			configurator = new PropertiesConfigurator();
			c4j.setSystemConfigurator(configurator);
		}
		configurator.configure();
		if (c4j.getContractEnforcer() == null) {
			try {
				c4j.setContractEnforcer(new DefaultContractEnforcer(new JexlBSFExpressionInterpreter(), true));
			} catch (BSFException e) {
				throw new Configurator.ConfigurationFailedException("Could not create a Jexl BSF interpreter.", e);
			}
		}
		Reporter r = c4j.getReporter();
		if (r == null) {
			r = new WriterReporter();
			c4j.setReporter(r);
		}
		r.report (Severity.WARN, c4j.getClass(), 
			"Contract4J was not configured explicitly, so the default process " +
			"was used. See the unit tests for configuration examples.");
	}
}
