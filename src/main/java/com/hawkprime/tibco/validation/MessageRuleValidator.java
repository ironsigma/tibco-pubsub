package com.hawkprime.tibco.validation;

import com.hawkprime.tibco.config.MessageRule;
import com.hawkprime.validation.ObjectValidator;
import com.hawkprime.validation.Validator;

public class MessageRuleValidator implements ObjectValidator {

	@Override
	public boolean validate(Object value, String fieldPath, Validator validator) {
		MessageRule rule = (MessageRule) value;
		boolean valid = true;
		if (rule.getMatchText() == null
				&& rule.getMatchTextRegEx() == null
				&& rule.getMatchHeader() == null
				&& rule.getMatchHeaderRegEx() == null
				&& rule.getMatchClass() == null) {
			validator.addError(String.format("MessageRule \"%s\" must contain at least one match rule", fieldPath));
			valid = false;
		}

		if (rule.getAction() == null && rule.getActionClass() == null) {
			validator.addError(String.format("MessageRule \"%s\" must contain at least one action", fieldPath));
			valid = false;
		}

		return valid;
	}
}
