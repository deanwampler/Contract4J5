package org.contract4j5.aspects.test;

import junit.framework.TestCase;

import org.contract4j5.Invar;
import org.contract4j5.Post;
import org.contract4j5.Pre;

public class UsageTest extends TestCase {
	@SuppressWarnings("The @Pre, @Post, and @Invar Contract4J annotations require the class annotation @Contract")
	public static class MissingContractAnno {
		@Pre ("str != null")
		public void foo(String str) { name = str; }
		@Invar
		private String name;
		@Post("name != null")
		public MissingContractAnno (String name) {
			this.name = name;
		}
	}
	
	MissingContractAnno mca = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		mca = new MissingContractAnno("mca");
	}

	public void test1() {
		assertNotNull (mca);
	}
	
	public void test2() {
		mca = new MissingContractAnno(null);
		assertNotNull(mca);
		mca.foo(null);
	}
}
