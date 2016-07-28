package com.hawkprime.tibco.config;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import com.hawkprime.tibco.config.validation.ConfigurationValidation;
import com.hawkprime.tibco.config.xstream.ServerMapConverter;
import com.hawkprime.validation.Validator;
import com.hawkprime.validation.annotations.Optional;
import com.hawkprime.validation.annotations.Required;
import com.hawkprime.validation.annotations.ValidatorClass;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.ConversionException;

@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XStreamAlias("configuration")
@ValidatorClass(ConfigurationValidation.class)
public class Configuration {

	@Required
	@XStreamConverter(ServerMapConverter.class)
	@Singular
	private Map<String, Server> servers;

	@Optional
	@Singular
	private List<Producer> producers;

	@Optional
	@Singular
	private List<Consumer> consumers;

	@Required
	private Logger logger;

	public static Configuration load(InputStream stream) throws InvalidConfigurationException {
		try {
			val config = (Configuration) createXStream().fromXML(stream);
			val validator = new Validator();

			validator.validate(config);

			if (validator.hasWarnings()) {
				for (val warning : validator.getWarnings()) {
					log.warn(warning);
				}
			}

			if (validator.hasErrors()) {
				log.error("Invalid configuration");
				for (val error : validator.getErrors()) {
					log.error(error);
				}
				throw new InvalidConfigurationException("Configuration contains errors");
			}

			val servers = config.getServers();
			if (config.hasProducers()) {
				for (val producer : config.producers) {
					val connection = producer.getConnection();
					connection.setServer(servers.get(connection.getServerId()));
				}
			}

			if (config.hasConsumers()) {
				for (val consumer : config.consumers) {
					val connection = consumer.getConnection();
					connection.setServer(servers.get(connection.getServerId()));
				}
			}

			return config;
		} catch (ConversionException ex) {
			throw new InvalidConfigurationException("Invalid configuration XML", ex);
		}
	}

	private static XStream createXStream() {
		val xstream = new XStream();
		xstream.processAnnotations(Configuration.class);
		xstream.aliasSystemAttribute(null, "class");
		return xstream;
	}

	public String toXml() {
		return createXStream().toXML(this);
	}

	public boolean hasConsumers() {
		return consumers != null && !consumers.isEmpty();
	}

	public boolean hasProducers() {
		return producers != null && !producers.isEmpty();
	}
}
