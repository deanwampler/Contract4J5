/*
 * Copyright 2005 Dean Wampler. All rights reserved.
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
 * @author deanwampler <dean@aspectprogramming.com>
 */

package org.contract4j.aspects;

/** Declare precedence of advice: See documentation for why we use this order.
 */
public aspect DeclareContractPrecedence {
	declare precedence :
		ConstructorBoundaryConditions, 
		MethodBoundaryConditions, 
		InvariantConditions,
		InvariantConditions.InvariantCtorConditions,
		InvariantConditions.InvariantMethodConditions,
		InvariantConditions.InvariantFieldCtorConditions,
		InvariantConditions.InvariantFieldConditions,
		InvariantConditions.InvariantTypeConditions;
}
