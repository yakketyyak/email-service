package ci.pabeu.email.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Data;

@ConfigurationProperties(prefix = "smtp")
@AllArgsConstructor
@Data
public class ServerProperties {
	
	private String host;
	private int port;
	private String username;
	private String password;

}
