package ch.admin.seco.jobroom.security.saml;

import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!security-mock")
public class SamlProperties {

    @Value("${security.saml.idpConfigPath}")
    private String idpConfigPath;

    @Value("${security.saml.keystorePath}")
    private String keystorePath;

    @Value("${security.saml.keystorePassword}")
    private String keystorePassword;

    @Value("${security.saml.keystorePrivateKeyName}")
    private String keystorePrivateKeyName;

    @Value("${security.saml.keystorePrivateKeyPassword}")
    private String keystorePrivateKeyPassword;

    @Value("${security.saml.externalContextScheme}")
    private String externalContextScheme;

    @Value("${security.saml.externalContextServerName}")
    private String externalContextServerName;

    @Value("${security.saml.externalContextServerPort}")
    private String externalContextServerPort;

    @Value("${security.saml.externalContextPath}")
    private String externalContextPath;

    @Value("${security.saml.entityId:}")
    private String entityId;

    @Value("${security.saml.entityAlias:}")
    public String entityAlias;

    public String getIdpConfigPath() {
        return idpConfigPath;
    }

    public void setIdpConfigPath(String idpConfigPath) {
        this.idpConfigPath = idpConfigPath;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public String getKeystorePrivateKeyName() {
        return keystorePrivateKeyName;
    }

    public void setKeystorePrivateKeyName(String keystorePrivateKeyName) {
        this.keystorePrivateKeyName = keystorePrivateKeyName;
    }

    public String getKeystorePrivateKeyPassword() {
        return keystorePrivateKeyPassword;
    }

    public void setKeystorePrivateKeyPassword(String keystorePrivateKeyPassword) {
        this.keystorePrivateKeyPassword = keystorePrivateKeyPassword;
    }

    public String getExternalContextScheme() {
        return externalContextScheme;
    }

    public void setExternalContextScheme(String externalContextScheme) {
        this.externalContextScheme = externalContextScheme;
    }

    public String getExternalContextServerName() {
        return externalContextServerName;
    }

    public void setExternalContextServerName(String externalContextServerName) {
        this.externalContextServerName = externalContextServerName;
    }

    public String getExternalContextServerPort() {
        return externalContextServerPort;
    }

    public void setExternalContextServerPort(String externalContextServerPort) {
        this.externalContextServerPort = externalContextServerPort;
    }

    public String getExternalContextPath() {
        return externalContextPath;
    }

    public void setExternalContextPath(String externalContextPath) {
        this.externalContextPath = externalContextPath;
    }

    public String getEntityId() {
        return StringUtils.isEmpty(this.entityId) ? null : entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityAlias() {
        return StringUtils.isEmpty(this.entityAlias) ? null : entityAlias;
    }

    public void setEntityAlias(String entityAlias) {
        this.entityAlias = entityAlias;
    }
}
