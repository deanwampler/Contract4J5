/*
 * Copyright 2005 Dean Wampler. All rights reserved.
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

package org.contract4j.aspects;

import org.contract4j5.Invar;
import org.contract4j5.Post;
import org.contract4j5.Pre;
import org.contract4j5.aspects.Contract4J;

/**
 * Aspect to look for invalid usage of Contract4J.
 * @author Dean Wampler
 */
public aspect UsageEnforcement {
	pointcut noPre() : 
		! within (Contract4J.ContractMarker+) &&
		(execution (@Pre * *.*(..)) || execution (@Pre *.new(..)));

	pointcut noPost() : 
		! within (Contract4J.ContractMarker+) &&
		(execution (@Post * *.*(..)) || execution (@Post *.new(..)));
	
	pointcut noInvar() : 
		! within (Contract4J.ContractMarker+) &&
		(execution (@Invar * *.*(..)) || execution (@Invar *.new(..)) ||
		 get (@Invar * *) || set (@Invar * *));

	declare warning: noPre(): 
		"The @Pre Contract4J annotation requires the class annotation @Contract";
	declare warning: noPost(): 
		"The @Post Contract4J annotation requires the class annotation @Contract";
	declare warning: noInvar(): 
		"The @Invar Contract4J annotation requires the class annotation @Contract";
}
