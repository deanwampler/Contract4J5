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

package org.contract4j5.test;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;

/**
 * Class implementing the interface w/ the "@Contract" annotation. 
 * Note that we can't declare field invariant tests nor constructor tests
 * in interfaces; there is no where to put them. The best you can do is 
 * tests on accessor methods and class invariants.
 */
@Contract
@Invar
public class ExampleContractInterfaceImpl implements ExampleContractInterface {
	private String name = null;
	@Pre
	public void setName(String name) {
		this.name = name;
	}
	@Post
	public String getName() {
		return name;
	}
	
	@Invar("$this.b == true")
	private boolean b = false;
	public boolean getB() {
		return b;
	}
	
	int flag = 0;
	public int getFlag() { return flag; }
	
	@Invar
	public void m (String s) {
		this.name = s;
		if (s.equals("bad")) {
			this.name = "";
		}
	}
	
	// C'tor that obeys all the contract tests.
	// Note that preconditions must use $args[i], not field names.
	@Pre("$args[0] != 100")
	@Post("!$this.name.equals(\"bad\")")
	public ExampleContractInterfaceImpl (int flag) { 
		this.flag = flag; 
		setName("ContractInterfaceImpl obj2");
		b = true;
	}
	
	// C'tor that specifically fails test depending on the value of the 2nd flag
	// or none of them if the value is 0. Note that the post condition allows a
	// null value so getName() conditions can be tested.
	@Pre("$args[0] != 100")
	@Post("$this.name == null || !$this.name.equals(\"bad\")")
	public ExampleContractInterfaceImpl (int flag, int whichFailure) { 
		this.flag = flag; 
		setName("ContractInterfaceImpl obj2");
		this.b = true;
		switch (whichFailure) {
		case 0:
			break; 		// No failures
		case 1:
			// reserved
			break;
		case 2:
			this.flag = 0;		// Fail the class invariant
			break;
		case 3:
			this.name = "bad";	// Fail the c'tor's own postcond.
			break;
		case 4:
			this.name = null;		// Set up later failure of getName() post. and m() invariant
			break;
		case 5:
			this.b = false;		// Fail the field invariant
			break;
		default:
			assert false: "Unknown value for whichFailure: "+whichFailure;
		}
	}
}
