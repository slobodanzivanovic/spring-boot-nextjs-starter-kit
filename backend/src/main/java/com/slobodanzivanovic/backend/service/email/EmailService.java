package com.slobodanzivanovic.backend.service.email;


import jakarta.mail.MessagingException;

import java.util.Map;

/**
 * @author Slobodan Zivanovic - June 24, 2025
 */
public interface EmailService {

	/**
	 * Sends a verification email to a user.
	 *
	 * @param to      The recipient email address
	 * @param subject The email subject
	 * @param body    The email content (HTML format)
	 * @throws MessagingException If sending the email fails
	 */
	void sendVerificationEmail(String to, String subject, String body) throws MessagingException;

	/**
	 * Send an email using a Thymeleaf template
	 * <p>
	 * Processes the template with the provided model and sends the resulting HTML
	 * content to the recipient. The template is selected based on the users locale.
	 * </p>
	 *
	 * @param to            The recipient email address
	 * @param subject       The email subject
	 * @param templateName  The base name of the template (without locale suffix or extension)
	 * @param templateModel The model containing variables to be used in the template
	 * @throws MessagingException If sending the email fails
	 */
	void sendTemplatedEmail(String to, String subject, String templateName, Map<String, Object> templateModel)
		throws MessagingException;

}
