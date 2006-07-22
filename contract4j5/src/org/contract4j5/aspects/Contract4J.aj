/*
 * Copyright 2005, 2006 Dean Wampler. All rights reserved.
 * http://www.aspectprogramming.com
 *
 * Licensed under the Eclipse Public License - v 1.0; you may not use this
 * software except in compliance with the License. You may obtain a copy of the 
 * License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * A copy is also included with this distribution. See the "LICENSE" file.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */

package org.contract4j5.aspects;

import java.util.Map;

import org.contract4j5.Contract;
import org.contract4j5.ContractEnforcer;
import org.contract4j5.ContractEnforcerImpl;
import org.contract4j5.TestContext;
import org.contract4j5.configurator.Configurator;
import org.contract4j5.configurator.PropertiesConfigurator;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.jexl.JexlExpressionInterpreter;
import org.contract4j5.util.reporter.Reporter;
import org.contract4j5.util.reporter.Severity;
import org.contract4j5.util.reporter.WriterReporter;

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
abstract public aspect Contract4J {
	/**
	 * Marker interface that is used with intertype declaration to support the
	 * desired inheritance of contracts.
	 */
	public static interface ContractMarker {}
	
	declare parents: (@Contract *) implements ContractMarker;
		
	/**
	 * The types of contract tests.
	 */
	public enum TestType { Pre, Post, Invar };
	
	static private boolean isEnabled[] = {true, true, true};
	
	/**
	 * Is the test type enabled globally?
	 * @param type  the type of test
	 * @return true if enabled globally, false otherwise
	 */
	static public boolean isEnabled (TestType type) { 
		return isEnabled[type.ordinal()]; 
	}
	
	/**
	 * Set whether or not the test type is enabled globally.
	 * @param type the type of test
	 * @param b true if enabled globally, false otherwise
	 */
	static public void setEnabled (TestType type, boolean b) { 
		isEnabled[type.ordinal()] = b; 
	}	
	
	static private Configurator systemConfigurator = null;
	
	/**
	 * Return the system configurator used by Contract4J. If null, no 
	 * configuration will be done, which is appropriate if using an "external"
	 * configurator, such as the Spring IoC container.
	 * @return the system configurator. May be null.
	 */
	static public Configurator getSystemConfigurator() {
		return systemConfigurator;
	}
	
	static public void setSystemConfigurator (Configurator configurator) {
		systemConfigurator = configurator;
		configured = false;
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
		if (isEnabled(TestType.Pre)) &&
		commonC4J();
	
	/**
	 * PCD common for all postcondition tests.
	 */
	pointcut postCommon() : 
		if (isEnabled(TestType.Post)) &&
		commonC4J();
	
	/**
	 * PCD common for all invariant tests.
	 */
	pointcut invarCommon() : 
		if (isEnabled(TestType.Invar)) &&
		commonC4J();
	

	private static Reporter reporter;
	
	/**
	 * @return the reporter used for routine logging.
	 */
	public static Reporter getReporter() {
		return Contract4J.reporter;
	}

	/**
	 * @param reporter used for routing logging.
	 */
	public static void setReporter(Reporter reporter) {
		Contract4J.reporter = reporter;
	}

	private static ContractEnforcer contractEnforcer = null;

	/**
	 * Set the ContractEnforcer object to use.
	 * @param contractEnforcer object that does the work of executing all tests
	 * and handling failures. If null, all tests are effectively disabled.
	 */
	public static void setContractEnforcer (ContractEnforcer contractEnforcer) { 
		Contract4J.contractEnforcer = contractEnforcer;
	}

	/**
	 * Get the ContractEnforcer used to evaluate all tests. 
	 * @return the ContractEnforcer, which might be null
	 */
	public static ContractEnforcer getContractEnforcer () { 
		return contractEnforcer;
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
	

	public Contract4J() {}

	// "Last resort" initialization; User's of C4J should do this explicitly as
	// described elsewhere.
	private static aspect MakeSureWeAreConfigured {
		pointcut thisAspect(): adviceexecution() && within(MakeSureWeAreConfigured);
		pointcut getContract4JState(): 
			!get(static boolean Contract4J.configured) && get(static * Contract4J+.*); 
		pointcut makeSureConfiguredFirst(): 
			if(Contract4J.isConfigured() == false) && getContract4JState() && !cflow(thisAspect());
		before(): makeSureConfiguredFirst() {
			lazyConfigure();
		}
	}
	
	protected static boolean configured = false;
	public static boolean isConfigured() {
		return configured;
	}

	public static void setConfigured(boolean configured) {
		Contract4J.configured = configured;
	}

	protected static void lazyConfigure() { 
		if (configured == false) {
			doDefaultConfiguration();
			configured = true;
		}
	}
	
	/**
	 * Last resort configuration; use a {@link PropertiesConfigurator}, then
	 * if not initialized, use a {@link ContractEnforcerImpl}, with a {@link
	 * JexlExpressionInterpreter} and a {@link WriterReporter}.
	 * @todo Move this logic elsewhere.
	 */
	protected static void doDefaultConfiguration() {
		Configurator configurator = getSystemConfigurator();
		if (configurator == null) {
			configurator = new PropertiesConfigurator();
			setSystemConfigurator(configurator);
		}
		configurator.configure();
		if (getContractEnforcer() == null) {
			setContractEnforcer(new ContractEnforcerImpl(new JexlExpressionInterpreter(), true));
		}
		Reporter r = getReporter();
		if (r == null) {
			r = new WriterReporter();
			setReporter(r);
		}
		r.report (Severity.WARN, Contract4J.class, 
			"Contract4J was not configured explicitly, so the default process " +
			"was used. See the unit tests for configuration examples.");
	}

}

