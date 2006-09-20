/*
 * Copyright 2005 2006 Dean Wampler. All rights reserved.
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

package org.contract4j5.util.reporter;


/**
 * Uses the "Null Object Pattern" to define reporter that does nothing. 
 * This object is primarily useful for eliminating the need for 
 * clients to always test for null.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class NullReporter implements Reporter {

	public void report(Severity level, Class clazz, String message) {}
	public Severity getThreshold() {
		return Severity.OFF;
	}
	public void setThreshold(Severity level) {}
	public void setThresholdUsingString(String level) throws IllegalArgumentException {
	}
}
