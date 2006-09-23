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

package org.contract4j5.testexpression;

import org.contract4j5.context.TestContext;

/**
 * Interface for objects that know how to construct a "default" test expression 
 * when one is not specified in the test annotation. An appropriate default will
 * be context dependent (e.g., class-level vs. method-level invariants). The
 * aspects that use these objects provide API hooks for users to install their
 * own versions.
 */
public interface DefaultTestExpressionMaker {
	/**
	 * @param context TestContext information
	 * @return new test expression string or an empty string, if no default
	 * is desired or feasible. Should never return null.
	 */
	String makeDefaultTestExpression(TestContext context);

	/**
	 * Convenience method that examines the input "testExpression" and simply returns 
	 * it, if it is not empty, or makes and returns the default expression.
	 * @param testExpression that may or may not be empty.
	 * @param context TestContext information
	 * @return new test expression string or an empty string, if no default
	 * is desired or feasible. Should never return null.
	 */
	String makeDefaultTestExpressionIfEmpty(
			String      testExpression,
			TestContext context);
	
	/**
	 * Helper method that iterates through the method arguments and composes a 
	 * test expression that requires each of them to be non-null. However, if 
	 * any argument is actually a primitive, it is ignored. For example, a 
	 * method that has the following argument list, <code>String, int, Foo, 
	 * float</code>, would result in a returned string equal to 
	 * <code>args[0] != null && args[2] != null</code>. 
	 * @note If the expression will be used within a larger test expression, 
	 * it should probably be enclosed in parentheses by the caller, to avoid 
	 * some potential scoping ambiguities. 
	 * @note There may be cases where an argument is a primitive, but it's type
	 * doesn't reflect that, (e.g., it is not {@link Integer#TYPE}. It appears
	 * harmless to return an <code>$args[n] != null</code> expression for it anyway.
	 * @param context with the method args.
	 * @return an appropriate test string that will never be null.
	 */
	String makeArgsNotNullExpression (TestContext context);
	
	/**
	 * @param clazz
	 * @return true if the input class does not represent a primitive. If null, we assume this
	 * is true. If not null, we return the value of {@link Class#isPrimitive()}.
	 */
	boolean isNotPrimitive (Class clazz);
}
