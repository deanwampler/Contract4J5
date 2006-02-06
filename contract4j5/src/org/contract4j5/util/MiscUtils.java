package org.contract4j5.util;

import org.contract4j5.Instance;

/**
 * "Miscellaneous", generic utilities. Some of them are here because we can't add static method
 * declarations to interfaces!
 * @author Dean Wampler
 */
public class MiscUtils {
	/**
	 * Make an array of instances from an input array of names, types, and values. Normally,
	 * the input arrays should be equal length. However, if they aren't, we create an Instance
	 * array of length equal to the longest input array and assign the values, in order, filling
	 * in "" for the <code>itemName</code> when we run out of names, and null for the
	 * <code>class</code> and <code>value</code> when we run out classes and values, respectively.
	 * @param names
	 * @param argTypes
	 * @param argValues
	 * @return null on error, other wise an Instance array of length equal to the input arrays
	 * with the values assigned in order.
	 */
	static public Instance[] makeInstanceArray(String[] argNames, Class[] argTypes, Object[] argValues) {
		int nlen = argNames  != null ? argNames.length  : 0;
		int clen = argTypes  != null ? argTypes.length  : 0;
		int vlen = argValues != null ? argValues.length : 0;
		int len = nlen;
		if (clen > len) { len = clen; }
		if (vlen > len) { len = vlen; }
		Instance[] instances = new Instance[len];
		for (int i=0; i<len; i++) {
			String name  = i < nlen ? argNames[i]  : "";
			Class  clazz = i < clen ? argTypes[i]  : null;
			Object value = i < vlen ? argValues[i] : null;
			instances[i] = new Instance (name, clazz, value);
		}
		return instances;
	}
	
	/**
	 * @param instances
	 * @return a String array of the instance names.
	 */
	static public String[] getInstanceNames(Instance[] instances) {
		if (instances == null) {
			return null;
		}
		String[] s = new String[instances.length];
		for (int i=0; i<instances.length; i++) {
			s[i] = instances[i].getItemName();
		}
		return s;
	}

	/**
	 * @param instances
	 * @return a Class array of the instance classes.
	 */
	static public Class[] getInstanceClasses(Instance[] instances) {
		if (instances == null) {
			return null;
		}
		Class[] c = new Class[instances.length];
		for (int i=0; i<instances.length; i++) {
			c[i] = instances[i].getClass();
		}
		return c;
	}
	
	/**
	 * @param instances
	 * @return an Object array of the instance values.
	 */
	static public Object[] getInstanceValues(Instance[] instances) {
		if (instances == null) {
			return null;
		}
		Object[] o = new Object[instances.length];
		for (int i=0; i<instances.length; i++) {
			o[i] = instances[i].getValue();
		}
		return o;
	}
}
