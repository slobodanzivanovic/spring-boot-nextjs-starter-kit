package com.slobodanzivanovic.backend.config;

import java.util.Arrays;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Slobodan Zivanovic
 */
@Configuration
public class LocalizationConfig implements WebMvcConfigurer {

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename("i18n/messages");
		source.setDefaultEncoding("UTF-8");
		source.setUseCodeAsDefaultMessage(true);
		source.setFallbackToSystemLocale(false);
		// TODO: disable caching just for development purposes
		source.setCacheSeconds(0);
		// source.setCacheSeconds(3600); // Cache for 1 hour
		return source;
	}

	@Bean
	public LocalValidatorFactoryBean getValidator() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource(messageSource());
		return validator;
	}

	@Bean
	public LocaleResolver localeResolver() {
		return new AcceptHeaderLocaleResolver() {
			@Override
			public Locale resolveLocale(HttpServletRequest request) {
				String lang = request.getParameter("lang");
				if (lang != null) {
					return Locale.forLanguageTag(lang);
				}
				// if no specific header matches our supported locales, use default
				Locale locale = super.resolveLocale(request);
				if (!Arrays.asList(new Locale("sr"), Locale.ENGLISH).contains(locale)) {
					return new Locale("sr");
				}

				return locale;
			}
		};
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("lang");
		return interceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

}
