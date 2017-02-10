/*******************************************************************************
 * Copyright (c) 2017 Red Hat Inc and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.kura.simulator.app;

import org.eclipse.kapua.kura.simulator.payload.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDefaultApplication implements Application {

	private static final Logger logger = LoggerFactory.getLogger(AbstractDefaultApplication.class);

	private final Descriptor descriptor;

	protected abstract void processRequest(final Request request);

	public AbstractDefaultApplication(final String applicationId) {
		this.descriptor = new Descriptor(applicationId);
	}

	@Override
	public Descriptor getDescriptor() {
		return this.descriptor;
	}

	@Override
	public Handler createHandler(final ApplicationContext context) {
		return new Handler() {
			@Override
			public void processMessage(final Message message) {
				process(context, message);
			}
		};
	}

	protected void process(final ApplicationContext context, final Message message) {
		logger.debug("Received message: {}", message);

		final Request request;
		try {
			request = Request.parse(context, message);
		} catch (final Exception e) {
			logger.warn("Failed to parse request message", e);
			return;
		}

		try {
			processRequest(request);
		} catch (final Exception e) {
			logger.info("Failed to process request", e);
			request.sendError(e);
		}
	}

}
