package com.hawkprime.tibco;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;

import com.hawkprime.tibco.agents.ConsumerAgent;
import com.hawkprime.tibco.agents.ProducerAgent;
import com.hawkprime.tibco.agents.PubSubAgent;
import com.hawkprime.tibco.config.Configuration;
import com.hawkprime.tibco.config.InvalidConfigurationException;

@Slf4j
public final class TibcoPubSub {
	private static Configuration config;
	private String configFileName = "tibco-pubsub.xml";
	private List<PubSubAgent> agents = new LinkedList<PubSubAgent>();

	private TibcoPubSub() {
		/* empty */
	}

	private void start() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				for (val agent : agents) {
					agent.shutdown();
				}
				log.info("Done.");
			}
		});

		log.info("Tibco Pub/Sub v{}", config.getBuildVersion());
		log.debug("Effective configuration: \"{}\"\n{}", configFileName, config.toXml());

		if (config.hasProducers()) {
			for (val producer : config.getProducers()) {
				agents.add(new ProducerAgent(producer));
			}
		}

		if (config.hasConsumers()) {
			for (val consumer : config.getConsumers()) {
				agents.add(new ConsumerAgent(consumer));
			}
		}
	}

	private void creteLog() {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n");
		encoder.setContext(context);
		encoder.start();

		String logFile = getConfig().getLogger().getFileName();

		RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<ILoggingEvent>();
		fileAppender.setFile(logFile);
		fileAppender.setEncoder(encoder);
		fileAppender.setContext(context);

		FixedWindowRollingPolicy fixedWindowRollingPolicy = new FixedWindowRollingPolicy();
		fixedWindowRollingPolicy.setFileNamePattern(logFile + ".%i.gz");
		fixedWindowRollingPolicy.setMaxIndex(getConfig().getLogger().getMaxNumFiles());
		fixedWindowRollingPolicy.setContext(context);
		fixedWindowRollingPolicy.setParent(fileAppender);

		SizeBasedTriggeringPolicy<ILoggingEvent> sizeBasedTriggerPolicy = new SizeBasedTriggeringPolicy<ILoggingEvent>();
		sizeBasedTriggerPolicy.setMaxFileSize(getConfig().getLogger().getMaxFileSize());
		sizeBasedTriggerPolicy.setContext(context);

		fileAppender.setRollingPolicy(fixedWindowRollingPolicy);
		fileAppender.setTriggeringPolicy(sizeBasedTriggerPolicy);

		sizeBasedTriggerPolicy.start();
		fixedWindowRollingPolicy.start();
		fileAppender.start();

		ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(Logger.ROOT_LOGGER_NAME);

		logger.addAppender(fileAppender);
		logger.setLevel(Level.valueOf(getConfig().getLogger().getLevel().toUpperCase()));
	}

	private void parseCommandLine(String[] commandLineArguments) throws InvalidConfigurationException {
		int argumentIndex = 0;
		while (argumentIndex < commandLineArguments.length) {
			switch(commandLineArguments[argumentIndex]) {

			case "--config":
			case "-c":
				if (argumentIndex + 1 < commandLineArguments.length) {
					argumentIndex += 1;
					configFileName = commandLineArguments[argumentIndex];

				} else {
					throw new InvalidConfigurationException("file path required after configuration option");
				}
				break;

			default:
				throw new InvalidConfigurationException(String.format("Unknown command line argument \"%s\"",
						commandLineArguments[argumentIndex]));
			}

			argumentIndex++;
		}
	}

	public void loadConfig() throws InvalidConfigurationException {
		try {
			config = Configuration.load(new FileInputStream(new File(configFileName)));

			if (this.getClass().getPackage() != null) {
				String version = this.getClass().getPackage().getImplementationVersion();
				if (version == null) {
					version = "local-dev";
				}
				config.setBuildVersion(version);
			}

		} catch (FileNotFoundException e) {
			throw new InvalidConfigurationException(
					String.format("Configuration file \"%s\" not found, use --config to specify fullpath",
							configFileName));
		}

	}

	public static Configuration getConfig() {
		return config;
	}

	// CHECKSTYLE IGNORE UncommentedMain
	public static void main(String[] commandLineArguments) {
		val app = new TibcoPubSub();

		try {

			app.parseCommandLine(commandLineArguments);
			app.loadConfig();
			app.creteLog();
			app.start();

		} catch (InvalidConfigurationException e) {
			log.error(e.getMessage());
			if (e.hasCause()) {
				log.debug(e.getCause().toString());
			}
		}
	}
}
