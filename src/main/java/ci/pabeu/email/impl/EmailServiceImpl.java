package ci.pabeu.email.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import ci.pabeu.email.config.ServerProperties;
import ci.pabeu.email.domain.Email;
import ci.pabeu.email.service.EmailService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	
	private ServerProperties serverProperties;
	
	private TemplateEngine templateEngine;

	@Override
	public 	ResponseEntity<String> send(Email email)
			throws MessagingException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
	

		Context context = new Context();
		if (email.getToRecipients() == null || email.getToRecipients().isEmpty()) {
			return new ResponseEntity<>(
			          "La liste des destinataires est nulle ou vide", 
			          HttpStatus.BAD_REQUEST);
		}

		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		Locale locale = null;

		javaMailSender.setHost(serverProperties.getHost());
		if (serverProperties.getPort() > 0) {
			javaMailSender.setPort(serverProperties.getPort());
		} else {
			javaMailSender.setPort(25);
		}

		if (StringUtils.isBlank(serverProperties.getUsername())
				|| StringUtils.isBlank(serverProperties.getPassword())) {
			return new ResponseEntity<>(
			          "Veuillez renseigner le login et/ou le mot de passe", 
			          HttpStatus.BAD_REQUEST);
		}

		javaMailSender.setUsername(serverProperties.getUsername());
		javaMailSender.setPassword(serverProperties.getPassword());
		javaMailSender.setJavaMailProperties(getMailProperties(serverProperties.getHost(), true));

		MimeMessage message = javaMailSender.createMimeMessage();

		MimeMessageHelper msgHelper = new MimeMessageHelper(message, true);

		// sender
		if (email.getFrom() != null && !email.getFrom().isEmpty()) {
			msgHelper.setFrom(new InternetAddress(email.getFrom().get("name"), email.getFrom().get("email")));
		} else {
			msgHelper.setFrom(new InternetAddress(serverProperties.getUsername(), "Default email"));
		}

		// recipients
		List<InternetAddress> to = this.to(email.getToRecipients());

		msgHelper.setTo(to.toArray(new InternetAddress[0]));

		// Subject and body
		if (StringUtils.isBlank(email.getLang())) {
			locale = new Locale("fr");
		}else {
			locale = new Locale(email.getLang());
		}
		msgHelper.setSubject(email.getSubject());
		String body = templateEngine.process(email.getTemplateName().concat("_").concat(locale.toString()), context);
		msgHelper.setText(body,true);

		// Attachments
		if (email.getAttachmentsFilesAbsolutePaths() != null && !email.getAttachmentsFilesAbsolutePaths().isEmpty()) {
			this.attachFiles(email.getAttachmentsFilesAbsolutePaths(), msgHelper);
		}

		// send email
		javaMailSender.send(message);
		
		return new ResponseEntity<>(
			      "Email envoyé avec succès ", HttpStatus.OK);
	}

	@Override
	public List<InternetAddress> to(List<Map<String, String>> toRecipients)
			throws UnsupportedEncodingException, AddressException {
		// TODO Auto-generated method stub
		List<InternetAddress> to = new ArrayList<InternetAddress>();
		for (Map<String, String> recipient : toRecipients) {
			String toName = recipient.get("name");
			if (toName != null && !toName.isEmpty()) {
				to.add(new InternetAddress(recipient.get("email"), recipient.get("name")));
			} else {
				to.add(new InternetAddress(recipient.get("email")));
			}
		}
		return to;
	}

	@Override
	public void attachFiles(List<String> attachmentsFilesAbsolutePaths, MimeMessageHelper msgHelper)
			throws MessagingException {
		// TODO Auto-generated method stub
		for (String attachmentPath : attachmentsFilesAbsolutePaths) {
			File pieceJointe = new File(attachmentPath);
			FileSystemResource file = new FileSystemResource(attachmentPath);
			if (pieceJointe.exists() && pieceJointe.isFile()) {
				msgHelper.addAttachment(file.getFilename(), file);
			}
		}
	}

	private Properties getMailProperties(String host, Boolean auth) {
		Properties properties = new Properties();
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.smtp.auth", auth.toString());
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.smtp.starttls.required", "true");
		// properties.setProperty("mail.debug", "true");
		if (host.equals("smtp.gmail.com"))
			properties.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");
		return properties;
	}

}
