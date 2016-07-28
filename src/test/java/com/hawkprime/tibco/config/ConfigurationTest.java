package com.hawkprime.tibco.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import lombok.val;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.hawkprime.util.HashMapBuilder;
import com.hawkprime.validation.Validator;

public class ConfigurationTest {
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	private File certAuth;
	private File serverKey;
	private File clientKey;
	private File targetDir;
	private File sourceDir;
	private File storageDir;
	private Map<String, String> filePaths;

	@Before
	public void setup() throws IOException {
		certAuth = testFolder.newFile("ca");
		serverKey = testFolder.newFile("serverKey");
		clientKey = testFolder.newFile("clientKey");

		targetDir = testFolder.newFolder("target");
		sourceDir = testFolder.newFolder("source");
		storageDir = testFolder.newFolder("storage");

		filePaths = new HashMapBuilder<String, String>()
				.put("certAuth", certAuth.getAbsolutePath())
				.put("serverKey", serverKey.getAbsolutePath())
				.put("clientKey", clientKey.getAbsolutePath())
				.put("targetDir", targetDir.getAbsolutePath())
				.put("sourceDir", sourceDir.getAbsolutePath())
				.put("storageDir", storageDir.getAbsolutePath())
				.build();
	}

	@Test
	public void minimumConfigTest() {
		Server server = Server.builder()
			.id("prod-server")
			.host("ssl://tibe08.federated.fds")
			.certificateAuthorityFile(filePaths.get("certAuth"))
			.clientKeyFile(filePaths.get("clientKey"))
			.serverKeyFile(filePaths.get("serverKey"))
			.keyPassword("s3cre7")
			.password("prtuser")
			.port(7252)
			.user("prtuser")
			.build();

		Configuration config = Configuration.builder()
			.logger(Logger.builder()
				.fileName("/var/log/tibco-pubsub.log")
				.maxNumFiles(3)
				.maxFileSize("100MB")
				.level("DEBUG")
				.build())

			.server(server.getId(), server)

			.producer(Producer.builder()
				.profile("at-prod")
				.description("Auto-Tagging Producer")
				.connection(Connection.builder()
					.serverId(server.getId())
					.user("prtuserrw")
					.password("prtuserrw")
					.queueName("M.CSS.PRT.PRODUCT.BRE.TAGGING.SUB.ERROR")
					.build())
				.messageSource(filePaths.get("sourceDir"))
				.messageTarget(filePaths.get("targetDir"))
				.filter(FileFilter.builder()
					.fileNameFilter("*.json")
					.contentFilter("Application: OCMS-ORCH")
					.contentRegExFilter("JMS-Date: \\d+")
					.classFilter("com.hawkprime.tibco.config.FileFilter")
					.build())
				.build())

			.consumer(Consumer.builder()
				.profile("at-stress")
				.description("Auto-Tagging STRESS consumer")
				.connection(Connection.builder()
					.serverId(server.getId())
					.queueName("M.CSS.PRT.PRODUCT.BRE.TAGGING.SUB.ERROR")
					.connectionCount(2)
					.workerThreadCount(10)
					.build())
				.messageStorage(filePaths.get("storageDir"))
				.defaultAction("save")
				.rule(MessageRule.builder()
					.description("Ignore null brand")
					.matchText("Mesage has invalid brand")
					.action("discard")
					.build())
				.rule(MessageRule.builder()
					.description("Ignore invalid PID")
					.matchTextRegEx("No info found for PID: \\d+")
					.action("discard")
					.build())
				.rule(MessageRule.builder()
					.description("Header match")
					.matchHeader("JMS-Source: orch")
					.action("discard")
					.build())
				.rule(MessageRule.builder()
					.description("Header regex match")
					.matchHeaderRegEx("JMS-Timestamp: 1233\\d+")
					.action("discard")
					.build())
				.rule(MessageRule.builder()
					.description("Custom filter")
					.matchClass("com.hawkprime.tibco.config.filters.MessageFilter")
					.action("discard")
					.build())
				.rule(MessageRule.builder()
					.description("Custom action")
					.matchText("foo")
					.actionClass("com.hawkprime.tibco.config.actions.EmailMessage")
					.build())
				.build())
			.build();

		Validator validator = new Validator();
		validator.validate(config);

		assertThat(validator.validate(config), is(true));
	}

