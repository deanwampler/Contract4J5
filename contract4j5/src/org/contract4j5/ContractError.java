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

package org.contract4j5;

/**
 * ContractError is an <i>unchecked</i> Error thrown when 
 * contract tests fail.
 * @author Dean Wampler  <mailto:dean@aspectprogramming.com>
 */
public class ContractError extends Error {
	private static final long serialVersionUID = -7010022716043092608L;

	public ContractError () {
		super();
	}
	
	public ContractError (String s) {
		super(s);
	}

	public ContractError (String s, Throwable t) {
		super(s, t);
	}
}
