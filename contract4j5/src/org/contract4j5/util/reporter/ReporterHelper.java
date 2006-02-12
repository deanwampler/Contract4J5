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
 * Helper class that implements some of the {@link Reporter} methods.
 */
public abstract class ReporterHelper implements Reporter {

	public void report(Severity level, Class clazz, String message) {
		Severity threshold = getThreshold();
		if (threshold == Severity.OFF || level.compareTo(threshold) < 0) {
			return;
		}
		if (message == null) {
			message = "";
		}
		reportSupport (level, clazz, message);
	}

	/**
	 * Abstract support method implemented by derived classes to do the
	 * actual reporting. See {@link Reporter#report(Severity,Class,String)} for expected
	 * behavior, which is the only method that calls this method
	 * if the level equals or exceeds the threshold, so implementers don't need to check
	 * the threshold. Also, that method converts a null "message" to an empty string. 
	 * @param level the {@link Severity} of severity.
	 * @param message a non-null message.
	 */
	protected abstract void reportSupport(Severity level, Class clazz, String message);

	private Severity threshold;
	
	/**
	 * @return threshold for reporting messages. Only those at or above the threshold will
	 * actually get reported.
	 */
	public Severity getThreshold () {
		return threshold;
	}

	/**
	 * Set the threshold for reporting messages. Only those at or above the threshold will
	 * actually get reported.
	 * @param level Level
	 */
	public void setThreshold (Severity level) {
		threshold = level;
	}
	
	/**
	 * Default constructor. Sets the threshold to {@link Severity#Warn}.
	 */
	public ReporterHelper () {
		setThreshold (Severity.WARN);
	}
	
	/**
	 * Constructor.
	 * @param threshold for messages.
	 *
	 */
	public ReporterHelper (Severity threshold) {
		setThreshold (threshold);
	}
}
