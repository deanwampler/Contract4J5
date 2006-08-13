package org.contract4j5.configurator.test;

import org.contract4j5.ContractEnforcer;
import org.contract4j5.ContractEnforcerImpl;
import org.contract4j5.aspects.ConstructorBoundaryConditions;
import org.contract4j5.aspects.Contract4J;
import org.contract4j5.aspects.InvariantConditions;
import org.contract4j5.aspects.MethodBoundaryConditions;
import org.contract4j5.configurator.AbstractConfigurator;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.jexl.JexlExpressionInterpreter;
import org.contract4j5.testexpression.ParentTestExpressionFinder;
import org.contract4j5.testexpression.ParentTestExpressionFinderImpl;
import org.contract4j5.util.reporter.Reporter;
import org.contract4j5.util.reporter.WriterReporter;

public class ConfiguratorForTesting extends AbstractConfigurator {

	@Override
	protected void doConfigure() {
		Contract4J.setEnabled(Contract4J.TestType.Pre,   true);
		Contract4J.setEnabled(Contract4J.TestType.Post,  true);
		Contract4J.setEnabled(Contract4J.TestType.Invar, true);
		Reporter reporter = new WriterReporter();
		Contract4J.setReporter(reporter);
		ContractEnforcer ce = new ContractEnforcerImpl(); 
		Contract4J.setContractEnforcer(ce);
		ce.setReporter(reporter);
		ExpressionInterpreter ei = new JexlExpressionInterpreter();
		ce.setExpressionInterpreter(ei);
		ei.setReporter(reporter);

		ParentTestExpressionFinder ptef = new ParentTestExpressionFinderImpl(); 
		ptef.setReporter(reporter);
		ConstructorBoundaryConditions.setParentTestExpressionFinder(ptef);
		MethodBoundaryConditions.setParentTestExpressionFinder(ptef);
		
		InvariantConditions.InvariantTypeConditions.setParentTestExpressionFinder(ptef);
		InvariantConditions.InvariantMethodConditions.setParentTestExpressionFinder(ptef);
		InvariantConditions.InvariantCtorConditions.setParentTestExpressionFinder(ptef);

		// Using null will cause the aspects to use their default values.
		InvariantConditions.InvariantFieldConditions.setDefaultFieldInvarTestExpressionMaker(null);
		InvariantConditions.InvariantFieldCtorConditions.setDefaultFieldInvarTestExpressionMaker(null);
		InvariantConditions.InvariantTypeConditions.setDefaultTypeInvarTestExpressionMaker(null);
		InvariantConditions.InvariantMethodConditions.setDefaultMethodInvarTestExpressionMaker(null);
		InvariantConditions.InvariantCtorConditions.setDefaultCtorInvarTestExpressionMaker(null);
	}
}
