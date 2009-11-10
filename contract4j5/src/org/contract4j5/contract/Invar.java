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
package org.contract4j5.contract;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that supports Design by Contract "invariant" tests.
 * A class annotated with invar will have the test performed before and
 * after <i>all</i> public and protected method invocations, and after 
 * constructor invocations. An invariant test on a method, including constructors,
 * will be evaluated before and after the method execution. In contrast, a field
 * with an invariant test is equivalent to a postcondition test on the return 
 * value of a corresponding "getter" method; both are evaluated after the join 
 * point, so that lazy initialization of the field can be done. 
 * <br>The {@link #value} String must be a valid Java or AspectJ expression
 * evaluating to true or false at runtime.
 * @author Dean Wampler  <mailto:dean@aspectprogramming.com>
 * @note The class must be annotated with @Contract
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD, 
		 ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface Invar {
    /**
     * The "value" is the test expression, which must be a legal Java or AspectJ
     * expression that evaluates to true or false.
     * The default value here is "" as there is no convenient default suitable
     * for all cases. Instead subclasses of 
     * {@link org.contract4j5.testexpression.DefaultTestExpressionMaker}
     * are used by the aspects to dynamically generate suitable defaults,
     * when possible.
     */
    String value() default "";

    /**
     * An optional message to print with the standard message when the contract
     * fails.
     */
    String message() default "";

	/**
	 * When to run the contracts enclosed in this type.
	 */
	RunFlag run() default RunFlag.ALWAYS;
}
