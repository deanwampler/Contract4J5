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
 * An annotation that supports Design by Contract tests.
 * The tests are generated in AspectJ. Classes that use these tests must be
 * declared with this annotation. Then, tests can be specified using the
 * related annotations &#064;Pre, &#064;Post, and &#064;Inv, which stand for
 * "precondition", "postcondition", and "invariant". Based on the properties
 * of those annotations, appropriate AspectJ aspects are generated to test the
 * contract assertions. Failure of an assertion causes program termination.
 * <br/>When writing condition tests, several special-purpose keywords can be
 * used:
 * <ul>
 *   <li><code>$this</code>: the <code>object</code> being checked.</li>
 *   <li><code>$target</code>: the target to which the annotation is applied,
 *     <i>e.g.,</i> a class, object (this), method, field, return value, 
 *     <i>etc.</i> For example, <code>&#064;Pre("$target!=null")</code> applied
 *     to a method parameter specifies that the value cannot be null.</li>
 *   <li><code>$return</code>: the object returned by a method. Only allowed
 *     in <code>&#064;Post</code> annotations.</li>
 * </ul>
 *
 * @author Dean Wampler  <mailto:dean@aspectprogramming.com>
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Contract {
    /**
     * The message prefixed to any reports of failed contract tests.
     * You can override the global default by setting the property
     * <code>org.contract4j5.contract.messageprefix</code>
     */
    String messagePrefix() default "";

    /**
     * The message appended to any reports of failed contract tests.
     * You can override the global default by setting the property
     * <code>org.contract4j5.contract.messagesuffix</code>
     */
    String messageSuffix() default "";

    /**
     * A flag that, when set to <code>true</code>, tells contract4j to
     * generate this contract test even if contracts are turned
     * off globally when contract4j is run. The flag does not override
     * settings for the nested attributes. For example, if
     * <code>&#064;Pre</code> are disabled, any 
     * <code>&#064;Pre</code> declarations will still be suppressed,
     * unless the corresponding flag in one or more of the 
     * <code>&#064;Pre</code> is set to <code>true</code>.
     * Note: there is no global property for this attribute. Use the
     * "-Acontract" or "-Anocontract" command-line options or the
     * <code>org.contract4j5.contract</code> property.
     */
    boolean alwaysActive() default false;
}
