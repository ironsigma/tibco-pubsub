package com.hawkprime.tibco;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

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
		try {

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					for (val agent : agents) {
						agent.shutdown();
					}
					log.info("Done.");
				}
			});

			config = Configuration.load(new FileInputStream(new File(configFileName)));

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

		} catch (FileNotFoundException e) {
			log.error("Configuration file \"{}\" not found, use --config to specify fullpath", configFileName);

		} catch (InvalidConfigurationException e) {
			log.error(e.getMessage());
			log.debug(e.getCause().toString());
		}
	}

	private boolean parseCommandLine(String[] commandLineArguments) {
		boolean validCommandLine = true;
		int argumentIndex = 0;
		while (argumentIndex < commandLineArguments.length) {
			switch(commandLineArguments[argumentIndex]) {

			case "--config":
			case "-c":
				if (argumentIndex + 1 < commandLineArguments.length) {
					argumentIndex += 1;
					configFileName = commandLineArguments[argumentIndex];

				} else {
					log.error("file path required after configuration option");
					validCommandLine = false;
				}
				break;

			default:
				log.error("Unknown command line argument \"{}\"", commandLineArguments[argumentIndex]);
				validCommandLine = false;
			}

			argumentIndex++;
		}
		return validCommandLine;
	}

	public static Configuration getConfiguration() {
		return config;
	}

	// CHECKSTYLE IGNORE UncommentedMain
	public static void main(String[] commandLineArguments) {
		log.info("Tibco Pub/Sub v3.0");
		val app = new TibcoPubSub();

		if (app.parseCommandLine(commandLineArguments)) {
			app.start();
		}
	}
}
