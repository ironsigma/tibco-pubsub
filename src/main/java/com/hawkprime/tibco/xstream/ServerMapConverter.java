package com.hawkprime.tibco.xstream;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hawkprime.tibco.config.Server;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ServerMapConverter implements Converter {
	private static final Logger LOG = LoggerFactory.getLogger(ServerMapConverter.class);

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class cls) {
		return Map.class.isAssignableFrom(cls);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		if (value == null) {
			return;
		}

		@SuppressWarnings("unchecked")
		Map<String, Server> servers = (Map<String, Server>) value;
		for (Server server : servers.values()) {
			writer.startNode("server");
			context.convertAnother(server);
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Map<String, Server> serverMap = new HashMap<String, Server>();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			Server server = (Server) context.convertAnother(reader, Server.class);
			if (server.getId() == null) {
				LOG.error("Server wihtout ID, skipping: {}", server);
				continue;
			}
			serverMap.put(server.getId(), server);
			reader.moveUp();
		}
		return serverMap;
	}

}
