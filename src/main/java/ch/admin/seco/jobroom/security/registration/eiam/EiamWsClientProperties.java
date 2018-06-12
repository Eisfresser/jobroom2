package ch.admin.seco.jobroom.security.registration.eiam;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "security.eiam.wsclient")
@Validated
public class EiamWsClientProperties {

    @NotBlank
    private String endpointAddress;

    /**
     * The name of the client the user belongs to, as it is known by nevisIDM.
     */
    @NotBlank
    private String clientName;

    /**
     * Validate request and responses against the XSD Schema.
     * <p>
     * Default: {@code false}
     * </p>
     */
    private boolean validationEnabled = false;

    @Valid
    private KeystoreProperties keystore;

    @Valid
    private TruststoreProperties truststore;

    /**
     * This propertiy essentially turns hostname verification on or off.
     * <p>
     * Default: {@code true (No hostname verification)}
     * </p>
     */
    private boolean allowAllHostnameVerifier = true;

    /**
     * Determines the timeout in milliseconds until a connection is established.
     * A timeout value of zero is interpreted as an infinite timeout.
     * <p>
     * A timeout value of zero is interpreted as an infinite timeout.
     * A negative value is interpreted as undefined (system default).
     * </p>
     * <p>
     * Default: {@code -1}
     * </p>
     */
    private int connectTimeout = -1;


    /**
     * Returns the timeout in milliseconds used when requesting a connection
     * from the connection manager. A timeout value of zero is interpreted
     * as an infinite timeout.
     * <p>
     * A timeout value of zero is interpreted as an infinite timeout.
     * A negative value is interpreted as undefined (system default).
     * </p>
     * <p>
     * Default: {@code -1}
     * </p>
     */
    private int connectionRequestTimeout = -1;

    /**
     * Defines the socket timeout ({@code SO_TIMEOUT}) in milliseconds,
     * which is the timeout for waiting for data  or, put differently,
     * a maximum period inactivity between two consecutive data packets).
     * <p>
     * A timeout value of zero is interpreted as an infinite timeout.
     * A negative value is interpreted as undefined (system default).
     * </p>
     * <p>
     * Default: {@code -1}
     * </p>
     */
    private int sockedTimeout = -1;

    /**
     * Sets the maximum number of connections allowed for the underlying HttpClient.
     * <p>
     * Default: {@code 20}
     * </p>
     */
    private int maxConnTotal = 20;


    /**
     * The User which is used in order to test the connection to the eiam-ws.
     * <p>
     * Default: {@code MONITORING-USER}
     * </p>
     */
    @NotBlank
    private String monitoringUserExternalId = "MONITORING-USER";

    public boolean isValidationEnabled() {
        return validationEnabled;
    }

    public void setValidationEnabled(boolean validationEnabled) {
        this.validationEnabled = validationEnabled;
    }

    public String getEndpointAddress() {
        return endpointAddress;
    }

    public void setEndpointAddress(String endpointAddress) {
        this.endpointAddress = endpointAddress;
    }

    public boolean isAllowAllHostnameVerifier() {
        return allowAllHostnameVerifier;
    }

    public void setAllowAllHostnameVerifier(boolean allowAllHostnameVerifier) {
        this.allowAllHostnameVerifier = allowAllHostnameVerifier;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSockedTimeout() {
        return sockedTimeout;
    }

    public void setSockedTimeout(int sockedTimeout) {
        this.sockedTimeout = sockedTimeout;
    }

    public int getMaxConnTotal() {
        return maxConnTotal;
    }

    public void setMaxConnTotal(int maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public String getMonitoringUserExternalId() {
        return monitoringUserExternalId;
    }

    public void setMonitoringUserExternalId(String monitoringUserExternalId) {
        this.monitoringUserExternalId = monitoringUserExternalId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public KeystoreProperties getKeystore() {
        return keystore;
    }

    public void setKeystore(KeystoreProperties keystore) {
        this.keystore = keystore;
    }

    public TruststoreProperties getTruststore() {
        return truststore;
    }

    public void setTruststore(TruststoreProperties truststore) {
        this.truststore = truststore;
    }

    public boolean hasSSLProperties() {
        return this.keystore != null || this.truststore != null;
    }

    @Override
    public String toString() {
        return "EiamWsClientProperties{" +
            "endpointAddress='" + endpointAddress + '\'' +
            ", clientName='" + clientName + '\'' +
            ", validationEnabled=" + validationEnabled +
            ", keystore=" + keystore +
            ", truststore=" + truststore +
            ", allowAllHostnameVerifier=" + allowAllHostnameVerifier +
            ", connectTimeout=" + connectTimeout +
            ", connectionRequestTimeout=" + connectionRequestTimeout +
            ", sockedTimeout=" + sockedTimeout +
            ", maxConnTotal=" + maxConnTotal +
            ", monitoringUserExternalId='" + monitoringUserExternalId + '\'' +
            '}';
    }

    public static class KeystoreProperties {

        @NotNull
        private Resource location;

        @NotNull
        private String password;

        @NotNull
        private String privateKeyName;

        public Resource getLocation() {
            return location;
        }

        public void setLocation(Resource location) {
            this.location = location;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPrivateKeyName() {
            return privateKeyName;
        }

        public void setPrivateKeyName(String privateKeyName) {
            this.privateKeyName = privateKeyName;
        }

        @Override
        public String toString() {
            return "KeystoreProperties{" +
                "location=" + location +
                ", password='" + password + '\'' +
                ", privateKeyName='" + privateKeyName + '\'' +
                '}';
        }
    }

    public static class TruststoreProperties {

        @NotNull
        private Resource location;

        private String password;

        public Resource getLocation() {
            return location;
        }

        public void setLocation(Resource location) {
            this.location = location;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            return "TruststoreProperties{" +
                "location=" + location +
                ", password='" + password + '\'' +
                '}';
        }
    }

}
