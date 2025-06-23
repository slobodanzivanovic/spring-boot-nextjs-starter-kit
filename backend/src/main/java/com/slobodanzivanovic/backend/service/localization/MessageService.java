package com.slobodanzivanovic.backend.service.localization;

import com.slobodanzivanovic.backend.util.ApplicationContextProvider;

/**
 * Service interface for messages
 *
 * @author Slobodan Zivanovic - June 21, 2025
 */
public interface MessageService {

	String getMessage(String key);

	String getMessage(String key, Object... args);

	static String getStaticMessage(String key, Object... args) {
		return ApplicationContextProvider.bean(MessageService.class).getMessage(key, args);
	}

}
