package ch.admin.seco.jobroom.security.registration.eiam;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

class WebServiceHttpClientBuilder {

    private static final int DEFAULT_CONNECTION_TIMEOUT = -1;
    private static final int DEFAULT_READ_TIMEOUT = -1;
    private static final int DEFALT_CONNECTION_REQUEST_TIMEOUT = -1;
    private static final int DEFAULT_POOL_SIZE = 50;

    private final HttpClientBuilder httpClientBuilder;

    private WebServiceSSLContextBuilder webServiceSSLContextBuilder;

    WebServiceHttpClientBuilder() {
        httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor());
        this.setTimeouts(DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFALT_CONNECTION_REQUEST_TIMEOUT)
                .setPoolSize(DEFAULT_POOL_SIZE);
    }

    public WebServiceHttpClientBuilder setCredentials(Credentials credentials) {
        CredentialsProvider credPv = new BasicCredentialsProvider();
        credPv.setCredentials(AuthScope.ANY, credentials);
        httpClientBuilder.setDefaultCredentialsProvider(credPv);
        return this;
    }

    public void setProxy(HttpHost httpHost) {
        httpClientBuilder.setProxy(httpHost);
    }

    public WebServiceHttpClientBuilder allowAllHostnameVerifier() {
        httpClientBuilder.setSSLHostnameVerifier(new NoopHostnameVerifier());
        return this;
    }

    public HttpClient build() throws Exception {
        if (webServiceSSLContextBuilder != null) {
            httpClientBuilder.setSSLContext(webServiceSSLContextBuilder.build());
        }
        return httpClientBuilder.build();
    }

    public WebServiceHttpClientBuilder setSSLContextBuilder(WebServiceSSLContextBuilder webServiceSSLContextBuilder) {
        this.webServiceSSLContextBuilder = webServiceSSLContextBuilder;
        return this;
    }

    /**
     * Setting the webservices timeouts.
     *
     * @param connectTimeout           Determines the timeout in <b>milliseconds</b> until a connection is established.
     *                                 A timeout value of zero is interpreted as an infinite timeout.
     *                                 A negative value is interpreted as undefined (system default).
     * @param socketTimeout            Set the socket read timeout in <b>milliseconds</b> for the underlying HttpClient.
     *                                 A timeout value of 0 specifies an infinite timeout.
     * @param connectionRequestTimeout Set the timeout in <b>milliseconds</b> used when requesting a connection from the
     *                                 connection manager using the underlying HttpClient.
     *                                 A timeout value of 0 specifies an infinite timeout.
     * @return the builder
     */
    public WebServiceHttpClientBuilder setTimeouts(int connectTimeout, int socketTimeout, int connectionRequestTimeout) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .build();
        httpClientBuilder.setDefaultRequestConfig(requestConfig);
        return this;
    }

    WebServiceHttpClientBuilder setPoolSize(int maxConnTotal) {
        httpClientBuilder.setMaxConnTotal(maxConnTotal);
        return this;
    }

    static class WebServiceSSLContextBuilder {

        private final SSLContextBuilder sslContextBuilder;

        WebServiceSSLContextBuilder() {
            this.sslContextBuilder = new SSLContextBuilder();
        }

        public WebServiceSSLContextBuilder setKeystore(Resource keystoreLocation, String keystorePassword, String privateKeyName) {
            Assert.notNull(keystorePassword, "a keystore password is required");
            Assert.notNull(privateKeyName, "a private key name (certifacte alias) is required");
            final char[] storePassword = keystorePassword.toCharArray();
            try {
                this.sslContextBuilder.loadKeyMaterial(
                        keystoreLocation.getFile(),
                        storePassword,
                        storePassword,
                        (aliases, socket) -> {
                            if (aliases.containsKey(privateKeyName)) {
                                return privateKeyName;
                            }
                            return null;
                        });
            } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException | UnrecoverableKeyException e) {
                throw new IllegalArgumentException(String.format("Could not load Keystore material: %s", keystoreLocation.getFilename()), e);
            }
            return this;
        }

        public WebServiceSSLContextBuilder setTruststore(Resource truststoreLocation, String truststorePassword) {
            try {
                this.sslContextBuilder.loadTrustMaterial(
                        truststoreLocation.getFile(),
                        truststorePassword == null ? null : truststorePassword.toCharArray()
                );
            } catch (NoSuchAlgorithmException | KeyStoreException | IOException | CertificateException e) {
                throw new IllegalArgumentException(String.format("Could not load Truststore material: %s", truststoreLocation.getFilename()), e);
            }
            return this;
        }

        private SSLContext build() throws KeyManagementException, NoSuchAlgorithmException {
            return this.sslContextBuilder.build();
        }

    }
}



