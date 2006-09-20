package org.contract4j5.test;

import org.contract4j5.Contract;
import org.contract4j5.Contract4J;
import org.contract4j5.ContractError;
import org.contract4j5.Post;
import org.contract4j5.Pre;
import org.contract4j5.TestSpecificationError;

import junit.framework.TestCase;

public class Eclipse153490BugTest extends TestCase {

	@Contract
	public static class Foo {

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
		Contract4J c4j = new Contract4J();
		c4j.setEnabled(Contract4J.TestType.Pre,   true); //1
		c4j.setEnabled(Contract4J.TestType.Post,  true); //2 
		c4j.setEnabled(Contract4J.TestType.Invar, true); //3

		Foo foo = new Foo();
		try {
			foo.setFooField(null);
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			// expected
		}
		try {
			System.out.println(foo.getFooField());
			fail();
		} catch (TestSpecificationError tse) {
			fail();
		} catch (ContractError ce) {
			// expected
		}
	}	
}