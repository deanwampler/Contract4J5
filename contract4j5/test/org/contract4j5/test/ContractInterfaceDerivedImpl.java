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

import org.contract4j5.Contract;
import org.contract4j5.Invar;
import org.contract4j5.Post;
import org.contract4j5.Pre;

/**
 * Class subclassing {@link ContractInterfaceImpl}, which 
 * implements the interface w/ the "@Contract" annotation. 
 * This class does not change the annotations.
 * Note that the setName and getName methods below are commented out.
 * Jexl apparently can't resolve "$this.name" for objects of this class,
 * if name isn't an attribute. It doesn't seem to automatically convert to
 * getName(). However, this works for the parent class where the attribute exists.
 * Also, the two commented methods below cause an infinite recursion and stack 
 * overflow, even though the aspects explicitly try to prevent this!
 */
@Contract
@Invar
public class ContractInterfaceDerivedImpl extends ContractInterfaceImpl {
//	public void setName(String name) {
//		super.setName(name);
//	}
//	public String getName() {
//		return getName();
//	}
	public boolean getB() {
		return super.getB();
	}
	public int getFlag() { 
		return super.getFlag();
	}
	public void m (String s) {
		super.m(s);
	}
	
	public ContractInterfaceDerivedImpl (int flag) { 
		super(flag); 
		setName("ContractInterfaceDerivedImpl obj2");
	}
	
	public ContractInterfaceDerivedImpl (int flag, int whichFailure) { 
		super(flag, whichFailure); 
	}
}
