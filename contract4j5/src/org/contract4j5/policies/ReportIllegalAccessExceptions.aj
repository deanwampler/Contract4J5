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

package org.contract4j5.policies;

import org.contract4j5.aspects.Contract4J;
import org.contract4j5.util.reporter.Reporter;
import org.contract4j5.util.reporter.Severity;

/**
 * Report when {@link IllegalAccessException}'s are caught while doing reflection.
 * @note Ignore warning that this advice has not been applied, if present.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public aspect ReportIllegalAccessExceptions {
	before (IllegalAccessException iae) :
		handler (IllegalAccessException) && 
		within (org.contract4j..*) && 
		args (iae) {
		Reporter reporter = Contract4J.getReporter();
		if (reporter != null) {
			reporter.report(Severity.ERROR, Contract4J.class, iae.toString());
		}
	}
}
