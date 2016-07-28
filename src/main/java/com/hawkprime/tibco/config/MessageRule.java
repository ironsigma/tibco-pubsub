package com.hawkprime.tibco.config;

import lombok.Builder;
import lombok.Data;

import com.hawkprime.tibco.config.validation.MessageRuleValidator;
import com.hawkprime.validation.annotations.ClassName;
import com.hawkprime.validation.annotations.Matches;
import com.hawkprime.validation.annotations.NotBlank;
import com.hawkprime.validation.annotations.RegularExpression;
import com.hawkprime.validation.annotations.Required;
import com.hawkprime.validation.annotations.ValidatorClass;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@Data
@Builder
@ValidatorClass(MessageRuleValidator.class)
@XStreamAlias("messageRule")
public class MessageRule {

	@Required
	@NotBlank
	private String description;

	@NotBlank
	private String matchText;

	@RegularExpression
	private String matchTextRegEx;

	@NotBlank
	private String matchHeader;

	@RegularExpression
	private String matchHeaderRegEx;

	@ClassName
	private String matchClass;

	@Matches(list={"save", "discard"})
	private String action;

	@ClassName
	private String actionClass;
}
