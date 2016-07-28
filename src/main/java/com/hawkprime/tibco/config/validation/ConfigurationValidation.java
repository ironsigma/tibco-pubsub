package com.hawkprime.tibco.config.validation;

import lombok.val;

import com.hawkprime.tibco.config.Configuration;
import com.hawkprime.tibco.config.Consumer;
import com.hawkprime.tibco.config.Producer;
import com.hawkprime.validation.ObjectValidator;
import com.hawkprime.validation.Validator;

public class ConfigurationValidation implements ObjectValidator {

	@Override
	public boolean validate(Object value, String fieldPath, Validator validator) {
		val config = (Configuration) value;
		val servers = config.getServers();

		boolean valid = true;
		String serverId;
		int agentCount = 0;

		if (config.getProducers() != null) {
			agentCount += config.getProducers().size();
			for (Producer producer : config.getProducers()) {
				serverId = producer.getConnection().getServerId();
				if (servers.get(serverId) == null) {
					validator.addError(String.format("Invalid server id \"%s\" in producer connection \"%s\"",
							serverId, producer.getDescription()));
					valid = false;
				}
			}
		}

		if (config.getConsumers() != null) {
			agentCount += config.getConsumers().size();
			for (Consumer consumer : config.getConsumers()) {
				serverId = consumer.getConnection().getServerId();
				if (servers.get(serverId) == null) {
					validator.addError(String.format("Invalid server id \"%s\" in consumer connection \"%s\"",
							serverId, consumer.getDescription()));
					valid = false;
				}
			}
		}

		if (agentCount == 0) {
			validator.addError("There must be at least one producer or one consumer defined");
			valid = false;
		}

		return valid;
	}
}
