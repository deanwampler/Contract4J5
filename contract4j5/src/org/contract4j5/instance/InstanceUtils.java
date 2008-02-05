/*
 * Copyright 2005 2006 Dean Wampler. All rights reserved.
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
 * "Miscellaneous", generic utilities. Some of them are here because we can't add static method
 * declarations to interfaces!
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class InstanceUtils {
	/**
	 * Make an array of instances from an input array of names, types, and values. Normally,
	 * the input arrays should be equal length. However, if they aren't, we create an Instance
	 * array of length equal to the longest input array and assign the values, in order, filling
	 * in "" for the <code>itemName</code> when we run out of names, and null for the
	 * <code>class</code> and <code>value</code> when we run out classes and values, respectively.
	 * @param argNames
	 * @param argTypes
	 * @param argValues
	 * @return null on error, other wise an Instance array of length equal to the input arrays
	 * with the values assigned in order.
	 */
	static public Instance[] makeInstanceArray(String[] argNames, Class<?>[] argTypes, Object[] argValues) {
		int nlen = argNames  != null ? argNames.length  : 0;
		int clen = argTypes  != null ? argTypes.length  : 0;
		int vlen = argValues != null ? argValues.length : 0;
		int len = nlen;
		if (clen > len) { len = clen; }
		if (vlen > len) { len = vlen; }
		Instance[] instances = new Instance[len];
		for (int i=0; i<len; i++) {
			String   name  = i < nlen ? argNames[i]  : "";
			Class<?> clazz = i < clen ? argTypes[i]  : null;
			Object   value = i < vlen ? argValues[i] : null;
			instances[i]   = new Instance (name, clazz, value);
		}
		return instances;
	}
	
	/**
	 * @param instances
	 * @return an Object array of the instance values.
	 */
	static public Object[] getInstanceValues(Instance[] instances) {
		if (instances == null) {
			return new Object[0];
		}
		Object[] o = new Object[instances.length];
		for (int i=0; i<instances.length; i++) {
			o[i] = instances[i].getValue();
		}
		return o;
	}
}
