package com.hawkprime.tibco.agents;

import lombok.extern.slf4j.Slf4j;

import com.hawkprime.tibco.config.Consumer;

@Slf4j
public class ConsumerAgent implements PubSubAgent {

	public ConsumerAgent(Consumer consumer) {
		log.info("Creating new consumer: {}", consumer.getDescription());
	}

	@Override
	public void shutdown() {
	}

}
