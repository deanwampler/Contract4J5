package org.contract4j5;

/**
 * Encapsulate the "instance" of an object involved in a test, including it's name, class,
 * and object, when known. One reason for not just using <code>instance.getClass()</code>,
 * is to properly support the case where instance == null!
 */
public class Instance {
	String itemName = "";
	public  String getItemName () { return itemName; }
	public  void   setItemName (String itemName) { this.itemName = itemName; };
	
	Class  clazz = null;
	public  Class  getClazz() { return clazz; }
	public  void   setClazz(Class clazz) { this.clazz = clazz; }

	Object value = null;
	public  Object getValue() { return value; }
	public  void   setValue(Object value) { this.value = value; }
	
	public Instance() {}
	public Instance(String itemName, Class clazz, Object value) {
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
	
	public boolean equals (Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || !(o instanceof Instance)) {
			return false;
		}
		Instance i = (Instance) o;
		if (getItemName() == null) {
			if (i.getItemName() != null) {
				return false;
			}
		} else if (i.getItemName() == null) {
			return false;
		} else if (!getItemName().equals(i.getItemName())) {
			return false;
		}
		if (getClazz() == null) {
			if (i.getClazz() != null) {
				return false;
			}
		} else if (i.getClazz() == null) {
			return false;
		} else if (!getClazz().equals(i.getClazz())) {
			return false;
		}
		if (getValue() == null) {
			if (i.getValue() != null) {
				return false;
			}
		} else if (i.getValue() == null) {
			return false;
		} else if (!getValue().equals(i.getValue())) {
			return false;
		}
		return true;
	}
}