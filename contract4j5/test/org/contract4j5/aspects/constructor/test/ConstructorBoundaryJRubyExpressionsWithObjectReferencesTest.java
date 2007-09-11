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
 * Exercise test expressions with class and object references, for JRuby.
 */
public class ConstructorBoundaryJRubyExpressionsWithObjectReferencesTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		new ConfiguratorForTesting().configure();
		Validator.setCalled(false);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Contract
	static class PreCtor1ForJRuby {
		// Normally, you could do the following static block. That doesn't work in this test, because
		// the test's setUp is called after this block and it changes the interpreter!
//		static {
//			Contract4J.getInstance().registerGlobalContextObject("Validator", Validator.class);
//		}
		@Pre("$Validator.valid(name)")
		public PreCtor1ForJRuby(String name) {}
	}
	
	public void testJRubyConstructorPreconditionTestCannotReferenceStaticMethodsOnOtherClassesThatAreRegistered() {
		if (!SystemUtils.isJRuby()) 
			return;
		Contract4J.getInstance().registerGlobalContextObject("Validator", Validator.class);
		try {
			new PreCtor1ForJRuby("foo");
			fail();
		} catch (ContractError ce) {
		} finally {
			Contract4J.getInstance().unregisterGlobalContextObject("Validator");
		}
	}

	@Contract
	static class PreCtor2 {
		@Pre("org.contract4j5.aspects.constructor.test.Validator.valid(name)")
		public PreCtor2(String name) {}
	}
	
	/** 
	 * As for Jexl (but not Groovy), can use a fully-qualified name to a class with static methods.
	 */
	public void testJRubyConstructorPreconditionTestCanReferenceStaticMethodsOnOtherFullQualifiedClasses() {
		if (!SystemUtils.isJRuby()) 
			return;
		assertFalse(Validator.called);
		new PreCtor2("foo");
		assertTrue(Validator.called);
	}

	static KnownColorValidator colorValidator = new KnownColorValidator();
	
	@Contract
	static class PreCtor3 {
		// Normally, you could do the following static block. That doesn't work in this test, because
		// the test's setUp is called after this block and it changes the interpreter!
		// Note that you have to register the object without a "$", but the test has to include the "$".
//		static {
//			Contract4J.getInstance().registerGlobalContextObject("colorValidator", colorValidator);
//		}
		@Pre("$colorValidator.valid(color)")
		public PreCtor3(KnownColor color) {}
	}
	
	public void testJRubyConstructorPreconditionTestCanReferencePreregisteredObjects() {
		if (!SystemUtils.isJRuby()) 
			return;
		assertFalse(colorValidator.called);
		Contract4J.getInstance().registerGlobalContextObject("colorValidator", colorValidator);
		assertNotNull(Contract4J.getInstance().getContractEnforcer().getExpressionInterpreter().getObjectInContext("colorValidator"));
		new PreCtor3(KnownColor.BLUE);
		assertTrue(colorValidator.called);
		Contract4J.getInstance().unregisterGlobalContextObject("colorValidator");
	}

	@Contract
	static class PostCtor1 {
		public static KnownColorValidator colorValidator2 = new KnownColorValidator();
		@Post("colorValidator2.valid(color)")
		public PostCtor1(KnownColor color) {}
	}
	
	public void testJRubyDoesNotConvertBareStaticFieldReferenceIntoGetter() {
		if (!SystemUtils.isJRuby()) 
			return;
		try {
			new PostCtor1(KnownColor.BLUE);
			fail();
		} catch (ContractError ce) {}
	}

	@Contract
	static class PostCtor2 {
		public static KnownColorValidator colorValidator2 = new KnownColorValidator();
		@Post("$this.colorValidator2.valid(color)")
		public PostCtor2(KnownColor color) {}
	}
	
	public void testJRubyDoesNotConvertBareStaticFieldReferenceOnDollarThisIntoGetter() {
		if (!SystemUtils.isJRuby()) 
			return;
		try {
			new PostCtor2(KnownColor.BLUE);
			fail();
		} catch (ContractError ce) {}
	}
	
	@Contract
	static class PostCtor3 {
		public static KnownColorValidator colorValidator2b = new KnownColorValidator();
		public static KnownColorValidator colorValidator2() { return colorValidator2b; }
		@Post("$this.colorValidator2.valid(color)")
		public PostCtor3(KnownColor color) {}
	}
	
	public void testJRubyDoesNotAllowStaticMethodCallOnDollarThis() {
		if (!SystemUtils.isJRuby()) 
			return;
		try {
			new PostCtor3(KnownColor.BLUE);
			fail();
		} catch (ContractError ce) {}
	}
	
	@Contract
	static class PostCtor4 {
		public static KnownColorValidator colorValidator2b = new KnownColorValidator();
		public        KnownColorValidator colorValidator2() { return colorValidator2b; }
		@Post("$this.colorValidator2.valid(color)")
		public PostCtor4(KnownColor color) {}
	}
	
	public void testJRubyCanOnlyReferenceSameClassStaticObjectsThroughInstanceMethodOnDollarThis() {
		if (!SystemUtils.isJRuby()) 
			return;
		assertFalse(PostCtor4.colorValidator2b.called);
		new PostCtor4(KnownColor.BLUE);
		assertTrue(PostCtor4.colorValidator2b.called);
	}
	
}