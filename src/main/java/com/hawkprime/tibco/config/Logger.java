package com.hawkprime.tibco.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.hawkprime.tibco.config.validation.LoggerValidator;
import com.hawkprime.validation.annotations.InRange;
import com.hawkprime.validation.annotations.Matches;
import com.hawkprime.validation.annotations.Required;
import com.hawkprime.validation.annotations.ValidatorClass;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XStreamAlias("logger")
@ValidatorClass(LoggerValidator.class)
public class Logger {
	public static final String DEFAULT_LOG_LEVEL = "DEBUG";
	public static final String DEFAULT_FILE_SIZE = "250MB";
	public static final Integer DEFAULT_MAX_NUM_FILES = 1;

	@Required
	private String fileName;

	@InRange(min=1, max=99)
	private Integer maxNumFiles;

	@Matches(regex="^(?i)\\d+(KB|MB|GB)?$")
	private String maxFileSize;

	@Matches(list={"TRACE", "DEBUG", "INFO", "WARN", "ERROR"})
	private String level;
}
