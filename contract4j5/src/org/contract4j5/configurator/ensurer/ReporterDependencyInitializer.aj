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

/**
 * Solves the problem of initializing a dependent throughout the
 * application where the same object should be used. This aspect
 * watches for reads of a {@link Reporter} instance and either
 * returns it or if null, returns the global object.
 */
public aspect ReporterDependencyInitializer {
	
	Reporter around() : get(Reporter org.contract4j5..*.*) &&
		!cflow(call(Reporter Contract4J.getReporter())) {
		Reporter reporter = proceed();
		return reporter == null ? Contract4J.getInstance().getReporter() : reporter;
	}

}
