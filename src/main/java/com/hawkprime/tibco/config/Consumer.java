package com.hawkprime.tibco.config;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import com.hawkprime.validation.annotations.Matches;
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
@XStreamAlias("consumer")
public class Consumer {
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
	private String messageStorage;

	@Required
	@Matches(list={"save", "discard"})
	private String defaultAction;

	@Optional
	@Singular
	private List<MessageRule> rules;
}
