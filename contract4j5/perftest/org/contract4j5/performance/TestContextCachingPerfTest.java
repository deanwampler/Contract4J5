package org.contract4j5.performance;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;

import junit.framework.TestCase;

/** 
 * Test the TestContext caching. Do this by running with the
 * cache enabled then disabled. Cache puts are disabled globally 
 * using TestContextCache.disableCachePuts() (static method).
 * Turns out, the TestContext caching has minimal impact :(
 */
public class TestContextCachingPerfTest extends TestCase {
	@Contract @Invar("name != null") 
	class C {
		private String name = "";
		@Pre("arg.length() > 0")
		public void setName (String arg) { name = arg; }
		public String getName() { return name; }
		
		@Invar("count >= 0")
		private int count = 1;
		@Post("count > 4")
		public void setCount(int i) { count = i; }
		public int  getCount() { return count; }
		
		public C(String name, int count) {
			this.name = name;
			this.count = count;
		}
	}
	
	public void testPerfOfTestContext() {
		for (int i=0; i < 1000; i++) {
			C c = new C("c", i);
			assertEquals(i, c.getCount());
			assertEquals("c", c.getName());
		}
	}
}
