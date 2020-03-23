package ci.pabeu.email.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ci.pabeu.email.domain.Email;
import ci.pabeu.email.impl.EmailServiceImpl;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(name = "/email")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EmailController {
	
	private EmailServiceImpl emailServiceImpl;
	
	@PostMapping(name = "/send", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> send(@RequestBody @Validated Email email) throws UnsupportedEncodingException, MessagingException
	{
		return emailServiceImpl.send(email);
	}
	
	@GetMapping(name = "/testJenkins", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> get() {
		return new ResponseEntity<String>("Okay for jenkins", HttpStatus.ACCEPTED);
	}

}
