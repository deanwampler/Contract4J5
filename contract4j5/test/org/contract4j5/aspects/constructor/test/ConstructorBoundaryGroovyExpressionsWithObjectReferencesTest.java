package org.contract4j5.aspects.constructor.test;

import junit.framework.TestCase;

import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.util.SystemUtils;

/**
 * Exercise test expressions with class and object references, for Groovy.
 */
public class ConstructorBoundaryGroovyExpressionsWithObjectReferencesTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (SystemUtils.isGroovy()) {
			new ConfiguratorForTesting().configure();
			Validator.setCalled(false);
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (SystemUtils.isGroovy()) 
			Contract4J.getInstance().unregisterGlobalContextObject("Validator");
	}
	
	@Contract
	static class PreCtor1 {
		// Normally, you could do the following static block. That doesn't work in this test, because
		// the test's setUp is called after this block and it changes the interpreter!
//		static {
//			Contract4J.getInstance().registerGlobalContextObject("Validator", Validator.class);
//		}
		@Pre("Validator.valid(name)")
		public PreCtor1(String name) {}
	}
	
	public void testGroovyConstructorPreconditionTestCanReferenceStaticMethodsOnOtherClassesThatAreRegistered() {
		if (!SystemUtils.isGroovy()) 
			return;
		Contract4J.getInstance().registerGlobalContextObject("Validator", Validator.class);
		assertFalse(Validator.called);
		new PreCtor1("foo");
		assertTrue(Validator.called);
	}

	@Contract
	static class PreCtor2 {
		@Pre("Validator.valid(name)")
		public PreCtor2(String name) {}
	}
	
	public void testGroovyConstructorPreconditionTestCanReferenceStaticMethodsOnOtherClassesInSamePackage() {
		if (!SystemUtils.isGroovy()) 
			return;
		assertFalse(Validator.called);
		new PreCtor2("foo");
		assertTrue(Validator.called);
	}

	enum Color { RED, GREEN, BLUE }
	static ColorValidator colorValidator = new ColorValidator();
	
	@Contract
	static class PreCtor3 {
		// Normally, you could do the following static block. That doesn't work in this test, because
		// the test's setUp is called after this block and it changes the interpreter!
//		static {
//			Contract4J.getInstance().registerGlobalContextObject("colorValidator", colorValidator);
//		}
		@Pre("colorValidator.valid(color)")
		public PreCtor3(Color color) {}
	}
	
	public void testGroovyConstructorPreconditionTestCanReferencePreregisteredObjects() {
		if (!SystemUtils.isGroovy()) 
			return;
		assertFalse(colorValidator.called);
		Contract4J.getInstance().registerGlobalContextObject("colorValidator", colorValidator);
		assertNotNull(Contract4J.getInstance().getContractEnforcer().getExpressionInterpreter().getObjectInContext("colorValidator"));
		new PreCtor3(Color.BLUE);
		assertTrue(colorValidator.called);
	}

	@Contract
	static class PreCtor4 {
		public static ColorValidator colorValidator2 = new ColorValidator();
		@Post("$this.colorValidator2.valid(color)")
		public PreCtor4(Color color) {}
	}
	
	public void testGroovyConstructorPreconditionTestCanReferenceSameClassStaticObjectsWithDollarThis() {
		if (!SystemUtils.isGroovy()) 
			return;
		assertFalse(PreCtor4.colorValidator2.called);
		new PreCtor4(Color.BLUE);
		assertTrue(PreCtor4.colorValidator2.called);
	}
	
	@Contract
	static class PostCtor1 {
		public static ColorValidator colorValidator2 = new ColorValidator();
//		public  ColorValidator getColorValidator2() { return colorValidator2; }
		@Post("$this.colorValidator2.valid(color)")
		public PostCtor1(Color color) {}
	}
	
	public void testGroovyConstructorPostconditionTestCanReferenceSameClassStaticObjectsWithDollarThis() {
		if (!SystemUtils.isGroovy()) 
			return;
		assertFalse(PostCtor1.colorValidator2.called);
		new PostCtor1(Color.BLUE);
		assertTrue(PostCtor1.colorValidator2.called);
	}
	
	@Contract
	static class PostCtor2 {
		public static boolean valid = false;
		@Post("$this.valid")
		public PostCtor2(Color color) { valid = true; }
	}
	
	public void testGroovyConstructorPostconditionTestCanReferenceSameClassStaticFieldWithoutMethodCallIfDollarThisIsUsed() {
		if (!SystemUtils.isGroovy()) 
			return;
		assertFalse(PostCtor2.valid);
		new PostCtor2(Color.BLUE);
		assertTrue(PostCtor2.valid);
	}
}