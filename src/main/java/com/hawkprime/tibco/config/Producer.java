package com.hawkprime.tibco.config;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import com.hawkprime.validation.annotations.NotBlank;
import com.hawkprime.validation.annotations.Optional;
import com.hawkprime.validation.annotations.Required;
import com.hawkprime.validation.annotations.SystemFile;
import com.hawkprime.validation.annotations.SystemFile.Mode;
import com.hawkprime.validation.annotations.SystemFile.Type;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@Data
@Builder
@XStreamAlias("producer")
public class Producer {
	@NotBlank
	@XStreamAsAttribute
	private String profile;

	@Required
	@NotBlank
	private String description;

	@Required
	private Connection connection;

	@Required
	@SystemFile(type=Type.DIRECTORY, mode=Mode.READWRITE)
	private String messageSource;

	@Required
	@SystemFile(type=Type.DIRECTORY, mode=Mode.READWRITE)
	private String messageTarget;

	@Optional
	@Singular
	private List<FileFilter> filters;
}
