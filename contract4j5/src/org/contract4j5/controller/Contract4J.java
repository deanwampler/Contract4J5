package org.contract4j5.controller;

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

import org.contract4j5.configurator.Configurator;
import org.contract4j5.configurator.properties.PropertiesConfigurator;
import org.contract4j5.enforcer.ContractEnforcer;
import org.contract4j5.enforcer.defaultimpl.DefaultContractEnforcer;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.WriterReporter;

/**
 * Global services for Contract4J classes and aspects.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class Contract4J {

	private static Contract4J systemInstance = null;

	/**
	 * An unavoidable singleton (UNLESS you're using Spring, which we can't assume...), 
	 * but hopefully the only one required in C4J, except for the singleton aspects. 
	 */
	public static void setInstance(Contract4J c4j) { 
		systemInstance = c4j; 
	}
	public static Contract4J getInstance() { 
		if (systemInstance == null)
			systemInstance = new Contract4J();
		return systemInstance; 
	}
	

	public boolean isPreTestsEnabled()   { return isEnabled[TestType.Pre.ordinal()]; }
	public boolean isPostTestsEnabled()  { return isEnabled[TestType.Post.ordinal()]; }
	public boolean isInvarTestsEnabled() { return isEnabled[TestType.Invar.ordinal()]; }

	public void setPreTestsEnabled(boolean b)   { isEnabled[TestType.Pre.ordinal()] = b; }
	public void setPostTestsEnabled(boolean b)  { isEnabled[TestType.Post.ordinal()] = b; }
	public void setInvarTestsEnabled(boolean b) { isEnabled[TestType.Invar.ordinal()] = b; }
	
	/**
	 * The types of contract tests.
	 */
	public enum TestType { Pre, Post, Invar };
	
	private boolean[] isEnabled = {true, true, true};
	
	/**
	 * Is the test type enabled globally?
	 * @param type  the type of test
	 * @return true if enabled globally, false otherwise
	 */
	public boolean isEnabled (TestType type) { 
		return isEnabled[type.ordinal()]; 
	}
	
	/**
	 * Set whether or not the test type is enabled globally.
	 * @param type the type of test
	 * @param b true if enabled globally, false otherwise
	 */
	public void setEnabled (TestType type, boolean b) { 
		isEnabled[type.ordinal()] = b; 
	}	
	
	private Configurator systemConfigurator = null;
	
	public void setSystemConfigurator (Configurator configurator) {
		systemConfigurator = configurator;
	}
	public Configurator getSystemConfigurator() { 
		if (systemConfigurator == null) 
			systemConfigurator = new PropertiesConfigurator();
		return systemConfigurator; 
	}

	private ContractEnforcer contractEnforcer;
	
	public void setContractEnforcer(ContractEnforcer ce) { 
		contractEnforcer = ce; 
	}
	public ContractEnforcer getContractEnforcer() { 
		if (contractEnforcer == null)
			contractEnforcer = new DefaultContractEnforcer();
		return contractEnforcer; 
	}
	
	private Reporter reporter;
	
	/** @return the reporter used for routine and error logging. */
	public void setReporter(Reporter reporter) { this.reporter = reporter; }
	public Reporter getReporter() {
		if (this.reporter == null)
			this.reporter = new WriterReporter();
		return this.reporter; 
	}

	public Contract4J(
			boolean[] isEnabledFlags, 
			Configurator systemConfigurator,
			ContractEnforcer contractEnforcer,
			Reporter reporter) {
		for (int i=0; i< isEnabledFlags.length; i++) 
			this.isEnabled[i] = isEnabledFlags[i];  // allow size mismatch to throw exception
		this.systemConfigurator = systemConfigurator;
		this.contractEnforcer = contractEnforcer;
		this.reporter = reporter;
	}

	public void registerGlobalContextObject(String name, Object object) {
		getContractEnforcer().getExpressionInterpreter().registerGlobalContextObject(name, object);
	}

	public void unregisterGlobalContextObject(String name) {
		getContractEnforcer().getExpressionInterpreter().unregisterGlobalContextObject(name);
	}
		
	public Contract4J() {}
}

