package com.hawkprime.tibco.config;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import com.hawkprime.tibco.config.validation.ConnectionValidator;
import com.hawkprime.tibco.config.xstream.ServerReferenceConverter;
import com.hawkprime.validation.annotations.InRange;
import com.hawkprime.validation.annotations.NotBlank;
import com.hawkprime.validation.annotations.Required;
import com.hawkprime.validation.annotations.ValidatorClass;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@Data
@Builder
@XStreamAlias("connection")
@ValidatorClass(ConnectionValidator.class)
public class Connection {
	public static final int DEFAULT_CONNECITON_COUNT = 2;
	public static final int DEFAULT_WORKER_THREAD_COUNT = 10;

	@Required
	@NotBlank
	@XStreamAlias("server")
	@XStreamConverter(ServerReferenceConverter.class)
	private String serverId;

	@XStreamOmitField
	@Setter(AccessLevel.NONE)
	private Server server;

	@NotBlank
	@Getter(AccessLevel.NONE)
	private String user;

	@NotBlank
	@Getter(AccessLevel.NONE)
	private String password;

	@Required
	@NotBlank
	private String queueName;

	@InRange(min=1, max=100)
	private Integer connectionCount;

	@InRange(min=1, max=100)
	private Integer workerThreadCount;

	public void setServer(Server server) {
		val serverBuilder = server.toBuilder();
		this.server = serverBuilder
				.user(user)
				.password(password)
				.build();
	}
}
