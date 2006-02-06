package org.contract4j5.testexpression;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.contract4j5.TestContext;
import org.contract4j5.interpreter.TestResult;

/**
 * Utility class for finding a corresponding annotation in a parent class,
 * interface, advice, or method. This utility supports the case where subclasses
 * use an annotation without a test expression and we want to use the
 * test expression in the corresponding annotation on a parent element,
 * if any, as the "default" expression. Note that methods subclassing a 
 * parent method or implementing an interface method are required to
 * add the same annotations found on the parent. This overcomes the 
 * limitation of Java method annotations that they are never inherited. Class
 * annotations are different; the subclass can optionally add the annotation.
 * @author Dean Wampler
 */
public interface ParentTestExpressionFinder {
	/**
	 * Search for an annotation on the corresponding advice in the parent types,
	 * if any, and return the first nonempty test expression found or return "".
	 * @param whichAnnotationType
	 * @param advice which of type Method
	 * @param context
	 * @return a {@link TestResult} containing the parent's test expression, 
	 * if found, as the "message". It may be "", but never null. If 
	 * {@link TestResult#isPassed()} returns false, then an error occurred. 
	 * (It returns true for an empty expression.)
	 */
	TestResult findParentAdviceTestExpression(
			Annotation  whichAnnotationType, 
			Method      advice,
			TestContext context);

	/**
	 * If the input test expression is empty, then call
	 * {@link #findParentAdviceTestExpression(Annotation, Method, TestContext)} to
	 * determine a parent test expression, if any. If the expression is not empty,
	 * then return it inside the {@link TestResult}.
	 */
	TestResult findParentAdviceTestExpressionIfEmpty(
			String      testExpression,
			Annotation  whichAnnotationType, 
			Method      advice,
			TestContext context);

	/**
	 * Like {@link #findParentAdviceTestExpression(Annotation, Method, TestContext)}
	 * for methods.
	 */
	TestResult findParentMethodTestExpression(
			Annotation  whichAnnotationType, 
			Method      method,
			TestContext context);
	
	/**
	 * If the input test expression is empty, then call
	 * {@link #findParentMethodTestExpression(Annotation, Method, TestContext)} to
	 * determine a parent test expression, if any. If the expression is not empty,
	 * then return it inside the {@link TestResult}.
	 */
	TestResult findParentMethodTestExpressionIfEmpty(
			String      testExpression,
			Annotation  whichAnnotationType, 
			Method      advice,
			TestContext context);

	/**
	 * Like {@link #findParentAdviceTestExpression(Annotation, Method, TestContext)}
	 * for constructors.
	 */
	TestResult findParentConstructorTestExpression(
			Annotation  whichAnnotationType, 
			Constructor constructor,
			TestContext context);

	/**
	 * If the input test expression is empty, then call
	 * {@link #findParentConstructorTestExpression(Annotation, Method, TestContext)} to
	 * determine a parent test expression, if any. If the expression is not empty,
	 * then return it inside the {@link TestResult}.
	 */
	TestResult findParentConstructorTestExpressionIfEmpty(
			String      testExpression,
			Annotation  whichAnnotationType, 
			Constructor constructor,
			TestContext context);

	/**
	 * Search for an invariant annotation on the parent "types" (class, 
	 * interfaces, aspect). Since invariants may not change under derivation, 
	 * it is an error if more than one test expression is found and they do not 
	 * agree (whitespace is ignored). In this case, the returned test expression 
	 * will contain an error message and the {@link TestResult#isPassed()} 
	 * value will be false. If no parent annotation exists, then returns "".
	 * @param clazz of the type with the empty annotation.
	 * @param context
	 * @return the parent's test expression, if found, as the "message" in a 
	 * {@link TestResult}, which may be "", but never null. If 
	 * {@link TestResult#isPassed()} returns false, then an error occurred, 
	 * probably more than one unequal expressions were found.  (It returns 
	 * true for an empty expression.)
	 * @note There's no need to pass in the annotation type as they are always "Invars".
	 * @note Doing a string comparison, ignoring whitespace, doesn't correctly cover
	 * all possible logically-equivalent expressions, <i>e.g.,</i> 
	 * <code>foo == bar</code> is considered different from </code>bar == foo</code>,
	 * which is wrong!
	 * TODO run the uniqueness test on all invariants, not just when searching for
	 * parent expressions. Keep performance in mind.
	 */
	TestResult findParentTypeInvarTestExpression (
			Class       clazz,
			TestContext context);

	/**
	 * If the input test expression is empty, then call
	 * {@link #findParentTypeInvarTestExpression(Class, TestContext)} to
	 * determine a parent test expression, if any. If the expression is not empty,
	 * then return it inside the {@link TestResult}.
	 */
	TestResult findParentTypeInvarTestExpressionIfEmpty (
			String      testExpression,
			Class       clazz,
			TestContext context);

}
