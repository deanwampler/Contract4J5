package org.contract4j5.context.test;

import org.contract4j5.context.TestContextImpl;
import org.contract4j5.instance.Instance;

import junit.framework.TestCase;

public class TestContextTest extends TestCase {
	class ContextObject {
		public String name = "name";
		public void setName(String name) { this.name = name; }
	}
	
	public void testToString() {
		ContextObject contextObject = new ContextObject();
		Instance instance = new Instance("item", ContextObject.class, contextObject); 
		Instance field    = new Instance("name", String.class, contextObject.name); 
		Instance argInstance = new Instance("name", String.class, "new name");
		Instance[] methodArgs = new Instance[] { argInstance };
		Instance   methodResult = null;
		TestContextImpl context = 
			new TestContextImpl("a==b", "context object", instance, field, methodArgs, methodResult, "file.java", 100);
		String string = context.toString();
		assertTrue(string.contains("context object"));
		assertTrue(string.contains("item"));
		assertTrue(string.contains("name"));
	}
}
