package com.slobodanzivanovic.backend.service.email.impl;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.slobodanzivanovic.backend.service.email.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * @author Slobodan Zivanovic
 */
@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Override
	public void sendVerificationEmail(String to, String subject, String body) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(fromEmail);
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(body, true);
		mailSender.send(message);
	}

	@Override
	public void sendTemplatedEmail(String to, String subject, String templateName, Map<String, Object> templateModel)
			throws MessagingException {
		Locale locale = LocaleContextHolder.getLocale();
		String langCode = locale.getLanguage();

		if (!langCode.equals("en")) {
			langCode = "sr";
		}

		String localizedTemplateName = templateName + "-" + langCode;

		Context thymeleafContext = new Context(locale);
		thymeleafContext.setVariables(templateModel);

		String htmlContent = templateEngine.process(localizedTemplateName, thymeleafContext);

		sendVerificationEmail(to, subject, htmlContent);

	}

}
