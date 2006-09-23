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

import org.contract4j5.contract.Invar;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;

/**
 * Aspect to look for invalid usage of Contract4J. The following generate
 * warnings:
 * <ol>
 * <li>Warn when a precondition, postcondition, or invariant tests is defined 
 * on a class or interface without the @Contract annotation.</li>
 * <li>Warn when a test is defined on a static method.</li>
 * </ol>
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 * @note Static method tests are not supported because these methods are not
 * involved with an object's state or behavior. However, it's possible that
 * useful tests might be needed for class static "state", so this restriction
 * may be removed in a subsequent release.
 */
public aspect UsageEnforcement {
	pointcut preNotInContract() : 
		! within (AbstractConditions.ContractMarker+) &&
		(execution (@Pre * *.*(..)) || execution (@Pre *.new(..)));

	pointcut postNotInContract() : 
		! within (AbstractConditions.ContractMarker+) &&
		(execution (@Post * *.*(..)) || execution (@Post *.new(..)));
	
	pointcut invarNotInContract() : 
		! within (AbstractConditions.ContractMarker+) &&
		(execution (@Invar * *.*(..)) || execution (@Invar *.new(..)) ||
		 get (@Invar * *) || set (@Invar * *));

	pointcut staticTests() :
		execution (@(Pre || Post || Invar) static * *.*(..));
	
	declare warning: preNotInContract(): 
		"The @Pre Contract4J annotation requires the class annotation @Contract";
	declare warning: postNotInContract(): 
		"The @Post Contract4J annotation requires the class annotation @Contract";
	declare warning: invarNotInContract(): 
		"The @Invar Contract4J annotation requires the class annotation @Contract";
	declare warning: staticTests():
		"Tests cannot be defined on static methods.";
}
