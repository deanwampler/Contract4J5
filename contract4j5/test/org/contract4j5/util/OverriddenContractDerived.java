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

package org.contract4j5.util;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;

@Contract
@Invar
public class OverriddenContractDerived extends OverriddenContractBase {
	public String getField1() {
		return super.getField1();
	}
	public void setField1(String field1) {
		super.setField1(field1);
	}

	// Covariant postcondition (narrowed)
	@Post("$this.field.equals(\"foo\")")
	public String getField3() {
		return super.getField3();
	}
	// Contravariant precondition (widened)
	@Pre ("$args[0] != null && $args[0].length() >= 1")
	public void setField3(String field3) {
		super.setField3(field3);
	}
	
	@Invar
	public void doNothing() {
		super.doNothing();
	}

	@Invar
	// Contravariant precondition (widened)
	@Pre  ("$args[0] != null")
	// Covariant postcondition (narrowed)
	@Post ("$this.postFlag > 1")
	public OverriddenContractDerived (String f1, String f2, String f3) {
		super(f1, f2, f3);
		setPostFlag (2);
	}
}