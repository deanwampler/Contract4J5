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

@Contract
@Invar("$this.field1 != null")
public class OverriddenContractBase {
	String field1 = null;
	public String getField1() {
		return field1;
	}
	public void setField1(String field1) {
		this.field1 = field1;
	}

	@Invar String field2 = null;
	public String getField2() {
		return field2;
	}
	public void setField2(String field2) {
		this.field2 = field2;
	}
	
	String field3 = null;
	@Post("$this.field3.equals(\"foo\") || $this.field3.equals(\"bar\")")
	public String getField3() {
		return field3;
	}
	@Pre ("$args[0] != null && $args[0].length() >= 3")
	public void setField3(String field3) {
		this.field3 = field3;
	}
	
	int invarFlagMethod = 0;
	public int getInvarFlagMethod() { return invarFlagMethod; }
	@Invar("$this.invarFlagMethod == 0")
	public void doNothing() {}
	
	int invarFlagCtor = 0;
	public int getInvarFlagCtor() { return invarFlagCtor; }
	int postFlag  = 0;
	public int  getPostFlag() { return postFlag; }
	public void setPostFlag(int f) { postFlag = f; }

	@Invar("$this.invarFlagCtor == 0")
	@Pre  ("$args[0] != null && $args[1] != null && $args[2] != null")
	@Post ("$this.postFlag > 0")
	public OverriddenContractBase (String f1, String f2, String f3) {
		this.field1 = f1;  // Avoid setters and their tests!
		this.field2 = f2;  
		this.field3 = f3;
		postFlag = 1;
	}
}