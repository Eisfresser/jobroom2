package ch.admin.seco.jobroom.config;

import org.apache.commons.lang.StringUtils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("saml-security")
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "security.saml")
public class SamlProperties {
    private String idpConfigPath;

    private String keystorePath;
    private String keystorePassword;
    private String keystorePrivateKeyName;
    private String keystorePrivateKeyPassword;
    private String externalContextScheme;
    private String externalContextServerName;
    private String externalContextServerPort;
    private String externalContextPath;
    private String entityId;
    //public String entityAlias;

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

    /*public String getEntityAlias() {
        return StringUtils.isEmpty(this.entityAlias) ? null : entityAlias;
    }

    public void setEntityAlias(String entityAlias) {
        this.entityAlias = entityAlias;
    }*/
}
