package org.junit.runners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.ParentRunner;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * Using <code>Suite</code> as a runner allows you to manually
 * build a suite containing tests from many classes. It is the JUnit 4 equivalent of the JUnit 3.8.x
 * static {@link junit.framework.Test} <code>suite()</code> method. To use it, annotate a class
 * with <code>@RunWith(Suite.class)</code> and <code>@SuiteClasses(TestClass1.class, ...)</code>.
 * When you run this class, it will run all the tests in all the suite classes.
 */
public class Suite extends ParentRunner<Runner> {
	/**
	 * The <code>SuiteClasses</code> annotation specifies the classes to be run when a class
	 * annotated with <code>@RunWith(Suite.class)</code> is run.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface SuiteClasses {
		public Class<?>[] value();
	}
	
	private static Class<?>[] getAnnotatedClasses(Class<?> klass) throws InitializationError {
		SuiteClasses annotation= klass.getAnnotation(SuiteClasses.class);
		if (annotation == null)
			throw new InitializationError(String.format("class '%s' must have a SuiteClasses annotation", klass.getName()));
		return annotation.value();
	}

	private final List<Runner> fRunners;

	/**
	 * Called reflectively on classes annotated with <code>@RunWith(Suite.class)</code>
	 * 
	 * @param klass the root class
	 * @param builder builds runners for classes in the suite
	 * @throws InitializationError
	 */
	public Suite(Class<?> klass, RunnerBuilder builder) throws InitializationError {
		this(builder, klass, getAnnotatedClasses(klass));
		validate();
	}

	/**
	 * Call this when there is no single root class (for example, multiple class names
	 * passed on the command line to {@link org.junit.runner.JUnitCore}
	 * 
	 * @param builder builds runners for classes in the suite
	 * @param classes the classes in the suite
	 */
	public Suite(RunnerBuilder builder, Class<?>[] classes) {
		this(null, builder.runners(classes));
	}
	
	/**
	 * Called by this class and subclasses once the classes making up the suite have been determined
	 * 
	 * @param builder builds runners for classes in the suite
	 * @param klass the root of the suite
	 * @param suiteClasses the classes in the suite
	 * @throws InitializationError
	 */
	protected Suite(RunnerBuilder builder, Class<?> klass, Class<?>[] suiteClasses) throws InitializationError {
		this(klass, builder.runners(klass, suiteClasses));
	}
	
	/**
	 * Called by this class and subclasses once the runners making up the suite have been determined
	 * 
	 * @param klass root of the suite
	 * @param runners for each class in the suite, a {@link Runner}
	 */
	protected Suite(Class<?> klass, List<Runner> runners) {
		super(klass);
		fRunners = runners;
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		getTestClass().validateStaticMethods(errors);
	}
	
	@Override
	protected List<Runner> getChildren() {
		return fRunners;
	}
	
	@Override
	protected Description describeChild(Runner child) {
		return child.getDescription();
	}

	@Override
	protected void runChild(Runner each, final RunNotifier notifier) {
		each.run(notifier);
	}
}
