package com.hawkprime.tibco.config.validation;

import lombok.val;

import com.hawkprime.tibco.config.Server;
import com.hawkprime.validation.ObjectValidator;
import com.hawkprime.validation.Validator;

public class TiboHostValidation implements ObjectValidator {

	@Override
	public boolean validate(Object value, String fieldPath, Validator validator) {
		val server = (Server) value;
		val host = server.getHost();
		boolean valid = true;

		if (host == null) {
			validator.addError("Host cannot be empty");
			valid = false;

		} else if (host.startsWith("ssl://")) {
			if (server.getCertificateAuthorityFile() == null
					|| server.getServerKeyFile() == null
					|| server.getClientKeyFile() == null
					|| server.getKeyPassword() == null) {
				validator.addError(String.format("Server \"%s\" with SSL host \"%s\" requires "
						+ "certificate authority, server key, client key and key password",
						server.getId(), server.getHost()));
				valid = false;
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

		return valid;
	}

}
