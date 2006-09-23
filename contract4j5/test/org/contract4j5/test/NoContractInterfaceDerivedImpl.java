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

import org.contract4j5.contract.Post;

/**
 * Class subclassing the {@link NoContractInterfaceImpl} class the implements
 * an interface w/out the "@Contract" annotation. 
 */
public class NoContractInterfaceDerivedImpl extends NoContractInterfaceImpl {
	public String getName() {
		return super.getName();
	}
	public void setName(String name) {
		super.setName(name);
	}
	
	public boolean getB() {
		return super.getB();
	}
	
	public int getFlag() { return super.getFlag(); }
	
	public void m(String s) {
		super.m(s);
	}
	
	@Post("")
	public NoContractInterfaceDerivedImpl (int flag) { 
		super(flag);
	}
}
