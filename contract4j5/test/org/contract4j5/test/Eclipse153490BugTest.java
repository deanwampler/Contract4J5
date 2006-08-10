package org.contract4j5.test;

import org.contract4j5.Contract;
import org.contract4j5.Post;
import org.contract4j5.Pre;
import org.contract4j5.aspects.Contract4J;

import junit.framework.TestCase;

public class Eclipse153490BugTest extends TestCase {
	
	@Contract
	public class Foo {
	
	  private String fooField = null;
	
	  @Pre("nr != null")
	  public void setFooField(String f) {
	    fooField = f; 
	  }
	
	  @Post("$return != null")
	  public String getFooField() {
	    return fooField;
	  }
	}

	public static void main(String[] args) {
	  new Eclipse153490BugTest().testFoo();
	}
	
	public void testFoo() {
	  
	  Contract4J.setEnabled(Contract4J.TestType.Pre,   true); //1
	  Contract4J.setEnabled(Contract4J.TestType.Post,  true); //2 
	  Contract4J.setEnabled(Contract4J.TestType.Invar, true); //3
	
	  Foo foo = new Foo();
	  foo.setFooField(null);
	  System.out.println(foo.getFooField());
	}	
}