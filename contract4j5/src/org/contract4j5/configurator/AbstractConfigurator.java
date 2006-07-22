package org.contract4j5.configurator;

import org.contract4j5.ContractEnforcer;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.testexpression.ParentTestExpressionFinder;
import org.contract4j5.util.reporter.Reporter;
import org.contract4j5.util.reporter.WriterReporter;
import org.contract4j5.aspects.*;

public abstract class AbstractConfigurator implements Configurator {

	private Reporter reporter = null;
	
	/** 
	 * Returns the configurator's reporter. By default, a {@link WriterReporter} is returned.
	 * @see org.contract4j5.configurator.Configurator#getReporter()
	 */
	public Reporter getReporter() {
		if (reporter == null) {
			reporter = new WriterReporter();
			propagateReporter(reporter);
		}
		return reporter;
	}

	/**
	 * Set the reporter used by default. Calling this method will
	 * override any reporters that have been set individually on other "beans"!
	 * @param reporter
	 */
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
		propagateReporter(reporter);
	}

	protected void propagateReporter (Reporter reporter) {
		Contract4J.setReporter(reporter);
		ContractEnforcer ce = Contract4J.getContractEnforcer();
		if (ce != null) {
			ce.setReporter(reporter);
			ExpressionInterpreter ei = ce.getExpressionInterpreter();
			if (ei != null) {
				ei.setReporter(reporter);
			}
		}
		ParentTestExpressionFinder ptef = 
			ConstructorBoundaryConditions.getParentTestExpressionFinder();
		if (ptef != null) {
			ptef.setReporter(reporter);
		}
		ptef = MethodBoundaryConditions.getParentTestExpressionFinder();
		if (ptef != null) {
			ptef.setReporter(reporter);
		}
		ptef = InvariantConditions.InvariantTypeConditions.getParentTestExpressionFinder();
		if (ptef != null) {
			ptef.setReporter(reporter);
		}
		ptef = InvariantConditions.InvariantMethodConditions.getParentTestExpressionFinder();
		if (ptef != null) {
			ptef.setReporter(reporter);
		}
		ptef = InvariantConditions.InvariantCtorConditions.getParentTestExpressionFinder();
		if (ptef != null) {
			ptef.setReporter(reporter);
		}
	}

	/**
	 * Template method.
	 * @see org.contract4j5.configurator.Configurator#configure()
	 */
	public void configure() {
		Contract4J.setConfigured(true);
		doConfigure();
	}
	
	abstract protected void doConfigure();
}
