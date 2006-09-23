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

package org.contract4j5.reporter;

/**
 * Reporter is a simple interface that abstracts logging-style calls to output 
 * streams and loggers, etc. It is used so that Contract4J has minimal coupling 
 * to specific toolkits while providing flexibility to handle output as end users 
 * see fit. Specific implementations must direct the output appropriately, e.g.,
 * to System.out, log4j, etc. 
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public interface Reporter {
	/**
	 * "Report" a message at a particular level of severity. Do nothing if the
	 * "level" is below the value returned by {@link #getThreshold()}.
	 * @param level severity of the message, one of {@link Severity}
	 * @param clazz the Class of the caller
	 * @param message to report; if "" or null, an empty message is reported.
	 * TODO refactor to remove the "clazz", which is inefficient and is error prone.
	 */
	void report(Severity level, Class clazz, String message);

	/**
	 * Get the threshold for reporting messages. Only those at or above the
	 * threshold will actually get reported.
	 * @return the threshold level
	 */
	Severity getThreshold();

	/**
	 * Set the threshold for reporting messages. Only those at or above the
	 * threshold will actually get reported.
	 * @param level for the threshold
	 */
	void setThreshold(Severity level);

	/**
	 * Set the threshold for reporting messages using a string. 
	 * @param level string representing the threshold
	 * @throws IllegalArgumentException if the string is invalid
	 */
	void setThresholdUsingString(String level) throws IllegalArgumentException;
}
