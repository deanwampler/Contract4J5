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
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.jexl.JexlExpressionInterpreter;
import org.contract4j5.TestContext;
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
	 * Keys for property files.
	 */
	public static final String[] enabledPropertyKeys = new String[] {
			"org.contract4j5.Contract",
			"org.contract4j5.Pre",
			"org.contract4j5.Post",
			"org.contract4j5.Invar",
	};
	
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
		if (Contract4J.reporter == null) {
			Contract4J.reporter = new WriterReporter();
		}
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
	 * Get the ContractEnforcer used to evaluate all tests. Reports a warning if the 
	 * enforcer object isn't initialized and then creates a default 
	 * {@link ContractEnforcerImpl} object with a {@link JexlExpressionEvaluator}.
	 * @return the ContractEnforcer
	 */
	public static ContractEnforcer getContractEnforcer () { 
		if (contractEnforcer == null) {
			ExpressionInterpreter ei = new JexlExpressionInterpreter();
			contractEnforcer = new ContractEnforcerImpl(ei, false);
			contractEnforcer.setReporter(getReporter());
			ei.setReporter(getReporter());

			getReporter().report (Severity.WARN, Contract4J.class, 
					"Contract4J.aj has no ContractEnforcer defined. Using a ContractEnforcerImpl"+
					"with a JexlExpressionInterpreter()");
		}
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
	

	public Contract4J() {
		initSystemProps();
	}
	
	/**
	 * Allow system properties to override other configuration settings.
	 */
	protected void initSystemProps() {
		for (int i = 0; i < enabledPropertyKeys.length; i++) {
			String propStr = System.getProperty(enabledPropertyKeys[i]);
			if (propStr != null && propStr.length() > 0) {
				try {
					boolean value = convertToBoolean(propStr);
					if (i == 0) {	// The overall "contract" key?
						setEnabled(TestType.Pre,   value);
						setEnabled(TestType.Post,  value);
						setEnabled(TestType.Invar, value);
						if (value == false) {
							return;	// Don't read the props for any other keys
						}
					} else {
						setEnabled(TestType.values()[i], value);
					}
				} catch (IllegalArgumentException iae) {
					getReporter().report (Severity.ERROR, Contract4J.class, 
						"Invalid value \""+propStr+"\" for property \""+enabledPropertyKeys[i]+"\" ignored.");
				}	
			}
		}
	}
	
	/**
	 * @param s string that should start with "t", "f", "y", "n", or equals
	 * "on", or "off", case ignored. We assume the string is not null or empty.
	 * @return true or false corresponding to input string
	 * @throws IllegalArgumentException if the input string doesn't match an expected value.
	 */
	protected static boolean convertToBoolean (String s) throws IllegalArgumentException {
		s = s.trim();
		char c = s.charAt(0);
		switch (c) {
		case 't':
		case 'T':
		case 'y':
		case 'Y':
			return true;
		case 'f':
		case 'F':
		case 'n':
		case 'N':
			return false;
			default:
		}
		if (s.equalsIgnoreCase("on")) {
			return true;
		}
		if (s.equalsIgnoreCase("off")) {
			return false;
		}
		throw new IllegalArgumentException();
	}
	
}

