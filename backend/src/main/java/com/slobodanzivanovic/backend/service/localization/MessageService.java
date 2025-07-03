package com.slobodanzivanovic.backend.service.localization;

import com.slobodanzivanovic.backend.util.ApplicationContextProvider;

/**
 * @author Slobodan Zivanovic
 */
public interface MessageService {

	String getMessage(String key);

	String getMessage(String key, Object... args);

	static String getStaticMessage(String key, Object... args) {
		return ApplicationContextProvider.bean(MessageService.class).getMessage(key, args);
	}

}
