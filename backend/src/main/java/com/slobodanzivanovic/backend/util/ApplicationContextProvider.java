package com.slobodanzivanovic.backend.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Utility class that provides access to the ApplicationContext
 *
 * @author Slobodan Zivanovic
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

	private static ApplicationContext context;

	/**
	 * Get a bean of the specified type from the application context
	 *
	 * @param beanType The type of bean to retrieve
	 * @return The bean instance
	 */
	public static <T> T bean(Class<T> beanType) {
		return context.getBean(beanType);
	}

	/**
	 * Get a bean by name from the application context.
	 *
	 * @param name The name of the bean to retrieve
	 * @return The bean instance
	 */
	public static Object bean(String name) {
		return context.getBean(name);
	}

	@Override
	public void setApplicationContext(@SuppressWarnings("NullableProblems") ApplicationContext ac) {
		context = ac;
	}

}
