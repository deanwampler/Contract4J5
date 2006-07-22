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

package org.contract4j5.util.reporter;

/**
 * The "level" of severity of an "event", mapped roughly to what you would
 * expect for various 3rd-party logging toolkits, for example. Note that 
 * {@link #OFF} turns off reporting completely.
 */
public enum Severity {
	DEBUG, INFO, WARN, ERROR, FATAL, OFF;

	public static Severity parse(String str) {
		if (str == null || str.length() == 0)
			return null;
		for (Severity s: Severity.values()) {
			if (str.toUpperCase().equals(s.name()))
				return s;
		}
		return null;
	}
}
	
