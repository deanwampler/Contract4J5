package org.contract4j5.interpreter.test;

import org.contract4j5.configurator.Configurator;
import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.errors.ContractError;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.instance.Instance;
import org.contract4j5.interpreter.ExpressionInterpreterHelper;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.interpreter.bsf.groovy.GroovyBSFExpressionInterpreter;

import junit.framework.TestCase;

/**
 * Tests that you can reference other objects and class-static fields and methods in test expressions.
 */
public class ExpressionsWithOtherObjectsTest extends TestCase {
	static enum Color { RED, GREEN, BLUE }
	@Contract
	static class ColorMap {
		static Color map(String name) { 
			return Color.valueOf(name);
		}
		@Post("$return == Color.BLUE")
		Color getBlue() { return Color.RED; }
	}
	@Contract
	static class ColorUser {
		private Color color;
		public Color getColor() { return color; }
		@Pre
		public ColorUser(String colorName) {
			color = ColorMap.map(colorName);
		}
	}
	
	ExpressionInterpreterHelper interpreter = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		Configurator c = new ConfiguratorForTesting();
		c.configure();
		interpreter = new GroovyBSFExpressionInterpreter();
	}

	public static class TestClass {
		public static boolean validate() { return true; }
		public boolean valid() { return true; }
	}
	
	public void testStaticReferencesWhenExplicitlyRegisteringClass() {
		TestContext testContext = new TestContextImpl("org.contract4j5.interpreter.test.ExpressionsWithOtherObjectsTest.TestClass.validate() == true", "", null, null, new Instance[0], null, "", 0);
		interpreter.registerContextObject("org.contract4j5.interpreter.test.ExpressionsWithOtherObjectsTest.TestClass", TestClass.class);
		TestResult result = interpreter.invokeTest("org.contract4j5.interpreter.test.ExpressionsWithOtherObjectsTest.TestClass.validate() == true", testContext);
		assertTrue(result.isPassed());		
	}
	
	public void testStaticReferencesWhenNotRegisteringClassButUsingItsFullyQualifiedNameInstead() {
		TestContext testContext = new TestContextImpl("TestClass.validate() == true", "", null, null, new Instance[0], null, "", 0);
		interpreter.registerContextObject("TestClass", TestClass.class);
		TestResult result = interpreter.invokeTest("TestClass.validate() == true", testContext);
		assertTrue(result.isPassed());		
	}
	
	public void testReferenceToObjectWhenExplicitlyRegisteringObject() {
		TestContext testContext = new TestContextImpl("obj.valid() == true", "", null, null, new Instance[0], null, "", 0);
		interpreter.registerContextObject("obj", new TestClass());
		TestResult result = interpreter.invokeTest("obj.valid() == true", testContext);
		assertTrue(result.isPassed());		
	}
	
	public void testReferenceStaticMethodOnObjectWhenExplicitlyRegisteringObject() {
		TestContext testContext = new TestContextImpl("obj.validate() == true", "", null, null, new Instance[0], null, "", 0);
		interpreter.registerContextObject("obj", new TestClass());
		TestResult result = interpreter.invokeTest("obj.validate() == true", testContext);
		assertTrue(result.isPassed());		
	}
	
}
