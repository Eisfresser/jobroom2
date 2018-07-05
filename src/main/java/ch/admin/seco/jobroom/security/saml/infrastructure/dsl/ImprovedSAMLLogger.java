package ch.admin.seco.jobroom.security.saml.infrastructure.dsl;

import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.util.SAMLUtil;

public class ImprovedSAMLLogger extends SAMLDefaultLogger {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImprovedSAMLLogger.class);

    @Override
    public void log(String operation, String result, SAMLMessageContext context, Authentication a, Exception e) {
        prepareLogMessages();
        prepareLogErrors();

        super.log(operation, result, context, a, e);

        if (e != null) {
            LOGGER.info("Failing Saml Assertion: {}", extractSamlAssertion(context));
        }
    }

    private void prepareLogMessages() {
        if (LOGGER.isTraceEnabled()) {
            super.setLogMessages(true);
        } else {
            super.setLogMessages(false);
        }
    }

    private void prepareLogErrors() {
        if (LOGGER.isDebugEnabled()) {
            super.setLogErrors(true);
        } else {
            super.setLogErrors(false);
        }
    }

    private String extractSamlAssertion(SAMLMessageContext context) {
        try {
            if (context.getInboundSAMLMessage() != null) {
                return XMLHelper.nodeToString(SAMLUtil.marshallMessage(context.getInboundSAMLMessage()));
            }
            if (context.getOutboundSAMLMessage() != null) {
                return XMLHelper.nodeToString(SAMLUtil.marshallMessage(context.getOutboundSAMLMessage()));
            }
        } catch (MessageEncodingException e1) {
            LOGGER.warn("Error marshaling message during logging", e1);
        }
        return "";
    }
}
