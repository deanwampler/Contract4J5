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
package org.contract4j5.configurator;

public interface Configurator {
	public class ConfigurationFailedException extends RuntimeException {
		private static final long serialVersionUID = 8190835362858941492L;

		public ConfigurationFailedException(String message) { super(message); }
		public ConfigurationFailedException(Throwable th)   { super(th); }
		public ConfigurationFailedException(String message, Throwable th) { super(message, th); }
	}
	
	void configure() throws ConfigurationFailedException;
	void configureWithInterpreter(String whichInterpreter) throws ConfigurationFailedException;
}
