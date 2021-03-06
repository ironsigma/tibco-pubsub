package com.hawkprime.tibco.config.validation;

import lombok.val;

import com.hawkprime.tibco.config.FileFilter;
import com.hawkprime.validation.ObjectValidator;
import com.hawkprime.validation.Validator;

public class FileFilterValidator implements ObjectValidator {

	@Override
	public boolean validate(Object value, String fieldPath, Validator validator) {
		val filter = (FileFilter) value;
		if (filter.getFileNameFilter() == null
				&& filter.getContentFilter() == null
				&& filter.getContentRegExFilter() == null
				&& filter.getClassFilter() == null) {
			validator.addError(String.format("FileFilter \"%s\" must contain at least one filter", fieldPath));
			return false;
		}
		return true;
	}
}
