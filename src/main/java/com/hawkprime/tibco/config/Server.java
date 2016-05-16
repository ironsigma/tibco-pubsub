package com.hawkprime.tibco.config;

import lombok.Builder;
import lombok.Data;

import com.hawkprime.tibco.validation.TiboHostValidation;
import com.hawkprime.validation.annotations.InRange;
import com.hawkprime.validation.annotations.Matches;
import com.hawkprime.validation.annotations.NotBlank;
import com.hawkprime.validation.annotations.Required;
import com.hawkprime.validation.annotations.SystemFile;
import com.hawkprime.validation.annotations.ValidatorClass;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@Data
@Builder
@XStreamAlias("server")
@ValidatorClass(TiboHostValidation.class)
public class Server {
	@Required
	@NotBlank
	@XStreamAsAttribute
	private String id;

	@Required
	@Matches(regex="^(ssl|tcp)://.*$")
	private String host;

	@Required
	@InRange(min=1, max=65536)
	private Integer port;

	@Required
	@NotBlank
	private String user;

	@Required
	@NotBlank
	private String password;

	@SystemFile
	private String certificateAuthorityFile;

	@SystemFile
	private String serverKeyFile;

	@SystemFile
	private String clientKeyFile;

	@NotBlank
	private String keyPassword;
}
