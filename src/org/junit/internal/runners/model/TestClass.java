package org.junit.internal.runners.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public class TestClass extends TestElement {
	private final Class<?> fClass;
	
	public TestClass(Class<?> klass) {
		fClass= klass;
	}

	public List<TestMethod> getTestMethods() {
		return getAnnotatedMethods(Test.class);
	}

	@Override
	public List<TestMethod> getBefores() {
		return getAnnotatedMethods(BeforeClass.class);
	}

	@Override
	public List<TestMethod> getAfters() {
		return getAnnotatedMethods(AfterClass.class);
	}
	
	public List<TestMethod> getAnnotatedMethods(Class<? extends Annotation> annotationClass) {
		List<TestMethod> results= new ArrayList<TestMethod>();
		for (Class<?> eachClass : getSuperClasses(fClass)) {
			Method[] methods= eachClass.getDeclaredMethods();
			for (Method eachMethod : methods) {
				Annotation annotation= eachMethod.getAnnotation(annotationClass);
				TestMethod testMethod= new TestMethod(eachMethod, this);
				if (annotation != null && ! testMethod.isShadowedBy(results))
					results.add(testMethod);
			}
		}
		if (runsTopToBottom(annotationClass))
			Collections.reverse(results);
		return results;
	}

	private boolean runsTopToBottom(Class< ? extends Annotation> annotation) {
		return annotation.equals(Before.class) || annotation.equals(BeforeClass.class);
	}
	
	private List<Class<?>> getSuperClasses(Class< ?> testClass) {
		ArrayList<Class<?>> results= new ArrayList<Class<?>>();
		Class<?> current= testClass;
		while (current != null) {
			results.add(current);
			current= current.getSuperclass();
		}
		return results;
	}

	public Constructor<?> getConstructor() throws SecurityException, NoSuchMethodException {
		return fClass.getConstructor();
	}

	public Class<?> getJavaClass() {
		return fClass;
	}

	public String getName() {
		return fClass.getName();
	}

	public void runProtected(RunNotifier notifier, Description description, Runnable runnable) {
		// TODO: (Oct 8, 2007 1:02:02 PM) instead of this, have a runChildren overridable method in JUnit4ClassRunner
		runProtected(new EachTestNotifier(notifier, description), runnable, null);
	}

	public void validateMethods(Class<? extends Annotation> annotation, boolean isStatic, ErrorList errors) {
		List<TestMethod> methods= getAnnotatedMethods(annotation);
		
		for (TestMethod eachTestMethod : methods) {
			eachTestMethod.validate(isStatic, errors);
		}
	}

	public void validateStaticMethods(ErrorList errors) {
		validateMethods(BeforeClass.class, true, errors);
		validateMethods(AfterClass.class, true, errors);
	}

	public void validateNoArgConstructor(ErrorList errors) {
		try {
			getConstructor();
		} catch (Exception e) {
			errors.add(new Exception("Test class should have public zero-argument constructor", e));
		}
	}

	public void validateInstanceMethods(ErrorList errors) {
		validateMethods(After.class, false, errors);
		validateMethods(Before.class, false, errors);
		validateMethods(Test.class, false, errors);
		
		List<TestMethod> methods= getAnnotatedMethods(Test.class);
		if (methods.size() == 0)
			errors.add(new Exception("No runnable methods"));
	}

	public void validateMethodsForDefaultRunner(ErrorList errors) {
		validateNoArgConstructor(errors);
		validateStaticMethods(errors);
		validateInstanceMethods(errors);
	}
}
