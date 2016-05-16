package com.hawkprime.tibco.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ServerReferenceConverter implements Converter {
	private static final String ID_ATTRIBUTE = "id";

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class cls) {
		return String.class.equals(cls);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		writer.addAttribute(ID_ATTRIBUTE, (String) value);
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return reader.getAttribute(ID_ATTRIBUTE);
	}

}
