package ci.pabeu.email.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessageHelper;

import ci.pabeu.email.domain.Email;

/*
 * Interface des méthodes utilisées
 */
public interface EmailService {

	public ResponseEntity<String> send(Email email)
			throws MessagingException, UnsupportedEncodingException;

	public List<InternetAddress> to(List<Map<String, String>> toRecipients)
			throws UnsupportedEncodingException, AddressException;

	public void attachFiles(List<String> attachmentsFilesAbsolutePaths, MimeMessageHelper msgHelper)
			throws MessagingException;

}
