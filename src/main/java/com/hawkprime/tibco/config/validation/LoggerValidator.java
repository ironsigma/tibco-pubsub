package com.hawkprime.tibco.config.validation;

import lombok.val;

import com.hawkprime.tibco.config.Logger;
import com.hawkprime.validation.ObjectValidator;
import com.hawkprime.validation.Validator;

public class LoggerValidator implements ObjectValidator {

	@Override
	public boolean validate(Object value, String fieldPath, Validator validator) {
		val logger = (Logger) value;
		if (logger.getLevel() == null) {
			logger.setLevel(Logger.DEFAULT_LOG_LEVEL);
		}

		if (logger.getMaxFileSize() == null) {
			logger.setMaxFileSize(Logger.DEFAULT_FILE_SIZE);
		}

		if (logger.getMaxNumFiles() == null) {
			logger.setMaxNumFiles(Logger.DEFAULT_MAX_NUM_FILES);
		}

		return true;
	}
}
