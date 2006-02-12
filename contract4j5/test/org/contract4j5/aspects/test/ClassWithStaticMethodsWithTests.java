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

package org.contract4j5.aspects.test;

import org.contract4j5.Contract;
import org.contract4j5.Invar;
import org.contract4j5.Post;
import org.contract4j5.Pre;

/**
 * Class with static methods that declare tests. The compiler should warn that
 * this isn't supported. You can ignore those warnings!
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
@Contract
public class ClassWithStaticMethodsWithTests {
	@Pre
	public static void staticMethodWithPre() {}
	@Post
	public static void staticMethodWithPost() {}
	@Invar
	public static void staticMethodWithInvar() {}
}