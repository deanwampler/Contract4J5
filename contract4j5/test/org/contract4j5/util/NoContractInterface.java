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

package org.contract4j5.util;

import org.contract4j5.contract.Invar;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;

/**
 * Interface that declares some contracts, but fails to include the required
 * "@Contract" annotation. Expect warnings to be generated.
 */
@Invar ("$this.getFlag() > 0")
public interface NoContractInterface extends ExampleBaseInterface {
	@Pre   void setName (String s);
	@Post  String getName ();
	@Invar("$this.getName().length() > 0") 
	@Post("!$this.getName.equals(\"bad\")") 
	void m (String s);
}
