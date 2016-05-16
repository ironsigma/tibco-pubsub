package com.hawkprime.tibco.config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import lombok.Getter;
import lombok.ToString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hawkprime.tibco.xstream.ServerMapConverter;
import com.hawkprime.validation.Validator;
import com.hawkprime.validation.annotations.Optional;
import com.hawkprime.validation.annotations.Required;
import com.hawkprime.validation.annotations.ValidatorClass;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@Singleton
@ToString
@XStreamAlias("configuration")
@ValidatorClass(com.hawkprime.tibco.validation.ConfigurationValidation.class)
public class Configuration {
	private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

	@Required
	@Getter
	@XStreamConverter(ServerMapConverter.class)
	private Map<String, Server> servers;

	@Optional
	@Getter
	private List<Producer> producers;

	@Optional
	@Getter
	private List<Consumer> consumers;

	@PostConstruct
	private void init() {
		System.err.println("loading...");
	}

	public static Configuration load(InputStream stream) {
		Configuration config = (Configuration) createXStream().fromXML(stream);
		Validator validator = new Validator();

		validator.validate(config);

		if (validator.hasWarnings()) {
			for (String warning : validator.getWarnings()) {
				LOG.warn(warning);
			}
		}

		if (!validator.hasErrors()) {
			LOG.error("Invalid configuration");
			for (String error : validator.getErrors()) {
				LOG.error(error);
			}
			return null;
		}

		return config;
	}

	public String toXml() {
		return createXStream().toXML(this);
	}

	private static XStream createXStream() {
		XStream xstream = new XStream();
		xstream.processAnnotations(Configuration.class);
		xstream.aliasSystemAttribute(null, "class");
		return xstream;
	}

	public Server getServer(String serverId) {
		if (servers == null) {
			return null;
		}
		return servers.get(serverId);
	}

	public Configuration addServer(Server server) {
		if (servers == null) {
			servers = new HashMap<String, Server>();
		}
		servers.put(server.getId(), server);
		return this;
	}

	public Configuration addProducer(Producer producer) {
		if (producers == null) {
			producers = new LinkedList<Producer>();
		}
		producers.add(producer);
		return this;
	}

	public Configuration addConsumer(Consumer consumer) {
		if (consumers == null) {
			consumers = new LinkedList<Consumer>();
		}
		consumers.add(consumer);
		return this;
	}
}
