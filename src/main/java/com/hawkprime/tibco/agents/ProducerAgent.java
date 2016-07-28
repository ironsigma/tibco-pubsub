package com.hawkprime.tibco.agents;

import lombok.extern.slf4j.Slf4j;

import com.hawkprime.tibco.config.Producer;

@Slf4j
public class ProducerAgent implements PubSubAgent {

	public ProducerAgent(Producer producer) {
		log.info("Creating new producer: {}", producer.getDescription());
	}

	@Override
	public void shutdown() {
	}

}
