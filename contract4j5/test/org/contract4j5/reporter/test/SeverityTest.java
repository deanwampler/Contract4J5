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
package org.contract4j5.reporter.test;

import org.contract4j5.reporter.Severity;

import junit.framework.TestCase;

public class SeverityTest extends TestCase {

	public void testParseGood() {
		for (Severity s: Severity.values()) {
			String name = s.name();
			Severity s2 = Severity.parse(s.name());
			assertEquals(name, s, s2);
			Severity s3 = Severity.parse(s.name().toLowerCase());
			assertEquals(name.toLowerCase(), s, s3);
		}
	}
	public void testParseBad() {
		assertNull(Severity.parse(null));
		assertNull(Severity.parse(""));
		assertNull(Severity.parse("unknown"));
	}
}
