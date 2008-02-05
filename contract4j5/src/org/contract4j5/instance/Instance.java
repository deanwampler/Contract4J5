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

package org.contract4j5.instance;

/**
 * Encapsulate a particular "instance" of a class as part of the context
 * information used by a test, including it's name and class, when known. 
 * One reason for not just using <code>instance.getClass()</code>,
 * is to properly support the case where instance == null, but the class is
 * actually known, e.g., for a method argument.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class Instance {
	String itemName = "";
	public  String getItemName () { return itemName; }
	public  void   setItemName (String itemName) { this.itemName = itemName; };
	
	Class<?> clazz = null;
	public  Class<?> getClazz() { return clazz; }
	public  void     setClazz(Class<?> clazz) { this.clazz = clazz; }

	Object value = null;
	public  Object getValue() { return value; }
	public  void   setValue(Object value) { this.value = value; }
	
	public Instance() {}
	public Instance(String itemName, Class<?> clazz, Object value) {
		this.itemName = itemName;
		this.clazz = clazz;
		this.value = value;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(256);
		sb.append("[name = ");
		sb.append(itemName);
		sb.append(", class = ");
		sb.append(clazz);
		sb.append(", value = ");
		sb.append(value);
		sb.append("]");
		return sb.toString();
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result
				+ ((itemName == null) ? 0 : itemName.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Instance other = (Instance) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		if (itemName == null) {
			if (other.itemName != null)
				return false;
		} else if (!itemName.equals(other.itemName))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}