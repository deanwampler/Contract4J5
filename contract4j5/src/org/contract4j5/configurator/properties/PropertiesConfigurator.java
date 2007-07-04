/*
 * Copyright 2005, 2006 Dean Wampler. All rights reserved.
 * http://www.contract4j.org
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
package org.contract4j5.configurator.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.Severity;
import org.contract4j5.reporter.WriterReporter;
import org.contract4j5.testexpression.DefaultTestExpressionMaker;
import org.contract4j5.testexpression.ParentTestExpressionFinder;
import org.contract4j5.utils.StringUtils;

/**
 * Configure Contract4J using properties.  The System properties
 * will be read <em>first</em>, followed by an optional Properties object.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class PropertiesConfigurator extends AbstractConfigurator {
	public static final String PROPERTY_PREFIX = "org.contract4j5.";
	public static enum EnabledPropertyKeys {
		Contract, 
		Pre,
		Post,
		Invar
	};
	public static enum KnownBeanKeys {
		GlobalReporter,
		GlobalReporterThreshold,
		GlobalWriterReporterWriter,
		GlobalWriterReporterOutputStream, 
		ContractEnforcer,
		ContractEnforcerReportErrors,
		ContractEnforcerErrorReportingSeverity,
		ContractEnforcerIncludeStackTrace,
		ExpressionInterpreter,
		ExpressionInterpreterEmptyTestExpressionsValid,
		ExpressionInterpreterOptionalKeywordSubstitutions,
		DefaultFieldInvarTestExpressionMaker,
		DefaultFieldCtorInvarTestExpressionMaker,
		DefaultMethodInvarTestExpressionMaker,
		DefaultCtorInvarTestExpressionMaker,
		DefaultTypeInvarTestExpressionMaker,
		DefaultCtorPreTestExpressionMaker,
		DefaultCtorPostReturningVoidTestExpressionMaker,
		DefaultMethodPreTestExpressionMaker,
		DefaultMethodPostTestExpressionMaker,
		DefaultMethodPostReturningVoidTestExpressionMaker,
		CtorParentTestExpressionFinder,
		MethodParentTestExpressionFinder,
		MethodInvarParentTestExpressionFinder,
		CtorInvarParentTestExpressionFinder,
		TypeInvarParentTestExpressionFinder
	};
	
	private Properties properties = null;
	public Properties getProperties() { return properties; }
	public void       setProperties(Properties p) { properties = p; }
	
	/**
	 * Configure the system with a properties object. The System properties
	 * will be read <em>first</em>.
	 * @param properties
	 */
	public PropertiesConfigurator(Properties properties) {
		setProperties(properties);
	}
	
	/**
	 * Configure the system with the System properties only. 
	 */
	public PropertiesConfigurator() {}
	
	/**
	 * Configurator that reads the specification from the system properties.
	 * Provides only partial support for the configuration options.
	 * @see org.contract4j5.configurator.Configurator#configure()
	 */
	protected void doConfigure() {
		foundContractDisableProperty = false;  // starting over...
		errors.setLength(0);
		Properties properties = System.getProperties();
		initSystemProps(properties);
		properties = getProperties();
		if (properties != null) {
			initSystemProps(properties);
		}
	}

	private Reporter globalReporter = null;
	private Severity globalReporterThreshold = null;
	private java.io.Writer globalWriterReporterWriter = null;
	private java.io.OutputStream globalWriterReporterOutputStream = null;
	private boolean foundContractDisableProperty = false;
	private ContractEnforcer ce = null; 
	private Severity errorReportingSeverity = Severity.FATAL;
	private boolean errorReportingSeverityWasSet = false;
	private boolean reportErrors = true;
	private boolean reportErrorsWasSet = false;
	private boolean includeStackTrace = false;
	private boolean includeStackTraceWasSet = false;
	private ExpressionInterpreter ei = null; 
	private boolean emptyTestExprsValid = false;
	private boolean emptyTestExprsValidWasSet = false;
	private Map<String,String> optionalKeywordSubstitutions;

	private StringBuffer errors = new StringBuffer(1024);
	private Reporter reporter;
	
	protected void initSystemProps(Properties properties) {
		for (Object key: properties.keySet()) {
			String k = (String) key;
			if (k.startsWith(PROPERTY_PREFIX)) {
				String value = properties.getProperty(k);
				if (! processEnableTestTypeProperty(k, value)) {
					if (! processBean(k, value)) {
						recordUnknownPropertyError(k, value);
					}
				}
			}
		}
		configureContractEnforcer();
		configureGlobalReporter();
		if (errors.length() > 0) {
			try {
				reporter.report (Severity.ERROR, this.getClass(), errors.toString());
			} catch (NullPointerException npe) {
				System.err.println("No \"reporter\" was defined using the System Properties (See PropertiesConfigurator.java)");
				System.err.print(Severity.ERROR.name() + ": " + errors.toString());
			}
		}
	}

	private Contract4J getContract4J() {
		return Contract4J.getInstance();
	}
	protected boolean processEnableTestTypeProperty(String propKey, String propValue) {
		for (int i = 0; i < EnabledPropertyKeys.values().length; i++) {
			if (propKey != null && 
				propKey.equals(PROPERTY_PREFIX+EnabledPropertyKeys.values()[i])) {
				try {
					boolean value = convertToBoolean(propValue);
					if (i == 0) {	// The overall "contract" key?
						getContract4J().setEnabled(Contract4J.TestType.Pre,   value);
						getContract4J().setEnabled(Contract4J.TestType.Post,  value);
						getContract4J().setEnabled(Contract4J.TestType.Invar, value);
						foundContractDisableProperty = !value;
					} else {
						if (! foundContractDisableProperty)
							getContract4J().setEnabled(Contract4J.TestType.values()[i-1], value);
					}
				} catch (IllegalArgumentException iae) {
					recordEnableTestTypeError(propKey, propValue);
				}
				return true; // found and processed one of these options.
			}
		}
		return false;
	}

	protected boolean processBean(String beanName, String propValue) {
		if (propValue == null || propValue.trim().length() == 0) {
			recordEnableTestTypeError(beanName, propValue); 
			return true;  // we handled it here...
		}
		for (KnownBeanKeys beanKey: KnownBeanKeys.values()) {
			if (beanName != null &&	beanName.equals(PROPERTY_PREFIX+beanKey.name())) {
				try {
					switch (beanKey) {
					case GlobalReporter:
					{
						globalReporter = (Reporter) propertyToObject(propValue);
					}
					break;
					case GlobalReporterThreshold:
					{
						globalReporterThreshold = propertyToReporterThreshold(propValue);
					}
					break;
					case GlobalWriterReporterWriter:
					{
						globalWriterReporterWriter = (java.io.Writer) propertyToObject(propValue);						
					}
					break;
					case GlobalWriterReporterOutputStream:
					{
						globalWriterReporterOutputStream = (java.io.OutputStream) propertyToObject(propValue);
					}
					break;
					case ContractEnforcer:
					{
						ce = (ContractEnforcer) propertyToObject(propValue);
					}
					break;
					case ContractEnforcerReportErrors:
					{
						reportErrors = convertToBoolean(propValue);
						reportErrorsWasSet = true;
					}
					break;
					case ContractEnforcerErrorReportingSeverity:
					{
						errorReportingSeverity = convertToSeverity(propValue);
						errorReportingSeverityWasSet = true;
					}
					break;
					case ContractEnforcerIncludeStackTrace:
					{
						includeStackTrace = convertToBoolean(propValue);
						includeStackTraceWasSet = true;
					}
					break;
					case ExpressionInterpreter:
					{
						ei = (ExpressionInterpreter) propertyToObject(propValue);
					}
					break;
					case ExpressionInterpreterEmptyTestExpressionsValid:
					{
						emptyTestExprsValid = convertToBoolean(propValue);
						emptyTestExprsValidWasSet = true;
					}
					break;
					case ExpressionInterpreterOptionalKeywordSubstitutions:
					{
						processOptionalKeywordSubstitutions(propValue);
					}
					break;
					case DefaultFieldInvarTestExpressionMaker:
					{
						DefaultTestExpressionMaker dtem = 
							(DefaultTestExpressionMaker) propertyToObject(propValue);
						InvariantFieldConditions.aspectOf().setDefaultFieldInvarTestExpressionMaker(dtem);
					}	
					break;
					case DefaultFieldCtorInvarTestExpressionMaker:
					{
						DefaultTestExpressionMaker dtem = 
							(DefaultTestExpressionMaker) propertyToObject(propValue);
						// Must set the static value for all aspect instances here.
						InvariantFieldCtorConditions.aspectOf().setDefaultFieldInvarTestExpressionMaker(dtem);
					}
					break;
					case DefaultMethodInvarTestExpressionMaker:
					{
						DefaultTestExpressionMaker dtem = 
							(DefaultTestExpressionMaker) propertyToObject(propValue);
						InvariantMethodConditions.aspectOf().setDefaultMethodInvarTestExpressionMaker(dtem);
					}
					break;
					case DefaultCtorInvarTestExpressionMaker:
					{
						DefaultTestExpressionMaker dtem = 
							(DefaultTestExpressionMaker) propertyToObject(propValue);
						InvariantCtorConditions.aspectOf().setDefaultCtorInvarTestExpressionMaker(dtem);
					}
					break;
					case DefaultTypeInvarTestExpressionMaker:
					{
						DefaultTestExpressionMaker dtem = 
							(DefaultTestExpressionMaker) propertyToObject(propValue);
						InvariantTypeConditions.aspectOf().setDefaultTypeInvarTestExpressionMaker(dtem);
					}
					break;
					case DefaultCtorPreTestExpressionMaker:
					{
						DefaultTestExpressionMaker dtem = 
							(DefaultTestExpressionMaker) propertyToObject(propValue);
						ConstructorBoundaryConditions.aspectOf().setDefaultPreTestExpressionMaker(dtem);
					}
					break;
					case DefaultCtorPostReturningVoidTestExpressionMaker:
					{
						DefaultTestExpressionMaker dtem = 
							(DefaultTestExpressionMaker) propertyToObject(propValue);
						ConstructorBoundaryConditions.aspectOf().setDefaultPostReturningVoidTestExpressionMaker(dtem);
					}
					break;
					case DefaultMethodPreTestExpressionMaker:
					{
						DefaultTestExpressionMaker dtem = 
							(DefaultTestExpressionMaker) propertyToObject(propValue);
						MethodBoundaryConditions.aspectOf().setDefaultPreTestExpressionMaker(dtem);
					}
					break;
					case DefaultMethodPostTestExpressionMaker:
					{
						DefaultTestExpressionMaker dtem = 
							(DefaultTestExpressionMaker) propertyToObject(propValue);
						MethodBoundaryConditions.aspectOf().setDefaultPostTestExpressionMaker(dtem);
					}
					break;
					case DefaultMethodPostReturningVoidTestExpressionMaker:
					{
						DefaultTestExpressionMaker dtem = 
							(DefaultTestExpressionMaker) propertyToObject(propValue);
						MethodBoundaryConditions.aspectOf().setDefaultPostReturningVoidTestExpressionMaker(dtem);
					}
					break;
					case CtorParentTestExpressionFinder:
					{
						ParentTestExpressionFinder ptef = (ParentTestExpressionFinder) propertyToObject(propValue);
						ConstructorBoundaryConditions.aspectOf().setParentTestExpressionFinder(ptef);
					}
					break;
					case MethodParentTestExpressionFinder:
					{
						ParentTestExpressionFinder ptef = (ParentTestExpressionFinder) propertyToObject(propValue);
						MethodBoundaryConditions.aspectOf().setParentTestExpressionFinder(ptef);
					}
					break;
					case MethodInvarParentTestExpressionFinder:
					{
						ParentTestExpressionFinder ptef = (ParentTestExpressionFinder) propertyToObject(propValue);
						InvariantMethodConditions.aspectOf().setParentTestExpressionFinder(ptef);
					}
					break;
					case CtorInvarParentTestExpressionFinder:
					{
						ParentTestExpressionFinder ptef = (ParentTestExpressionFinder) propertyToObject(propValue);
						InvariantCtorConditions.aspectOf().setParentTestExpressionFinder(ptef);
					}
					break;
					case TypeInvarParentTestExpressionFinder:
					{
						ParentTestExpressionFinder ptef = (ParentTestExpressionFinder) propertyToObject(propValue);
						InvariantTypeConditions.aspectOf().setParentTestExpressionFinder(ptef);
					}
					break;
					default:
						throw new UnsupportedOperationException("Forgot to support bean type \""+beanKey+"\"!");
					}
				} catch (Throwable th) {
					recordBeanPropertyError(beanName, propValue, th);
				}
				return true; // found and processed one of these options.
			}
		}
		return false;		
	}

	private Severity propertyToReporterThreshold(String propValue) {
		Severity s = Severity.parse(propValue);
		if (s == null) {
			errors.append("No Reporter Severity matching string \"");
			errors.append(propValue);
			errors.append("\". Ignored.");
			errors.append(StringUtils.newline());
		}
		return s;
	}

	private void processOptionalKeywordSubstitutions(String propValue) {
		optionalKeywordSubstitutions = new HashMap<String,String>();
		String s = propValue.trim();
		for (String nvPair: s.split("\\s*,\\s*")) {
			String[] pair = nvPair.split("\\s*=\\s*");
			if (pair[0].length() == 0) {
				errors.append("keyword substitution format error: name empty in a name value pair.");
				errors.append("Map definition string is \"");
				errors.append(propValue);
				errors.append("\". Format should be \"name1=value1, name2=value2, ...\"");
				errors.append(StringUtils.newline());
			} else {
				optionalKeywordSubstitutions.put(pair[0], pair[1]);
			}
		}
	}

	private Object propertyToObject(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class clazz = Class.forName(className);
		Object object = clazz.newInstance();
		return object;
	}

	private void configureContractEnforcer() {
		if (ce == null) {
			ce = getContract4J().getContractEnforcer();
		} else {
			getContract4J().setContractEnforcer(ce);
		}
		if (ce != null) {
			if (reportErrorsWasSet) {
				ce.setReportErrors(reportErrors);
			}
			if (errorReportingSeverityWasSet) {
				ce.setErrorReportingSeverityLevel(errorReportingSeverity);
			}
			if (includeStackTraceWasSet) {
				ce.setIncludeStackTrace(includeStackTrace);
			}
			if (ei == null) {
				ei = ce.getExpressionInterpreter();
			} else {
				ce.setExpressionInterpreter(ei);
			}
			if (ei != null) {
				if (emptyTestExprsValidWasSet) {
					ei.setTreatEmptyTestExpressionAsValidTest(emptyTestExprsValid);
				}
				if (optionalKeywordSubstitutions != null) {
					ei.setOptionalKeywordSubstitutions(optionalKeywordSubstitutions);
				}
			}
		}
	}

	private void configureGlobalReporter() {
		initGlobalReporterIfNotInitialized();
		getContract4J().setReporter(globalReporter);
		if (globalReporterThreshold != null)
			globalReporter.setThreshold(globalReporterThreshold);
		if ((globalWriterReporterWriter != null ||
			globalWriterReporterOutputStream != null) && 
			!(globalReporter instanceof WriterReporter)) {
			errors.append("The \"global\" reporter is not a \"WriterReporter\", so the value for the java.io.Writer or the java.io.OutputStream is ignored.");
			errors.append(StringUtils.newline());
		} else {
			if (globalWriterReporterWriter != null) {
				WriterReporter wr = (WriterReporter) globalReporter;
				wr.setWriters(globalWriterReporterWriter);
			}
			if (globalWriterReporterOutputStream != null) {
				WriterReporter wr = (WriterReporter) globalReporter;
				wr.setStreams(globalWriterReporterOutputStream);
				if (globalWriterReporterWriter != null) {
					errors.append("Both a global java.io.OutputStream and java.io.Writer specified for the global \"Reporter\". The OutputStream will be used.");
					errors.append(StringUtils.newline());
				}
			}
		}
	}
	private void initGlobalReporterIfNotInitialized() {
		if (globalReporter == null) {
			globalReporter = new WriterReporter();
		}
	}
	
	protected void recordUnknownPropertyError(String k, String value) {
		errors.append("Unrecognized property key \"");
		errors.append(k);
		errors.append("\" (value = \"");
		errors.append(value);
		errors.append("\") ignored.");
		errors.append(StringUtils.newline());
	}

	private void recordEnableTestTypeError(String propKey, String propValue) {
		errors.append("Invalid value \"");
		errors.append(propValue);
		errors.append("\" for property \"");
		errors.append(propKey);
		errors.append("\" ignored.");
		errors.append(StringUtils.newline());
	}
	
	private void recordBeanPropertyError(String beanName, Object object, Throwable th) {
		errors.append("Invalid value (type?) \"");
		errors.append(object);
		errors.append("\" for property \"");
		errors.append(beanName);
		errors.append("\" ignored. (");
		errors.append(th.toString());
		errors.append(")");
		errors.append(StringUtils.newline());
	}
	
	/**
	 * @param booleanString string that should start with "t", "f", "y", "n", or equals
	 * "on", or "off", case ignored. We assume the string is not null or empty.
	 * @return true or false corresponding to input string
	 * @throws IllegalArgumentException if the input string doesn't match an expected value.
	 */
	protected static boolean convertToBoolean (String booleanString) throws IllegalArgumentException {
		if (booleanString == null || booleanString.length() == 0)
			throw new IllegalArgumentException("Boolean value string actually null or empty.");
		String bool = booleanString.trim();
		char c = bool.charAt(0);
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
		if (bool.equalsIgnoreCase("on")) {
			return true;
		}
		if (bool.equalsIgnoreCase("off")) {
			return false;
		}
		throw new IllegalArgumentException("Boolean value string unrecognized: \""+bool+"\".");
	}

	private Severity convertToSeverity(String propValue) {
		return Severity.parse(propValue);
	}
	
}
