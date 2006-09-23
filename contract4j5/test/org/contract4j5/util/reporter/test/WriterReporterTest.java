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

package org.contract4j5.util.reporter.test;

import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

import org.contract4j5.reporter.Severity;
import org.contract4j5.reporter.WriterReporter;

public class WriterReporterTest extends TestCase {
	WriterReporter reporter;
	
	protected void setUp() throws Exception {
		super.setUp();
		reporter = new WriterReporter(Severity.INFO, new StringWriter());
	}

	public void testGetSetWriters() {
		for (Severity level: Severity.values()) {
			Writer sw = reporter.getWriter(level);
			reporter.setWriter(level, sw);
			assertSame ("Severity: "+level, sw, reporter.getWriter(level));
		}
	}
	
	public void testThreshold() {
		for (Severity level: Severity.values()) {
			reporter.setThreshold (level);
			assertTrue("Severity: " + level, reporter.getThreshold().compareTo(level) == 0);
		}
	}

	public void testSetThresholdUsingString() {
		try {
			reporter.setThresholdUsingString("bad");
			fail();
		} catch (IllegalArgumentException e) {
		}
		reporter.setThresholdUsingString("warn");
		reporter.setThresholdUsingString("WARN");
	}
	
	public void testThresholdCalls() {
		for (Severity level: Severity.values()) {
			reporter.setThreshold (level);
			for (Severity level2: Severity.values()) {
				StringWriter sw = (StringWriter) reporter.getWriter(level2);
				String old = sw.toString();	// snapshot of output
				reporter.report(level2, this.getClass(), 
						"test: threshold = "+reporter.getThreshold()+", level = "+level2);
				// Will the previous vs. new output be the same, because of the threshold?
				boolean same = reporter.getThreshold().compareTo(level2) > 0;	
				// Handle special case of "OFF", when no output is expected.
				if (level.equals(Severity.OFF)) {
					same = true;
				}
				assertSame ("Threshold, Severity: "+ reporter.getThreshold()+", "+level2+", expected same value? "+same+", old = \""+old+"\", new = \""+sw.toString()+"\"",
							same, old.equals(sw.toString()));
			}
		}
	}
}
