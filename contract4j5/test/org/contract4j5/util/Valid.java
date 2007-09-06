/**
 * 
 */
package org.contract4j5.util;

public class Valid implements Validatable {
	public String name;
	public Valid(String name) { 
		this.name = name; 
	}
	public boolean valid() {
		return name != null && name.length() > 0;
	}
}