	@Test
	public void loadConfigTest() throws InvalidConfigurationException {

		val config = Configuration.load(readXmlTestConfig());
		val servers = config.getServers();

		assertThat(config.getLogger().getFileName(), is("/var/log/tibco-pubsub.log"));
		assertThat(config.getLogger().getMaxFileSize(), is("150MB"));
		assertThat(config.getLogger().getMaxNumFiles(), is(8));
		assertThat(config.getLogger().getLevel(), is("WARN"));

		assertThat(servers.size(), is(2));

		val localServer = config.getServers().get("localvm-server");
		assertThat(localServer.getHost(), is("tcp://192.168.56.102"));
		assertThat(localServer.getPort(), is(7222));
		assertThat(localServer.getUser(), is("admin"));
		assertThat(localServer.getPassword(), is("admpass"));
		assertThat(localServer.getCertificateAuthorityFile(), is(nullValue()));
		assertThat(localServer.getServerKeyFile(), is(nullValue()));
		assertThat(localServer.getClientKeyFile(), is(nullValue()));
		assertThat(localServer.getKeyPassword(), is(nullValue()));

		val prodServer = config.getServers().get("prod-server");
		assertThat(prodServer.getHost(), is("ssl://tibe08.federated.fds"));
		assertThat(prodServer.getPort(), is(7252));
		assertThat(prodServer.getUser(), is("admcsseap"));
		assertThat(prodServer.getPassword(), is("admcsspass"));
		assertThat(prodServer.getCertificateAuthorityFile(), is(filePaths.get("certAuth")));
		assertThat(prodServer.getServerKeyFile(), is(filePaths.get("serverKey")));
		assertThat(prodServer.getClientKeyFile(), is(filePaths.get("clientKey")));
		assertThat(prodServer.getKeyPassword(), is("5ecr37"));

		assertThat(config.getProducers(), is(notNullValue()));
		assertThat(config.getProducers().size(), is(1));

		val producer = config.getProducers().get(0);
		assertThat(producer.getProfile(), is("STRESS"));
		assertThat(producer.getDescription(), is("Post to Auto-Tagging VM queue"));
		assertThat(producer.getMessageSource(), is(filePaths.get("sourceDir")));
		assertThat(producer.getMessageTarget(), is(filePaths.get("targetDir")));

		val connection = producer.getConnection();
		assertThat(connection.getServerId(), is("localvm-server"));
		assertThat(connection.getQueueName(), is("M.CSS.PRT.PRODUCT.BRE.TAGGING.SUB"));
		assertThat(connection.getConnectionCount(), is(Connection.DEFAULT_CONNECITON_COUNT));
		assertThat(connection.getWorkerThreadCount(), is(Connection.DEFAULT_WORKER_THREAD_COUNT));

		val connServer = connection.getServer();
		assertThat(connServer.getHost(), is("tcp://192.168.56.102"));
		assertThat(connServer.getUser(), is("alt-user"));
		assertThat(connServer.getPassword(), is("alt-password"));

		assertThat(producer.getFilters(), is(notNullValue()));
		assertThat(producer.getFilters().size(), is(1));

		val fileFilter = producer.getFilters().get(0);
		assertThat(fileFilter.getFileNameFilter(), is("*.json"));
		assertThat(fileFilter.getContentFilter(), is("Application: OCMS-ORCH"));
		assertThat(fileFilter.getContentRegExFilter(), is("JMS-Data: 204857384293"));
		assertThat(fileFilter.getClassFilter(), is("com.hawkprime.tibco.config.FileFilter"));

		assertThat(config.getConsumers(), is(notNullValue()));
		assertThat(config.getConsumers().size(), is(1));

		val consumer = config.getConsumers().get(0);
		assertThat(consumer.getProfile(), is("SIT"));
		assertThat(consumer.getDescription(), is("Consume error messages from SIT"));
		assertThat(consumer.getMessageStorage(), is(filePaths.get("storageDir")));
		assertThat(consumer.getDefaultAction(), is("save"));

		assertThat(consumer.getRules(), is(notNullValue()));
		assertThat(consumer.getRules().size(), is(2));

		val messageRule1 = consumer.getRules().get(0);
		assertThat(messageRule1.getDescription(), is("Discard PDS Errors"));
		assertThat(messageRule1.getMatchText(), is("Product Data Service Error"));
		assertThat(messageRule1.getAction(), is("discard"));
		assertThat(messageRule1.getActionClass(), is(nullValue()));
		assertThat(messageRule1.getMatchClass(), is(nullValue()));
		assertThat(messageRule1.getMatchHeader(), is(nullValue()));
		assertThat(messageRule1.getMatchHeaderRegEx(), is(nullValue()));
		assertThat(messageRule1.getMatchTextRegEx(), is(nullValue()));

		val messageRule2 = consumer.getRules().get(1);
		assertThat(messageRule2.getDescription(), is("Discard BRE Errors"));
		assertThat(messageRule2.getMatchHeader(), is("Destination: BRE"));
		assertThat(messageRule2.getAction(), is("discard"));
		assertThat(messageRule2.getActionClass(), is(nullValue()));
		assertThat(messageRule2.getMatchClass(), is(nullValue()));
		assertThat(messageRule2.getMatchHeaderRegEx(), is(nullValue()));
		assertThat(messageRule2.getMatchText(), is(nullValue()));
		assertThat(messageRule2.getMatchTextRegEx(), is(nullValue()));

	}

	private InputStream readXmlTestConfig() {
		// Read XML to String
		Scanner s = new Scanner(getClass().getResourceAsStream("/tibco-pubsub-test.xml"));
		s.useDelimiter("\\A");
		String xml = s.next();
		s.close();

		// Replace Tokens
		for (Entry<String, String> entry : filePaths.entrySet()) {
			xml = xml.replace("${" + entry.getKey() + "}", entry.getValue());
		}

		// Create new stream
		return new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8")));
	}
}
