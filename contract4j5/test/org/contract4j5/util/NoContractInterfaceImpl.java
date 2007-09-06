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

import org.contract4j5.contract.Invar;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;

/**
 * Class implementing the interface w/out the "@Contract" annotation. 
 * Note that we can't declare field invariant tests nor constructor tests
 * in interfaces; there is no where to put them. The best you can do is 
 * tests on accessor methods and class invariants.
 */
public class NoContractInterfaceImpl implements NoContractInterface {
	int flag = 0;
	private String name = null;
	@Invar("b == true")
	private boolean b = false;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean getB() {
		return b;
	}
	
	public int getFlag() { return flag; }
	
	public void m(String s) {
		this.name = "";
	}
	
	@Pre("$args[0] != 100")
	@Post("!$this.name.equals(\"bad\")")
	public NoContractInterfaceImpl (int flag) { this.flag = flag; }
}
