package org.contract4j5.test;

import java.io.PrintWriter;

import org.contract4j5.ContractEnforcer;
import org.contract4j5.ContractEnforcerImpl;
import org.contract4j5.aspects.ConstructorBoundaryConditions;
import org.contract4j5.aspects.Contract4J;
import org.contract4j5.aspects.InvariantConditions;
import org.contract4j5.aspects.MethodBoundaryConditions;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.jexl.JexlExpressionInterpreter;
import org.contract4j5.util.reporter.Reporter;
import org.contract4j5.util.reporter.Severity;
import org.contract4j5.util.reporter.WriterReporter;

/**
 * Manually configure all the dependencies. This is a service method
 * with all static methods.
 * @author Dean Wampler
 */
public class ManualSetup {
	private static ContractEnforcer contractEnforcer = null;

	public static ContractEnforcer getContractEnforcer() {
		return contractEnforcer;
	}

	public static void setContractEnforcer(ContractEnforcer contractEnforcer) {
		ManualSetup.contractEnforcer = contractEnforcer;
	}

	private static ExpressionInterpreter expressionInterpreter = null;
	
	public static ExpressionInterpreter getExpressionInterpreter() {
		return expressionInterpreter;
	}

	public static void setExpressionInterpreter(ExpressionInterpreter expressionInterpreter) {
		ManualSetup.expressionInterpreter = expressionInterpreter;
	}
	
	private static Reporter reporter;

	public static Reporter getReporter() {
		return reporter;
	}

	public static void setReporter(Reporter reporter) {
		ManualSetup.reporter = reporter;
	}

	/**
	 * Do the basic bean "wiring". A {@link WriterReporter} is used with severity
	 * set to "warn". We have to wire the 3 main aspects {@link ConstructorBoundaryConditions},
	 * {@link InvariantConditions}, and {@link MethodBoundaryConditions}
	 */
	public static void wireC4J() {
		expressionInterpreter = new JexlExpressionInterpreter();
		contractEnforcer = new ContractEnforcerImpl(expressionInterpreter, false);
		reporter = new WriterReporter(Severity.DEBUG, new PrintWriter(System.out));
		wireC4J(expressionInterpreter, contractEnforcer, reporter);
	}
	
	/**
	 * Do the basic bean "wiring" with user specified dependents. We have to wire the 
	 * 3 main aspects {@link ConstructorBoundaryConditions}, {@link InvariantConditions},
	 * and {@link MethodBoundaryConditions}
	 * @param expressionInterpreter
	 * @param contractEnforcer
	 * @param reporter
	 */
	public static void wireC4J(
			ExpressionInterpreter expressionInterpreter,
			ContractEnforcer      contractEnforcer,
			Reporter              reporter) {
		setExpressionInterpreter(expressionInterpreter);
		setContractEnforcer(contractEnforcer);
		setReporter(reporter);
		Contract4J.setContractEnforcer(contractEnforcer);
		Contract4J.setReporter(reporter);
		enableContracts(true, true, true);
		contractEnforcer.setReporter(reporter);
		expressionInterpreter.setReporter(reporter);
	}

	public static void enableContracts (boolean preOn, boolean postOn, boolean invarOn) {
		Contract4J.setEnabled(Contract4J.TestType.Pre,   preOn);
		Contract4J.setEnabled(Contract4J.TestType.Post,  postOn);
		Contract4J.setEnabled(Contract4J.TestType.Invar, invarOn);
	}
	
	private ManualSetup() {}
}
