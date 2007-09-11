package org.contract4j5.aspects.constructor.test;

import junit.framework.TestCase;

import org.contract4j5.configurator.test.ConfiguratorForTesting;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.errors.ContractError;
import org.contract4j5.util.SystemUtils;

/**
 * Exercise test expressions with class and object references, for Jexl.
 */
public class ConstructorBoundaryJexlExpressionsWithObjectReferencesTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (!SystemUtils.isJexl()) 
			return;
		new ConfiguratorForTesting().configure();
		Validator.setCalled(false);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (!SystemUtils.isJexl()) 
			return;
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
	
	public void testJexlConstructorPreconditionTestCanReferenceStaticMethodsOnOtherClassesThatAreRegistered() {
		Contract4J.getInstance().registerGlobalContextObject("Validator", Validator.class);
		assertFalse(Validator.called);
		new PreCtor1("foo");
		assertTrue(Validator.called);
	}

	@Contract
	static class PreCtor2 {
		@Pre("org.contract4j5.aspects.constructor.test.Validator.valid(name)")
		public PreCtor2(String name) {}
	}
	
	/** 
	 * With Jexl and JRuby, you can use a fully-qualified name to a class with static methods.
	 * Unfortunately, Groovy assumes they are deeply-nested property queries!
	 */
	public void testJexlConstructorPreconditionTestCanReferenceStaticMethodsOnOtherFullQualifiedClasses() {
		if (!SystemUtils.isJexl()) 
			return;
		assertFalse(Validator.called);
		new PreCtor2("foo");
		assertTrue(Validator.called);
	}

	@Contract
	static class PreCtor2b {
		@Pre("Validator.valid(name)")
		public PreCtor2b(String name) {}
	}
	
	/** 
	 * With Jexl, you can use static methods on a class in the same package.
	 * Unfortunately, Groovy assumes they are deeply-nested property queries!
	 */
	public void testJexlConstructorPreconditionTestCanReferenceStaticMethodsOnOtherClassesInSamePackage() {
		if (!SystemUtils.isJexl()) 
			return;
		assertFalse(Validator.called);
		new PreCtor2b("foo");
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
	
	public void testJexlConstructorPreconditionTestCannotReferencePreregisteredObjects() {
		if (!SystemUtils.isJexl()) 
			return;
		assertFalse(colorValidator.called);
		Contract4J.getInstance().registerGlobalContextObject("colorValidator", colorValidator);
		assertNotNull(Contract4J.getInstance().getContractEnforcer().getExpressionInterpreter().getObjectInContext("colorValidator"));
		try {
			new PreCtor3(Color.BLUE);
			fail();
		} catch (ContractError ce) {
		} finally {
			Contract4J.getInstance().unregisterGlobalContextObject("colorValidator");
		}
	}

	@Contract
	static class PreCtor4 {
		public static ColorValidator colorValidator2 = new ColorValidator();
		@Post("$this.colorValidator2.valid(color)")
		public PreCtor4(Color color) {}
	}
	
	public void testJexlConstructorPreconditionTestCannotReferenceSameClassStaticObjectsWithDollarThis() {
		if (!SystemUtils.isJexl()) 
			return;
		try {
			new PreCtor4(Color.BLUE);
			fail();
		} catch (ContractError ce) {}
	}
	
	@Contract
	static class PostCtor1 {
		public static ColorValidator colorValidator2 = new ColorValidator();
//		public  ColorValidator getColorValidator2() { return colorValidator2; }
		@Post("$this.colorValidator2.valid(color)")
		public PostCtor1(Color color) {}
	}
	
	public void testJexlConstructorPostconditionTestCannotReferenceSameClassStaticObjectsWithDollarThis() {
		if (!SystemUtils.isJexl()) 
			return;
		try {
			new PostCtor1(Color.BLUE);
			fail();
		} catch (ContractError ce) {}
	}
	
	@Contract
	static class PostCtor2 {
		public static boolean valid = false;
		@Post("$this.valid")
		public PostCtor2(Color color) { valid = true; }
	}
	
	public void testJexlConstructorPostconditionTestCanReferenceSameClassStaticFieldWithoutMethodCallIfDollarThisIsUsed() {
		if (!SystemUtils.isJexl())
			return;
		try {
			new PostCtor2(Color.BLUE);
			fail();
		} catch (ContractError ce) {}
	}
}