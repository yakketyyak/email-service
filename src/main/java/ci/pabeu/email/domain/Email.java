package ci.pabeu.email.domain;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Email {

	
	@NotNull
	private Map<String, String> from;
	@NotNull
	private List<Map<String, String>> toRecipients;
	private List<String> attachmentsFilesAbsolutePaths;
	@NotBlank
	private String templateName;
	@NotBlank
	private String subject;
	private String lang;
}
