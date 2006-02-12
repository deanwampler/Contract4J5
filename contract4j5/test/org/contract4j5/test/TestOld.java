/*
 * Copyright 2005, 2006 Dean Wampler. All rights reserved.
 * http://www.aspectprogramming.com
 *
 * Licensed under the Eclipse Public License - v 1.0; you may not use this
 * software except in compliance with the License. You may obtain a copy of the 
 * License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * A copy is also included with this distribution. See the "LICENSE" file.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */

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
