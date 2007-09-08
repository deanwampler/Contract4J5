package org.contract4j5.aspects.constructor.test;

import junit.framework.TestCase;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.controller.Contract4J;

/**
 * Exercise test expressions with class and object references.
 */
public class ConstructorBoundaryExpressionsWithObjectReferencesTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Validator.setCalled(false);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		Contract4J.getInstance().unregisterGlobalContextObject("Validator");
	}
	
	@Contract
	static class PreCtor1 {
		static {
			Contract4J.getInstance().registerGlobalContextObject("Validator", Validator.class);
		}
		@Pre("Validator.valid(name)")
		public PreCtor1(String name) {}
	}
	
	public void testConstructorPreconditionTestCanReferenceStaticMethodsOnOtherClassesThatAreRegistered() {
		assertFalse(Validator.called);
		new PreCtor1("foo");
		assertTrue(Validator.called);
	}

	@Contract
	static class PreCtor2 {
		@Pre("org.contract4j5.aspects.constructor.test.Validator.valid(name)")
		public PreCtor2(String name) {}
	}
	
	public void testConstructorPreconditionTestCanReferenceStaticMethodsOnOtherFullQualifiedClasses() {
		assertFalse(Validator.called);
		new PreCtor2("foo");
		assertTrue(Validator.called);
		Contract4J.getInstance().unregisterGlobalContextObject("Validator");
	}

	@Contract
	static class PreCtor2b {
		@Pre("Validator.valid(name)")
		public PreCtor2b(String name) {}
	}
	
	public void testConstructorPreconditionTestCanReferenceStaticMethodsOnOtherClassesInSamePackage() {
		assertFalse(Validator.called);
		new PreCtor2b("foo");
		assertTrue(Validator.called);
		Contract4J.getInstance().unregisterGlobalContextObject("Validator");
	}

	enum Color { RED, GREEN, BLUE }
	static ColorValidator colorValidator = new ColorValidator();
	
	@Contract
	static class PreCtor3 {
		static {
			Contract4J.getInstance().registerGlobalContextObject("colorValidator", colorValidator);
		}
		@Pre("colorValidator.valid(color)")
		public PreCtor3(Color color) {}
	}
	
	public void testConstructorPreconditionTestCanReferencePreregisteredObjects() {
		assertFalse(colorValidator.called);
		new PreCtor3(Color.BLUE);
		assertTrue(colorValidator.called);
		Contract4J.getInstance().unregisterGlobalContextObject("Validator");
	}

	@Contract
	static class PreCtor4 {
		public static ColorValidator colorValidator2 = new ColorValidator();
//		public  ColorValidator getColorValidator2() { return colorValidator2; }
		@Post("$this.colorValidator2.valid(color)")
		public PreCtor4(Color color) {}
	}
	
	public void testConstructorPreconditionTestCanReferenceSameClassStaticObjectsWithDollarthis() {
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
	
	public void testConstructorPostconditionTestCanReferenceSameClassStaticObjectsIfDollarThisIsUsed() {
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
	
	public void testConstructorPostconditionTestCanReferenceSameClassStaticFieldWithoutMethodCallIfDollarThisIsUsed() {
		assertFalse(PostCtor2.valid);
		new PostCtor2(Color.BLUE);
		assertTrue(PostCtor2.valid);
	}
}
