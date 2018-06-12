package ch.admin.seco.jobroom.security.saml.dsl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.common.SAMLException;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.ws.message.encoder.MessageEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLConstants;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.context.SAMLContextProvider;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.log.SAMLLogger;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.security.saml.util.SAMLUtil;
import org.springframework.security.saml.websso.WebSSOProfile;
import org.springframework.security.saml.websso.WebSSOProfileOptions;


public class SAMLDslEntryPoint extends SAMLEntryPoint {

    /**
     * Metadata manager, cannot be null, must be set.
     * It is set directly in the custom config, so can be optional here.
     * User could override it if desired.
     *
     * @param metadata manager
     */
    @Autowired(required = false)
    @Override
    public void setMetadata(MetadataManager metadata) {
        super.setMetadata(metadata);
    }

    /**
     * Logger for SAML events, cannot be null, must be set.
     *
     * @param samlLogger logger
     *                   It is set in the custom config, so can be optional here.
     *                   User could override it if desired.
     */
    @Autowired(required = false)
    @Override
    public void setSamlLogger(SAMLLogger samlLogger) {
        super.setSamlLogger(samlLogger);
    }

    /**
     * Profile for consumption of processed messages, cannot be null, must be set.
     * It is set in the custom config, so can be optional here.
     * User could override it if desired.
     *
     * @param webSSOprofile profile
     */
    @Autowired(required = false)
    @Qualifier("webSSOprofile")
    @Override
    public void setWebSSOprofile(WebSSOProfile webSSOprofile) {
        super.setWebSSOprofile(webSSOprofile);
    }

    /**
     * Sets entity responsible for populating local entity context data.
     * It is set in the custom config, so can be optional here.
     * User could override it if desired.
     *
     * @param contextProvider provider implementation
     */
    @Autowired(required = false)
    @Override
    public void setContextProvider(SAMLContextProvider contextProvider) {
        super.setContextProvider(contextProvider);
    }

    /**
     * This method overwrites the one in SAMLEntryPoint in order to allow us to manipulate
     * the authentication request sent to the eIAM. Per default the authentication request
     * contains the extended set of authentication methods including single-factor
     * authentication (mTAN, smartcard-PKI, software-PKI, kerberos and username/password).
     * If the login URL contains the strong=true parameter, two-factor authentication is
     * enforced (by not allowing username/password authentication).
     *
     * Note, that the method uses its own initializeSSO method to handle the switching
     * between the two cases (except of this switch the initializeSSO method is largely
     * a copy of the one in SAMLEntryPoint).
     *
     * @param request   http request
     * @param response  http response
     * @param e        exception causing this entry point to be invoked or null when EntryPoint is invoked directly
     * @throws IOException      error sending response
     * @throws ServletException error initializing SAML protocol
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

        try {

            SAMLMessageContext context = contextProvider.getLocalAndPeerEntity(request, response);

            if (isECP(context)) {
                initializeECP(context, e);
            } else if (isDiscovery(context)) {
                initializeDiscovery(context);
            } else {
                if (request.getParameter("strong") != null && request.getParameter("strong").equalsIgnoreCase(Boolean.TRUE.toString())) {
                    initializeSSO(context, e, true);
                } else {
                    initializeSSO(context, e, false);
                }
            }

        } catch (SAMLException | MetadataProviderException | MessageEncodingException e1) {
            logger.debug("Error initializing entry point", e1);
            throw new ServletException(e1);
        }

    }

    private void initializeSSO(SAMLMessageContext context, AuthenticationException e, boolean twoFactorRequired) throws MetadataProviderException, SAMLException, MessageEncodingException {

        // Generate options for the current SSO request
        WebSSOProfileOptions options = getProfileOptions(context, e);

        // switch between allowed one-factor and forced two-factor authentication
        if (!twoFactorRequired) {
            options.setAuthnContexts(SAMLConfigurer.PASSWORD_ALLOWED_AUTHN_CTX);
        } else {
            options.setAuthnContexts(SAMLConfigurer.DEFAULT_AUTHN_CTX);
        }

        // Determine the assertionConsumerService to be used
        AssertionConsumerService consumerService = SAMLUtil.getConsumerService((SPSSODescriptor) context.getLocalEntityRoleMetadata(), options.getAssertionConsumerIndex());

        // HoK WebSSO
        if (SAMLConstants.SAML2_HOK_WEBSSO_PROFILE_URI.equals(consumerService.getBinding())) {
            if (webSSOprofileHoK == null) {
                logger.warn("WebSSO HoK profile was specified to be used, but profile is not configured in the EntryPoint, HoK will be skipped");
            } else {
                logger.debug("Processing SSO using WebSSO HolderOfKey profile");
                webSSOprofileHoK.sendAuthenticationRequest(context, options);
                samlLogger.log(SAMLConstants.AUTH_N_REQUEST, SAMLConstants.SUCCESS, context);
                return;
            }
        }

        // Ordinary WebSSO
        logger.debug("Processing SSO using WebSSO profile");
        webSSOprofile.sendAuthenticationRequest(context, options);
        samlLogger.log(SAMLConstants.AUTH_N_REQUEST, SAMLConstants.SUCCESS, context);
    }

}
