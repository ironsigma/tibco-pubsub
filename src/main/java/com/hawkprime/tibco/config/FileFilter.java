package com.hawkprime.tibco.config;

import lombok.Builder;
import lombok.Data;

import com.hawkprime.validation.annotations.ClassName;
import com.hawkprime.validation.annotations.NotBlank;
import com.hawkprime.validation.annotations.RegularExpression;
import com.hawkprime.validation.annotations.ValidatorClass;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@Data
@Builder
@XStreamAlias("fileFilter")
@ValidatorClass(com.hawkprime.tibco.validation.FileFilterValidator.class)
public class FileFilter {
	@NotBlank
	private String fileNameFilter;

	@NotBlank
	private String contentFilter;

	@RegularExpression
	private String contentRegExFilter;

	@ClassName
	private String classFilter;
}
