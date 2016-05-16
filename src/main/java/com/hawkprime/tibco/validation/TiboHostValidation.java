package com.hawkprime.tibco.validation;

import com.hawkprime.tibco.config.Server;
import com.hawkprime.validation.ObjectValidator;
import com.hawkprime.validation.Validator;

public class TiboHostValidation implements ObjectValidator {

	@Override
	public boolean validate(Object value, String fieldPath, Validator validator) {
		Server server = (Server) value;
		String host = server.getHost();

		if (host == null) {
			return false;

		} else if (host.startsWith("ssl://")) {
			if (server.getCertificateAuthorityFile() == null
					|| server.getServerKeyFile() == null
					|| server.getClientKeyFile() == null
					|| server.getKeyPassword() == null) {
				validator.addError(String.format("Server \"%s\" with SSL host \"%s\" requires "
						+ "certificate authority, server key, client key and key password",
						server.getId(), server.getHost()));
				return false;
			}

		} else if (host.startsWith("tcp://")) {
			if (server.getCertificateAuthorityFile() != null
					|| server.getServerKeyFile() != null
					|| server.getClientKeyFile() != null
					|| server.getKeyPassword() != null) {
				validator.addWarning(String.format("Server \"%s\" specifies TCP host \"%s\" and SSL configuration, "
						+ "ignoring SSL configuration", server.getId(), server.getHost()));
			}
		}
		return true;
	}

}
