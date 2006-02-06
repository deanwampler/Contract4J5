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
 * @author deanwampler <dean@aspectprogramming.com>
 */
package org.contract4j5;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that supports Design by Contract "invariant" tests.
 * The tests are generated in AspectJ. Classes that use these tests must be
 * declared with the {@link Contract} annotation. Then, invariant tests
 * can be specified using this annotation.
 * <br>A class annotated with invar will have the test performed before and
 * after <i>all</i> public and protected method invocations. A method with an
 * invar annotation will have the test performed before and after the method
 * execution. In contrast, a field invar annotation is equivalent to a
 * postcondition on the return value of a corresponding get method or direct
 * access. The test isn't performed before execution to permit "lazy
 * evaluation".
 * <br>The {@link #value} String must be a valid Java or AspectJ expression
 * evaluating to true or false at runtime.
 * @see Contract for more information on value strings.
 *
 * @author Dean Wampler  <mailto:dean@aspectprogramming.com>
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD, 
		 ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface Invar {
    /**
     * The "value" is the condition, the runtime test statement.
     * It must evaluate to a legal Java or AspectJ expression at runtime.
     * The default value is "" as there is no convenient default suitable
     * for all cases. Instead subclasses of 
     * {@link org.contract4j5.testexpression.DefaultTestExpressionMaker}
     * are used by the aspects to dynamically generate suitable defaults
     * (when possible).
     * You can override the global default by setting the property
     * <code>org.contract4j5.invar.condition</code>
     * Yes, that's "condition", not "value"!
     */
    String value() default "";

    /**
     * An optional message to print with the standard message when the contract
     * fails.
     * You can override the global default by setting the property
     * <code>org.contract4j5.invar.message</code>
     */
    String message() default "";
}
