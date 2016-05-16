package com.hawkprime.tibco.config;

import lombok.Builder;
import lombok.Data;

import com.hawkprime.tibco.xstream.ServerReferenceConverter;
import com.hawkprime.validation.annotations.InRange;
import com.hawkprime.validation.annotations.NotBlank;
import com.hawkprime.validation.annotations.Required;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@Data
@Builder
@XStreamAlias("connection")
public class Connection {
	private static final int DEFAULT_CONNECITON_COUNT = 2;
	private static final int DEFAULT_THREAD_COUNT = 10;

	@Required
	@NotBlank
	@XStreamAlias("server")
	@XStreamConverter(ServerReferenceConverter.class)
	private String serverId;

	@NotBlank
	private String user;

	@NotBlank
	private String password;

	@Required
	@NotBlank
	private String queueName;

	@InRange(min=1, max=100)
	private Integer connectionCount = DEFAULT_CONNECITON_COUNT;

	@InRange(min=1, max=100)
	private Integer workerThreadCount = DEFAULT_THREAD_COUNT;
}
