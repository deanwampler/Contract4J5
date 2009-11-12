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
package org.contract4j5.aspects;

import java.util.Map;

import org.contract4j5.context.TestContext;
import org.contract4j5.contract.Disabled;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.contract.Pre;
import org.contract4j5.contract.Post;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.enforcer.ContractEnforcer;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.testexpression.ParentTestExpressionFinder;
import org.contract4j5.testexpression.ParentTestExpressionFinderImpl;

/**
 * An abstract aspect that supports Design by Contract tests by advising classes,
 * aspects, methods, and fields that have Contract4J annotations applied to
 * them. This aspect provides some common features. Other aspects implement the
 * specific test types, etc.
 * Note that most PCDs will exclude static methods and only match classes (and their
 * subclasses) that are annotated with @Contract. Note that for practical reasons,
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
		
	public static Contract4J getContract4J() {
		return Contract4J.getInstance();
	}

	public ContractEnforcer getContractEnforcer () { 
		return getContract4J().getContractEnforcer();
	}

	private ParentTestExpressionFinder parentTestExpressionFinder = null;

	/**
	 * Get the object that determines the test expression from a corresponding
	 * parent-class test. Used when an test is declared without an expression.
	 * Note that it may be ignored by some concrete aspects if there will not
	 * be a corresponding parent test.
	 */
	public ParentTestExpressionFinder getParentTestExpressionFinder() {
		if (parentTestExpressionFinder == null) 
			parentTestExpressionFinder = new ParentTestExpressionFinderImpl();
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
	 * Note that we match only on types annotated with @Contract and their subclasses.
	 */
	pointcut commonC4J() : 
		within (@Contract *+) && ! within (@Disabled *+) &&
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
	
	/**
	 * Find the "$old(..)" expressions in the test expression, determine the corresponding values
	 * from the context and return those values in a map.
	 * @see ExpressionInterpreter#determineOldValues(TestContext)
	 */
	public Map<String, Object> determineOldValues (TestContext context) {
		ContractEnforcer ce = getContractEnforcer();
		return ce.getExpressionInterpreter().determineOldValues(context);
	}
}
