package com.hawkprime.tibco.validation;

import com.hawkprime.tibco.config.Configuration;
import com.hawkprime.tibco.config.Consumer;
import com.hawkprime.tibco.config.Producer;
import com.hawkprime.validation.ObjectValidator;
import com.hawkprime.validation.Validator;

public class ConfigurationValidation implements ObjectValidator {

	@Override
	public boolean validate(Object value, String fieldPath, Validator validator) {
		Configuration config = (Configuration) value;
		boolean valid = true;
		String serverId;

		if (config.getProducers() != null) {
			for (Producer producer : config.getProducers()) {
				serverId = producer.getConnection().getServerId();
				if (config.getServer(serverId) == null) {
					validator.addError(String.format("Invalid server id \"%s\" in producer connection \"%s\"",
							serverId, producer.getDescription()));
					valid = false;
				}
			}
		}

		if (config.getConsumers() != null) {
			for (Consumer consumer : config.getConsumers()) {
				serverId = consumer.getConnection().getServerId();
				if (config.getServer(serverId) == null) {
					validator.addError(String.format("Invalid server id \"%s\" in consumer connection \"%s\"",
							serverId, consumer.getDescription()));
					valid = false;
				}
			}
		}

		return valid;
	}
}
