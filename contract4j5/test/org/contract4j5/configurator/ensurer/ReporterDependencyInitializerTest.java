/*
 * Copyright 2005, 2006 Dean Wampler. All rights reserved.
 * http://www.contract4j.org
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
package org.contract4j5.configurator.ensurer;

import org.contract4j5.controller.Contract4J;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.Severity;

import junit.framework.TestCase;

public class ReporterDependencyInitializerTest extends TestCase {
	private StubReporter stubReporter;
	static class StubReporter implements Reporter {
		public Severity getThreshold() { return null; }
		StringBuffer buff = new StringBuffer();
		public void report(Severity level, Class<?> clazz, String message) {
			buff.append(message+"\t");
		}
		public void setThreshold(Severity level) {}

		public void setThresholdUsingString(String level)
				throws IllegalArgumentException {}
	}
	
	static class ObjectWithUninitializedReporter {
		private Reporter reporter;

		public ObjectWithUninitializedReporter (String msg) {
			reporter.report(Severity.ERROR, this.getClass(), msg);
		}
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		stubReporter = new StubReporter();
		Contract4J.getInstance().setReporter(stubReporter);
	}
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		Contract4J.getInstance().setReporter(null);  // unset for next test(?)
	}
	
	public void testObjectWithUninitializedReporterUsesContract4JReporter() {
		new ObjectWithUninitializedReporter("hello");
		assertEquals("hello\t", stubReporter.buff.toString());
	}
}
