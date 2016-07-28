package com.hawkprime.tibco.config.validation;

import lombok.val;

import com.hawkprime.tibco.config.Connection;
import com.hawkprime.validation.ObjectValidator;
import com.hawkprime.validation.Validator;

public class ConnectionValidator implements ObjectValidator {

	@Override
	public boolean validate(Object value, String fieldPath, Validator validator) {
		val conn = (Connection) value;
		if (conn.getConnectionCount() == null) {
			conn.setConnectionCount(Connection.DEFAULT_CONNECITON_COUNT);
		}
		if (conn.getWorkerThreadCount() == null) {
			conn.setWorkerThreadCount(Connection.DEFAULT_WORKER_THREAD_COUNT);
		}
		return true;
	}
}
