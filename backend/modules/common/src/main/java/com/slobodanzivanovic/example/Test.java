package com.slobodanzivanovic.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {

	private static final String CLASSNAME = Test.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);

	public static void test() {
		LOGGER.debug("{}.test()", CLASSNAME);
	}
}
