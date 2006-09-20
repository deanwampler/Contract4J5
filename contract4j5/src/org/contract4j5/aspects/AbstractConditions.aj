package org.contract4j5.aspects;

import java.util.Map;

import org.contract4j5.Contract;
import org.contract4j5.Contract4J;
import org.contract4j5.TestContext;
import org.contract4j5.enforcer.ContractEnforcer;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.testexpression.ParentTestExpressionFinder;
import org.contract4j5.testexpression.ParentTestExpressionFinderImpl;
import org.contract4j5.util.reporter.Reporter;

/**
 * An abstract aspect that supports Design by Contract tests by advising classes,
 * aspects, methods, and fields that have Contract4J annotations applied to
 * them. This aspect provides some common features. Other aspects implement the
 * specific test types, etc.
 * Note that most PCDs will exclude static methods and look for
 * classes and subclasses that implement the ContractMarker interface, which is
 * injected using intertype declaration. We use this marker interface, rather than
 * just looking for the @Contract annotation, because this allows us to match 
 * subclass pointcuts, whereas using the @Contract annotation in PCDs only matches
 * on classes that explicitly use the annotation! Hence, the marker interface allows
 * us to change the typical behavior of annotations to fit the expected model for 
 * contracts, which should be inherited. Note, however, that for practical reasons,
 * the @Contract annotation should always be used on subclasses anyway. One of those
 * reasons is to silence warnings that it is missing if you use the test annotations
 * in the subclasses!
 * Most PCDs must also include the "if (isEnabled(TestType...))" test, which determines
 * if a particular kind of annotation is disabled.
 * @note Many of the properties are declared static, which is less flexible than 
 * per-instance, but makes it easier for users to "wire" the property dependencies
 * using different means. Because the instantiation model of aspects is different from
 * objects, wiring is a little trickier. The preferred way is to use an IoC/DI
 * container like Spring, which makes it straightforward. However, since we don't want
 * to require usage of Spring or other DI toolkit, we compromised on flexibility.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
abstract public aspect AbstractConditions {
	/**
	 * Marker interface that is used with intertype declaration to support the
	 * desired inheritance of contracts.
	 */
	public static interface ContractMarker {}
	
	declare parents: (@Contract *) implements ContractMarker;
		
	public static Contract4J getContract4J() {
		return Contract4J.getInstance();
	}

	private ParentTestExpressionFinder parentTestExpressionFinder = 
		new ParentTestExpressionFinderImpl();

	/**
	 * Get the object that determines the test expression from a corresponding
	 * parent-class test. Used when an test is declared without an expression.
	 * Note that it may be ignored by some concrete aspects if there will not
	 * be a corresponding parent test.
	 */
	public ParentTestExpressionFinder getParentTestExpressionFinder() {
		return parentTestExpressionFinder;
	}

	public void setParentTestExpressionFinder(ParentTestExpressionFinder finder) {
		parentTestExpressionFinder = finder;
	}

	/**
	 * Common exclusions, etc. for all PCDs. For simplicity, and to prevent some subtle 
	 * bugs, we don't allow tests to be invoked implicitly within other tests, so we 
	 * exclude the cflow of the {@link ContractEnforcer} object. Tests within tests can
	 * happen if a test expression invokes a method call on the object and that method 
	 * call has tests, for example.
	 */
	pointcut commonC4J() : 
		within (ContractMarker+) &&
		!cflow(execution (* ContractEnforcer+.*(..))) &&
		!cflow(execution (ContractEnforcer+.new(..))); 
	
	/**
	 * PCD common for all precondition tests.
	 */
	pointcut preCommon() : 
		if (getContract4J().isEnabled(Contract4J.TestType.Pre)) &&
		commonC4J();
	
	/**
	 * PCD common for all postcondition tests.
	 */
	pointcut postCommon() : 
		if (getContract4J().isEnabled(Contract4J.TestType.Post)) &&
		commonC4J();
	
	/**
	 * PCD common for all invariant tests.
	 */
	pointcut invarCommon() : 
		if (getContract4J().isEnabled(Contract4J.TestType.Invar)) &&
		commonC4J();
	
	public ContractEnforcer getContractEnforcer () { 
		return getContract4J().getContractEnforcer();
	}

	/**
	 * Find the "$old(..)" expressions in the test expression, determine the corresponding values
	 * from the context and return those values in a map.
	 * @see ExpressionInterpreter#determineOldValues(String, TestContext)
	 */
	public Map<String, Object> determineOldValues (String testExpression, TestContext context) {
		ContractEnforcer ce = getContractEnforcer();
		return ce.getExpressionInterpreter().determineOldValues(testExpression, context);
	}

	protected Reporter getReporter() {
		return getContract4J().getReporter();
	}
}
