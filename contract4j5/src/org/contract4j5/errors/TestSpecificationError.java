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
package org.contract4j5.errors;

/**
 * The special case of ContractError that is thrown when a test itself is 
 * invalid, <i>e.g.,</i> because the test expression was empty and no default
 * value could be inferred and the option to allow empty tests was false. This
 * error is also thrown if the expression interpreter fails to parse or 
 * evaluate the test expression.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class TestSpecificationError extends ContractError {
	private static final long serialVersionUID = -2721715704273663735L;

	public TestSpecificationError () {
		super();
	}
	public TestSpecificationError (String s) {
		super(s);
	}
	public TestSpecificationError (String s, Throwable t) {
		super(s, t);
	}
}
