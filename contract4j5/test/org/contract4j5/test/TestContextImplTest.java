package org.contract4j5.test;

import junit.framework.TestCase;

import org.contract4j5.Instance;
import org.contract4j5.TestContextImpl;

/**
 * Test {@link TestContextImpl}. Mostly, we test the nested 
 * {@link Instance} class.
 */
public class TestContextImplTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testInstanceCtor() {
		Instance i = new Instance();
		assertNull (i.getClazz());
		assertNull (i.getValue());
	}

	public void testInstanceCtorClassValue() {
		Instance i = new Instance("foo", String.class, new String("foo"));
		assertEquals (String.class, i.getClazz());
		assertEquals ("foo", i.getValue());
	}
	
	public void testToString() {
		Instance i = new Instance("foo", String.class, new String("foo"));
		assertEquals (i.toString(), "[name = foo, class = class java.lang.String, value = foo]", i.toString());
	}
	
	public void testEquals() {
		Instance i1 = new Instance("foo", String.class,  new String("foo"));
		Instance i2 = new Instance("foo", String.class,  new String("foo"));
		Instance i3 = new Instance("foo", String.class,  new String("bar"));
		Instance i4 = new Instance("bar", String.class,  new String("bar"));
		Instance i5 = new Instance("foo", Integer.class, new Integer(1));
		Instance i6 = new Instance();
		assertEquals (i1, i2);
		assertEquals (i2, i1);
		assertFalse  (i1.equals(i3));
		assertFalse  (i1.equals(i4));
		assertFalse  (i1.equals(i5));
		assertFalse  (i1.equals(i6));
		assertFalse  (i3.equals(i1));
		assertFalse  (i4.equals(i1));
		assertFalse  (i5.equals(i1));
		assertFalse  (i6.equals(i1));
		assertFalse  (i1.equals(null));
	}
}
