/*
 * Copyright 2005 2006 Dean Wampler. All rights reserved.
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.contract4j5.context.TestContext;
import org.contract4j5.contract.Invar;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.Severity;

/**
 * Default implementation.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class ParentTestExpressionFinderImpl implements
		ParentTestExpressionFinder {

	/**
	 * Utility for determining the next name to use for lookup.
	 */
	static interface NameDeterminator {
		String getName (Class clazz, String oldName);
	}
	/**
	 * Default name determinator, useful for most cases, just
	 * returns the input name. 
	 */
	static class DefaultNameDeterminator implements NameDeterminator {
		public String getName(Class clazz, String oldName) {
			return oldName;
		}
	}
	/**
	 * Constructor name determinator uses the class' "simple name" for the method
	 * name.
	 */
	static class CtorNameDeterminator implements NameDeterminator {
		public String getName(Class clazz, String oldName) {
			return clazz.getSimpleName();
		}
	}

	static final NameDeterminator defaultNameDeterminator = new DefaultNameDeterminator();
	static final NameDeterminator ctorNameDeterminator    = new CtorNameDeterminator();
	
	/* (non-Javadoc)
	 * @see org.contract4j5.testexpression.ParentTestExpressionFinder#findParentAdviceTestExpression(java.lang.annotation.Annotation, java.lang.reflect.Method, org.contract4j5.TestContext)
	 */
	public TestResult findParentAdviceTestExpression(
			Annotation  whichAnnotationType, 
			Method      advice,
			TestContext context) {
		return findParentMethodTestExpression(whichAnnotationType, advice, context);
	}
	
	/* (non-Javadoc)
	 * @see org.contract4j5.testexpression.ParentTestExpressionFinder#findParentAdviceTestExpressionIfEmpty(java.lang.String, java.lang.annotation.Annotation, java.lang.reflect.Method, org.contract4j5.TestContext)
	 */
	public TestResult findParentAdviceTestExpressionIfEmpty(
			String      testExpression, 
			Annotation  whichAnnotationType, 
			Method      advice, 
			TestContext context) {
		if (! empty(testExpression)) {
			return makeTestResult(true, testExpression);
		}
		return findParentAdviceTestExpression(whichAnnotationType, advice, context);
	}

	/* (non-Javadoc)
	 * @see org.contract4j5.testexpression.ParentTestExpressionFinder#findParentMethodTestExpression(java.lang.annotation.Annotation, java.lang.reflect.Method, org.contract4j5.TestContext)
	 */
	public TestResult findParentMethodTestExpression(
			Annotation  whichAnnotationType, 
			Method      method,
			TestContext context) {
		Class       clazz = method.getDeclaringClass();
		String methodName = method.getName();
		Class<?>[] methodArgsTypes = method.getParameterTypes();
		return findParentMethodTestExpressionSupport(
				clazz, methodName, methodArgsTypes, false, whichAnnotationType, defaultNameDeterminator);
	}
	
	/* (non-Javadoc)
	 * @see org.contract4j5.testexpression.ParentTestExpressionFinder#findParentMethodTestExpressionIfEmpty(java.lang.String, java.lang.annotation.Annotation, java.lang.reflect.Method, org.contract4j5.TestContext)
	 */
	public TestResult findParentMethodTestExpressionIfEmpty(
			String      testExpression, 
			Annotation  whichAnnotationType, 
			Method      method, 
			TestContext context) {
		if (! empty(testExpression)) {
			return makeTestResult(true, testExpression);
		}
		return findParentMethodTestExpression(whichAnnotationType, method, context);
	}

	/* (non-Javadoc)
	 * @see org.contract4j5.testexpression.ParentTestExpressionFinder#findParentConstructorTestExpression(java.lang.annotation.Annotation, java.lang.reflect.Constructor, org.contract4j5.TestContext)
	 */
	public TestResult findParentConstructorTestExpression(
			Annotation  whichAnnotationType, 
			Constructor constructor,
			TestContext context) {
		Class       clazz = constructor.getDeclaringClass();
		String methodName = clazz.getSimpleName();
		Class<?>[] methodArgsTypes = constructor.getParameterTypes();
		return findParentMethodTestExpressionSupport(
			clazz, methodName, methodArgsTypes, true, whichAnnotationType, ctorNameDeterminator);
	}
	
	/* (non-Javadoc)
	 * @see org.contract4j5.testexpression.ParentTestExpressionFinder#findParentConstructorTestExpressionIfEmpty(java.lang.String, java.lang.annotation.Annotation, java.lang.reflect.Method, org.contract4j5.TestContext)
	 */
	public TestResult findParentConstructorTestExpressionIfEmpty(
			String      testExpression, 
			Annotation  whichAnnotationType, 
			Constructor constructor, 
			TestContext context) {
		if (! empty(testExpression)) {
			return makeTestResult(true, testExpression);
		}
		return findParentConstructorTestExpression(whichAnnotationType, constructor, context);
	}


	/**
	 * @param clazz of the object currently being examined
	 * @param methodName  the method name, when applicable, for which annotations are being searched
	 * @param methodArgsTypes the arguments to the method
	 * @param isConstructor true if we are looking for a constructor annotation.
	 * @param whichAnnotationType which type of annotation
	 * @param nameDeterminator the utility for determining the correct method name for the current "clazz".
	 * It will be equal to "methodName", except for constructors, where it will be the clazz's "simpleName".
	 * @return a {@link TestResult} object with the the parent test's expression.
	 */
	protected TestResult findParentMethodTestExpressionSupport (
			Class clazz,
			String methodName,
			Class<?>[] methodArgsTypes,
			boolean    isConstructor,
			Annotation whichAnnotationType,
			NameDeterminator nameDeterminator) {
		while (!clazz.equals(Object.class)) {
			// Somewhat superfluous on the first iteration, because
			// we already know it has no expression!
			TestResult result = findParentMethodAnnoTestExpression (
					clazz,
					methodName,
					methodArgsTypes,
					isConstructor,
					whichAnnotationType);
			if (result.isPassed() && result.getMessage().length() > 0) {
				return result;
			}
			// Note that for ctor annotations, nothing will be found on interfaces!
			Type[] interfaces = clazz.getGenericInterfaces();
			for (Type t: interfaces) {
                Class tClass = (t instanceof ParameterizedType)
                ? (Class) ((ParameterizedType) t).getRawType()
                : (Class) t;

				result = findParentMethodAnnoTestExpression (
					tClass,
					methodName,
					methodArgsTypes,
					isConstructor,
					whichAnnotationType);
				if (result.isPassed() && result.getMessage().length() > 0) {
					return result;
				}	
			}
			Type parent = clazz.getGenericSuperclass();
			clazz = (Class) parent;
			methodName = nameDeterminator.getName(clazz, methodName);
		}
		return makeEmptyTestResult();
	}
	
	protected TestResult findParentMethodAnnoTestExpression(
			Class      clazz,
			String     methodName,
			Class[]    methodArgsTypes,
			boolean    isConstructor,
			Annotation whichAnnotationType) {
		try {
			Annotation[] annos  = null;
			if (isConstructor) {
				Constructor ctor = clazz.getConstructor(methodArgsTypes);
				annos = ctor.getAnnotations();
			} else {
				Method method = clazz.getMethod(methodName, methodArgsTypes);
				annos = method.getDeclaredAnnotations();
			}
			if (annos == null) {
				return makeEmptyTestResult();
			}
			for (Annotation anno: annos) {
				if (anno.getClass().equals(whichAnnotationType.getClass())) {
					TestResult result = getAnnoTestExpression(anno);
					if (result.isPassed() && result.getMessage().length() > 0) {
						return result;
					}
				}
			}
		} catch (NoSuchMethodException nsme) {
			// Somewhat expensive setup for output, so don't do it unless we know we'll log...
			if (getReporter().getThreshold().compareTo(Severity.INFO) >= 0) {
				StringBuffer args = new StringBuffer(256);
				for (Class a: methodArgsTypes) {
					args.append(a.toString());
					args.append(",");
				}
				getReporter().report(Severity.INFO, this.getClass(),
						"No method found: "+clazz.getName()+"."+methodName+"("+args.toString()+")");
			}
		}
		return makeEmptyTestResult();
	}
	
	/* (non-Javadoc)
	 * @see org.contract4j5.testexpression.ParentTestExpressionFinder#findParentTypeInvarTestExpression(java.lang.Class, org.contract4j5.TestContext)
	 */
	public TestResult findParentTypeInvarTestExpression(Class clazz, TestContext context) {
		TestResult result = makeEmptyTestResult();
		if (clazz == null) {
			return result;
		}
		// Get only the annos. declared on this class/interface/aspect.
		if (!clazz.equals(Object.class)) {
			Annotation[] annos = clazz.getDeclaredAnnotations();
			for (Annotation anno: annos) {
				if (anno instanceof Invar) {
					Invar invar = (Invar) anno;
					result = setExpr(invar.value(), result);
					if (result.isPassed() == false) {
						return result;
					}
				}
			}
			Type t = clazz.getGenericSuperclass();
			TestResult result2 = findParentTypeInvarTestExpression ((Class) t, context);
			result = setExpr(result2.getMessage(), result);
			if (result.isPassed() == false) {
				return result;
			}
		}
		Type[] ts = clazz.getGenericInterfaces();
		for (Type i: ts) {
			TestResult result2 = findParentTypeInvarTestExpression ((Class) i, context);
			result = setExpr(result2.getMessage(), result);
			if (result.isPassed() == false) {
				return result;
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.contract4j5.testexpression.ParentTestExpressionFinder#findParentTypeInvarTestExpressionIfEmpty(java.lang.String, java.lang.annotation.Annotation, java.lang.Class, org.contract4j5.TestContext)
	 */
	public TestResult findParentTypeInvarTestExpressionIfEmpty(
			String      testExpression, 
			Class       clazz,
			TestContext context) {
		if (! empty(testExpression)) {
			return makeTestResult(true, testExpression);
		}
		return findParentTypeInvarTestExpression(clazz, context);
	}

	protected TestResult setExpr(String candidateExpr, TestResult result) {
		if (candidateExpr.length() == 0) {
			return result;
		}
		String expr = result.getMessage();
		if (expr.length() == 0) {
			result.setMessage(candidateExpr);
		} else {
			String e1 = expr.replaceAll("\\s", "");
			String e2 = candidateExpr.replaceAll("\\s", "");
			if (!e1.equals(e2)) {
				StringBuffer sb = new StringBuffer(
						ExpressionInterpreter.InvalidTestExpression.DUPLICATE_INVAR_TEST_EXPRESSIONS.toString());
				sb.append("\"");
				sb.append(expr);
				sb.append("\", \"");
				sb.append(candidateExpr);
				sb.append("\" (and possibly more)");
				return makeTestResult(false, sb.toString());
			}
		}
		return result;
	}
	

	protected TestResult getAnnoTestExpression(Annotation anno) {
		if (anno == null) {
			return makeEmptyTestResult();
		}
		if (anno instanceof Pre) {
			Pre pre = (Pre) anno;
			return makeTestResult(true, pre.value());
		}
		if (anno instanceof Post) {
			Post post = (Post) anno;
			return makeTestResult(true, post.value());
		}
		if (anno instanceof Invar) {
			Invar invar = (Invar) anno;
			return makeTestResult(true, invar.value());
		}
		String msg = "Unrecognized Annotation type: "+anno.toString();
		assert false: msg;
		return makeTestResult(false, msg);
	}
	
	protected TestResult makeEmptyTestResult () {
		return new TestResult(true, "");
	}
	
	protected TestResult makeTestResult (boolean passed, String msg) {
		return new TestResult(passed, msg);
	}
	
	protected boolean empty(String s) {
		return s == null || s.length() == 0;
	}

	private Reporter reporter;
	protected Reporter getReporter() {
		return reporter;
	}
	
}
