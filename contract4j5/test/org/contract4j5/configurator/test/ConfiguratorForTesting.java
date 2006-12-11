package org.contract4j5.configurator.test;

import org.contract4j5.aspects.ConstructorBoundaryConditions;
import org.contract4j5.aspects.InvariantCtorConditions;
import org.contract4j5.aspects.InvariantFieldConditions;
import org.contract4j5.aspects.InvariantFieldCtorConditions;
import org.contract4j5.aspects.InvariantMethodConditions;
import org.contract4j5.aspects.InvariantTypeConditions;
import org.contract4j5.aspects.MethodBoundaryConditions;
import org.contract4j5.configurator.AbstractConfigurator;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.enforcer.ContractEnforcer;
import org.contract4j5.enforcer.defaultimpl.DefaultContractEnforcer;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.jexl.JexlExpressionInterpreter;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.WriterReporter;
import org.contract4j5.testexpression.ParentTestExpressionFinder;
import org.contract4j5.testexpression.ParentTestExpressionFinderImpl;

public class ConfiguratorForTesting extends AbstractConfigurator {
	@Override
	protected void doConfigure() {
		Contract4J c4j = new Contract4J();  // Start with fresh singleton
		c4j.setSystemConfigurator(this);
		Contract4J.setInstance(c4j);  
		c4j.setEnabled(Contract4J.TestType.Pre,   true);
		c4j.setEnabled(Contract4J.TestType.Post,  true);
		c4j.setEnabled(Contract4J.TestType.Invar, true);
		Reporter reporter = new WriterReporter();
		c4j.setReporter(reporter);
		ContractEnforcer ce = new DefaultContractEnforcer(); 
		c4j.setContractEnforcer(ce);
		ExpressionInterpreter ei = new JexlExpressionInterpreter();
		ce.setExpressionInterpreter(ei);

		ParentTestExpressionFinder ptef = new ParentTestExpressionFinderImpl(); 
		ConstructorBoundaryConditions.aspectOf().setParentTestExpressionFinder(ptef);
		MethodBoundaryConditions.aspectOf().setParentTestExpressionFinder(ptef);
		InvariantTypeConditions.aspectOf().setParentTestExpressionFinder(ptef);
		InvariantMethodConditions.aspectOf().setParentTestExpressionFinder(ptef);
		InvariantCtorConditions.aspectOf().setParentTestExpressionFinder(ptef);

		// Allow the aspects to initialize their own expression makers.
		ConstructorBoundaryConditions.aspectOf().setDefaultPreTestExpressionMaker(null);
		ConstructorBoundaryConditions.aspectOf().setDefaultPostReturningVoidTestExpressionMaker(null);
		MethodBoundaryConditions.aspectOf().setDefaultPreTestExpressionMaker(null);
		MethodBoundaryConditions.aspectOf().setDefaultPostTestExpressionMaker(null);
		MethodBoundaryConditions.aspectOf().setDefaultPostReturningVoidTestExpressionMaker(null);
		InvariantFieldConditions.aspectOf().setDefaultFieldInvarTestExpressionMaker(null);
		InvariantFieldCtorConditions.aspectOf().setDefaultFieldInvarTestExpressionMaker(null);
		InvariantTypeConditions.aspectOf().setDefaultTypeInvarTestExpressionMaker(null);
		InvariantMethodConditions.aspectOf().setDefaultMethodInvarTestExpressionMaker(null);
		InvariantCtorConditions.aspectOf().setDefaultCtorInvarTestExpressionMaker(null);
	}
}
