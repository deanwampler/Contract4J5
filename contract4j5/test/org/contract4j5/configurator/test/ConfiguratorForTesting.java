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
package org.contract4j5.configurator.test;

import org.apache.bsf.BSFException;
import org.contract4j5.configurator.AbstractConfigurator;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.enforcer.ContractEnforcer;
import org.contract4j5.enforcer.defaultimpl.DefaultContractEnforcer;
import org.contract4j5.interpreter.ExpressionInterpreterHelper;
import org.contract4j5.interpreter.groovy.GroovyExpressionInterpreter;
import org.contract4j5.interpreter.jexl.JexlExpressionInterpreter;
import org.contract4j5.interpreter.bsf.jruby.JRubyBSFExpressionInterpreter;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.WriterReporter;
import org.contract4j5.testexpression.ParentTestExpressionFinder;
import org.contract4j5.testexpression.ParentTestExpressionFinderImpl;

import org.contract4j5.aspects.ConstructorBoundaryConditions;
import org.contract4j5.aspects.MethodBoundaryConditions;
import org.contract4j5.aspects.InvariantFieldConditions;
import org.contract4j5.aspects.InvariantFieldCtorConditions;
import org.contract4j5.aspects.InvariantTypeConditions;
import org.contract4j5.aspects.InvariantMethodConditions;
import org.contract4j5.aspects.InvariantCtorConditions;

public class ConfiguratorForTesting extends AbstractConfigurator {
	public ExpressionInterpreterHelper expressionInterpreter;

	@Override
	protected void doConfigure() {
		doConfigureWithInterpreter(System.getProperty("interpreter"));
	}
	
	protected void doConfigureWithInterpreter(String whichInterpreter) {
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
		try {
			if (whichInterpreter == null || whichInterpreter.length() == 0)
				throw new ConfigurationFailedException("no interpreter specified");
//				expressionInterpreter = new GroovyExpressionInterpreter();
			else if (whichInterpreter.equalsIgnoreCase("groovy")) 
				expressionInterpreter = new GroovyExpressionInterpreter();
			else if (whichInterpreter.equalsIgnoreCase("jexl"))
				expressionInterpreter = new JexlExpressionInterpreter();
			else if (whichInterpreter.equalsIgnoreCase("jruby")) {
				expressionInterpreter = new JRubyBSFExpressionInterpreter();
			}
			else
				throw new BSFException("Unrecognized interpreter name: \""+whichInterpreter+"\".");
		} catch (BSFException e) {
			throw new ConfigurationFailedException("Could not configure with the Jexl BSF expression interpreter", e);
		}
		ce.setExpressionInterpreter(expressionInterpreter);

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
