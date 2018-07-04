package ch.admin.seco.jobroom.security.saml.infrastructure.dsl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.log.SAMLDefaultLogger;

public class ImprovedSAMLLogger extends SAMLDefaultLogger {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImprovedSAMLLogger.class);

    @Override
    public void log(String operation, String result, SAMLMessageContext context, Authentication a, Exception e) {
        if (LOGGER.isTraceEnabled()) {
            super.setLogMessages(true);
        } else {
            super.setLogMessages(false);
        }
        super.log(operation, result, context, a, e);
    }

}
