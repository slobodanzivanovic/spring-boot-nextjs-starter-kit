package com.slobodanzivanovic.backend.service.localization.impl;

import com.slobodanzivanovic.backend.service.localization.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

/**
 * @author Slobodan Zivanovic - June 21, 2025
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

	private final MessageSource messageSource;

	@Override
	public String getMessage(String key) {
		return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
	}

	@Override
	public String getMessage(String key, Object... args) {
		return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
	}

}
