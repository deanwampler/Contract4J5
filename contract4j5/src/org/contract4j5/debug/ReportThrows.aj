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

package org.contract4j5.debug;

import org.contract4j5.controller.Contract4J;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.Severity;

/**
 * Report when an exception is thrown. This is most useful for debugging Contract4J
 * itself.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public aspect ReportThrows {

	after(Object o) throwing(Throwable th): 
		call (* org.contract4j..*.*(..)) && 
		!within (ReportThrows) &&
		this(o) {
		Class<?> clazz = o != null ? o.getClass() : null;
		Reporter r = getReporter();
		if (r != null) {
			r.report(Severity.ERROR, clazz, th.toString());
			th.printStackTrace();
		}
	}
	
	protected Reporter getReporter() {
		return Contract4J.getInstance().getReporter();
	}
}
