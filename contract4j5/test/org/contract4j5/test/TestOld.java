package org.contract4j5.test;

import junit.framework.TestCase;

import org.contract4j5.Contract;
import org.contract4j5.ContractError;
import org.contract4j5.Post;

public class TestOld extends TestCase {
	@Contract
	public static class Point {
		private int x = 0;
		private int y = 0;
		public int getX() { return x; }
		public int getY() { return y; }
		// Use "args[0]" in the test.
		@Post("$this.x == $old($this.x)+$args[0] && $this.y == $old($this.y)")
		public int moveX(int dx) { x += dx; return x; }
		// Use "dy" in the test.
		@Post("$this.y == $old($this.y)+dy && $this.x == $old($this.x)")
		public int moveY(int dy) { y += dy; return y; }
		public Point(int x, int y) { 
			this.x = x;
			this.y = y;
		}
	}
	
	private Point p = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ManualSetup.wireC4J();
		p = new Point (0,0);
	}
	
	public void testMoveX() {
		assertEquals(0,  p.getX());
		assertEquals(0,  p.getY());
		try {
			p.moveX(10);
		} catch (ContractError ce) {
			fail();
		}
		assertEquals(10, p.getX());
		assertEquals(0,  p.getY());
	}
	
	public void testMoveY() {
		assertEquals(0,  p.getX());
		assertEquals(0,  p.getY());
		try {
			p.moveY(20);
		} catch (ContractError ce) {
			fail();
		}
		assertEquals(0,  p.getX());
		assertEquals(20, p.getY());
	}
}
