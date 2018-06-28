package ch.admin.seco.jobroom.security.saml.infrastructure.dsl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.metadata.provider.FilesystemMetadataProvider;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.security.x509.CertPathPKIXTrustEvaluator;
import org.opensaml.xml.security.x509.PKIXTrustEvaluator;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.SAMLDiscovery;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.SAMLLogoutProcessingFilter;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.saml.context.SAMLContextProvider;
import org.springframework.security.saml.context.SAMLContextProviderLB;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataDisplayFilter;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.processor.HTTPArtifactBinding;
import org.springframework.security.saml.processor.HTTPPAOS11Binding;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.HTTPSOAP11Binding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessor;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.storage.EmptyStorageFactory;
import org.springframework.security.saml.trust.MetadataCredentialResolver;
import org.springframework.security.saml.trust.PKIXInformationResolver;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.ArtifactResolutionProfile;
import org.springframework.security.saml.websso.ArtifactResolutionProfileImpl;
import org.springframework.security.saml.websso.SingleLogoutProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfile;
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
import org.springframework.security.saml.websso.WebSSOProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import ch.admin.seco.jobroom.repository.UserInfoRepository;

public final class SAMLConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private static final String TARGET_URL_AFTER_AUTHENTICATION = "/#/home";
    private static final String TARGET_URL_AFTER_LOGOUT = "/#/home";
    public static final String TARGET_URL_REGISTRATION_PROCESS = "/#/registrationQuestionnaire";
    public static final String TARGET_URL_ENTER_ACCESS_CODE = "/#/accessCode";
    public static final String TARGET_URL_FORCE_TWO_FACTOR_AUTH = "/samllogin?strong=true";

    /* Authentication methods supported by the eIAM */
    private static final String NOMAD_TELEPHONY_AUTH_CTX = AuthnContext.NOMAD_TELEPHONY_AUTHN_CTX;
    private static final String SMARTCARD_PKI_AUTHN_CTX = AuthnContext.SMARTCARD_PKI_AUTHN_CTX;
    private static final String SOFTWARE_PKI_AUTHN_CTX = AuthnContext.SOFTWARE_PKI_AUTHN_CTX;
    private static final String KERBEROS_AUTHN_CTX = AuthnContext.KERBEROS_AUTHN_CTX;
    private static final String PASSWORD_PROTECTED_TRANSPORT_AUTHN_CTX = AuthnContext.PPT_AUTHN_CTX;
    public static final Collection<String> PASSWORD_ALLOWED_AUTHN_CTX = Arrays.asList(
        NOMAD_TELEPHONY_AUTH_CTX, SMARTCARD_PKI_AUTHN_CTX, SOFTWARE_PKI_AUTHN_CTX, KERBEROS_AUTHN_CTX, PASSWORD_PROTECTED_TRANSPORT_AUTHN_CTX
    );
    public static final Collection<String> DEFAULT_AUTHN_CTX = Arrays.asList(
        NOMAD_TELEPHONY_AUTH_CTX, SMARTCARD_PKI_AUTHN_CTX, SOFTWARE_PKI_AUTHN_CTX, KERBEROS_AUTHN_CTX
    );
    public static final String ONE_FACTOR_AUTHN_CTX = PASSWORD_PROTECTED_TRANSPORT_AUTHN_CTX;

    private final String targetUrlEiamAccessRequest;

    private final UserInfoRepository userInfoRepository;

    private final AuthenticationEntryPoint fallbackAuthenticationEntryPoint;

    private final ApplicationEventPublisher applicationEventPublisher;

    private IdentityProvider identityProvider = new IdentityProvider();
    private ServiceProvider serviceProvider = new ServiceProvider();

    private WebSSOProfileOptions webSSOProfileOptions = webSSOProfileOptions();
    private StaticBasicParserPool parserPool = staticBasicParserPool();
    private SAMLProcessor samlProcessor = samlProcessor();
    private SAMLDefaultLogger samlLogger = new SAMLDefaultLogger();
    private SAMLAuthenticationProvider samlAuthenticationProvider;
    private MetadataProvider metadataProvider;
    private ExtendedMetadataDelegate extendedMetadataDelegate;
    private CachingMetadataManager cachingMetadataManager;
    private WebSSOProfile webSSOProfile;
    private SAMLUserDetailsService samlUserDetailsService;


    private ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<Object>() {
        public <T> T postProcess(T object) {
            return object;
        }
    };


    private SAMLConfigurer(String accessRequestUrl, UserInfoRepository userInfoRepository, AuthenticationEntryPoint fallbackAuthenticationEntryPoint, ApplicationEventPublisher applicationEventPublisher) {
        this.targetUrlEiamAccessRequest = accessRequestUrl;
        this.userInfoRepository = userInfoRepository;
        this.fallbackAuthenticationEntryPoint = fallbackAuthenticationEntryPoint;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void init(HttpSecurity http) throws Exception {

        metadataProvider = identityProvider.metadataProvider();
        ExtendedMetadata extendedMetadata = extendedMetadata();
        extendedMetadataDelegate = extendedMetadataDelegate(extendedMetadata);
        serviceProvider.keyManager = serviceProvider.keyManager();
        cachingMetadataManager = cachingMetadataManager();
        webSSOProfile = new WebSSOProfileImpl(samlProcessor, cachingMetadataManager);
        samlAuthenticationProvider = samlAuthenticationProvider();

        bootstrap();

        SAMLContextProvider contextProvider = contextProvider();
        SAMLEntryPoint samlEntryPoint = samlEntryPoint(contextProvider);

        AuthenticationEntryPoint entryPoint = prepareEntryPoint(samlEntryPoint);

        http
            .httpBasic().realmName("saml")
            .authenticationEntryPoint(entryPoint);

        /*        CsrfConfigurer<HttpSecurity> csrfConfigurer = http.getConfigurer(CsrfConfigurer.class);
        if (csrfConfigurer != null) {
            // Workaround to get working with Spring Security 3.2.
            RequestMatcher ignored = new AntPathRequestMatcher("/saml/SSO");
            RequestMatcher notIgnored = new NegatedRequestMatcher(ignored);
            RequestMatcher matcher = new AndRequestMatcher(new DefaultRequiresCsrfMatcher(), notIgnored);
            csrfConfigurer.requireCsrfProtectionMatcher(matcher);
        }
        */
        http
            .addFilterBefore(metadataGeneratorFilter(samlEntryPoint, extendedMetadata), ChannelProcessingFilter.class)
            .addFilterAfter(samlFilter(samlEntryPoint, contextProvider), BasicAuthenticationFilter.class)
            .authenticationProvider(samlAuthenticationProvider);
    }

    public static SAMLConfigurer saml(String accessRequestUrl,
        UserInfoRepository userInfoRepository,
        AuthenticationEntryPoint fallbackAuthenticationEntryPoint,
        ApplicationEventPublisher applicationEventPublisher) {
        return new SAMLConfigurer(accessRequestUrl, userInfoRepository, fallbackAuthenticationEntryPoint, applicationEventPublisher);
    }

    public SAMLConfigurer userDetailsService(SAMLUserDetailsService samlUserDetailsService) {
        this.samlUserDetailsService = samlUserDetailsService;
        return this;
    }

    public IdentityProvider identityProvider() {
        return identityProvider;
    }

    public ServiceProvider serviceProvider() {
        return serviceProvider;
    }

    private String entityBaseURL() {
        String entityBaseURL = serviceProvider.hostName + "/" + serviceProvider.basePath;
        entityBaseURL = entityBaseURL.replaceAll("//", "/").replaceAll("/$", "");
        entityBaseURL = serviceProvider.protocol + "://" + entityBaseURL;
        return entityBaseURL;
    }

    private SAMLEntryPoint samlEntryPoint(SAMLContextProvider contextProvider) {
        SAMLEntryPoint samlEntryPoint = new SAMLDslEntryPoint();
        samlEntryPoint.setDefaultProfileOptions(webSSOProfileOptions);
        samlEntryPoint.setWebSSOprofile(webSSOProfile);
        samlEntryPoint.setContextProvider(contextProvider);
        samlEntryPoint.setMetadata(cachingMetadataManager);
        samlEntryPoint.setSamlLogger(samlLogger);
        return samlEntryPoint;
    }

    private SAMLProcessor samlProcessor() {
        Collection<SAMLBinding> bindings = new ArrayList<>();
        bindings.add(httpRedirectDeflateBinding(parserPool));
        bindings.add(httpPostBinding(parserPool));
        bindings.add(artifactBinding(parserPool));
        bindings.add(httpSOAP11Binding(parserPool));
        bindings.add(httpPAOS11Binding(parserPool));
        return new SAMLProcessorImpl(bindings);
    }

    private CachingMetadataManager cachingMetadataManager() {
        try {
            List<MetadataProvider> providers = new ArrayList<>();
            providers.add(extendedMetadataDelegate);
            CachingMetadataManager cachingMetadataManager = new CachingMetadataManager(providers);
            cachingMetadataManager.setKeyManager(this.serviceProvider.keyManager);
            return cachingMetadataManager;
        } catch (MetadataProviderException e) {
            throw new IllegalStateException("could not initialize CachingMetadataManager", e);
        }
    }

    private StaticBasicParserPool staticBasicParserPool() {
        StaticBasicParserPool parserPool = new StaticBasicParserPool();
        try {
            parserPool.initialize();
        } catch (XMLParserException e) {
            throw new IllegalStateException("could not initalize StaticBasicParserPool", e);
        }
        return parserPool;
    }

    private ExtendedMetadataDelegate extendedMetadataDelegate(ExtendedMetadata extendedMetadata) {
        ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(metadataProvider, extendedMetadata);
        extendedMetadataDelegate.setMetadataTrustCheck(false);
        extendedMetadataDelegate.setMetadataRequireSignature(false);
        return extendedMetadataDelegate;
    }

    private ExtendedMetadata extendedMetadata() {
        ExtendedMetadata extendedMetadata = new ExtendedMetadata();
        extendedMetadata.setIdpDiscoveryEnabled(identityProvider.discoveryEnabled);
        extendedMetadata.setSignMetadata(identityProvider.signMetadata);
        extendedMetadata.setAlias(serviceProvider.entityAlias);
        return extendedMetadata;
    }

    private WebSSOProfileOptions webSSOProfileOptions() {
        WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
        webSSOProfileOptions.setIncludeScoping(false);
        webSSOProfileOptions.setAuthnContexts(DEFAULT_AUTHN_CTX);
        return webSSOProfileOptions;
    }

    private void bootstrap() {
        final SAMLBootstrap samlBootstrap = new SAMLBootstrap();
        samlBootstrap.postProcessBeanFactory(null);
    }

    private HTTPPostBinding httpPostBinding(ParserPool parserPool) {
        return new HTTPPostBinding(parserPool, VelocityFactory.getEngine());
    }

    private HTTPRedirectDeflateBinding httpRedirectDeflateBinding(ParserPool parserPool) {
        return new HTTPRedirectDeflateBinding(parserPool);
    }

    private SAMLProcessingFilter samlWebSSOProcessingFilter(SAMLAuthenticationProvider samlAuthenticationProvider, SAMLContextProvider contextProvider, SAMLProcessor samlProcessor) throws Exception {
        SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
        AuthenticationManagerBuilder authenticationManagerBuilder = new AuthenticationManagerBuilder(this.objectPostProcessor);
        authenticationManagerBuilder.authenticationProvider(samlAuthenticationProvider);
        samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManagerBuilder.build());
        samlWebSSOProcessingFilter.setContextProvider(contextProvider);
        samlWebSSOProcessingFilter.setSAMLProcessor(samlProcessor);
        samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        samlWebSSOProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        samlWebSSOProcessingFilter.setApplicationEventPublisher(this.applicationEventPublisher);
        return samlWebSSOProcessingFilter;
    }

    private JobroomAuthenticationFailureHandler authenticationFailureHandler() {
        JobroomAuthenticationFailureHandler authenticationFailureHandler = new JobroomAuthenticationFailureHandler();
        authenticationFailureHandler.setUseForward(false);
        return authenticationFailureHandler;
    }

    private AuthenticationEventPublisher authenticationEventPublisher() {
        DefaultAuthenticationEventPublisher authenticationEventPublisher = new DefaultAuthenticationEventPublisher();
        authenticationEventPublisher.setApplicationEventPublisher(this.applicationEventPublisher);
        return authenticationEventPublisher;
    }

    private JobroomAuthenticationSuccessHandler authenticationSuccessHandler() {
        JobroomAuthenticationSuccessHandler authenticationSuccessHandler = new JobroomAuthenticationSuccessHandler(this.targetUrlEiamAccessRequest, this.userInfoRepository, authenticationEventPublisher());
        authenticationSuccessHandler.setAlwaysUseDefaultTargetUrl(true);
        authenticationSuccessHandler.setDefaultTargetUrl(TARGET_URL_AFTER_AUTHENTICATION);
        return authenticationSuccessHandler;
    }

    private MetadataGeneratorFilter metadataGeneratorFilter(SAMLEntryPoint samlEntryPoint, ExtendedMetadata extendedMetadata) {
        MetadataGeneratorFilter metadataGeneratorFilter = new MetadataGeneratorFilter(getMetadataGenerator(samlEntryPoint, extendedMetadata));
        metadataGeneratorFilter.setManager(cachingMetadataManager);
        return metadataGeneratorFilter;
    }

    private FilterChainProxy samlFilter(SAMLEntryPoint samlEntryPoint, SAMLContextProvider contextProvider) throws Exception {
        List<SecurityFilterChain> chains = new ArrayList<>();
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"), samlEntryPoint));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/metadata/**"), metadataDisplayFilter(contextProvider)));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"), samlWebSSOProcessingFilter(samlAuthenticationProvider, contextProvider, samlProcessor)));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"), samlLogoutFilter(contextProvider, samlProcessor)));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SingleLogout/**"), samlLogoutProcessingFilter(contextProvider, samlProcessor)));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/discovery/**"), samlDiscovery(contextProvider)));
        return new FilterChainProxy(chains);
    }

    private SAMLDiscovery samlDiscovery(SAMLContextProvider contextProvider) {
        SAMLDiscovery samlDiscovery = new SAMLDiscovery();
        samlDiscovery.setMetadata(cachingMetadataManager);
        samlDiscovery.setContextProvider(contextProvider);
        return samlDiscovery;
    }

    private SAMLLogoutProcessingFilter samlLogoutProcessingFilter(SAMLContextProvider contextProvider, SAMLProcessor samlProcessor) {
        SAMLLogoutProcessingFilter samlLogoutProcessingFilter = new SAMLLogoutProcessingFilter(successLogoutHandler(), logoutHandler());
        samlLogoutProcessingFilter.setContextProvider(contextProvider);
        samlLogoutProcessingFilter.setSamlLogger(this.samlLogger);
        samlLogoutProcessingFilter.setLogoutProfile(singleLogoutProfile(samlProcessor));
        samlLogoutProcessingFilter.setSAMLProcessor(samlProcessor);
        return samlLogoutProcessingFilter;
    }

    private SAMLLogoutFilter samlLogoutFilter(SAMLContextProvider contextProvider, SAMLProcessor samlProcessor) {
        SAMLLogoutFilter samlLogoutFilter = new SAMLLogoutFilter(
            successLogoutHandler(),
            new LogoutHandler[] {logoutHandler()},
            new LogoutHandler[] {logoutHandler()}
        );
        samlLogoutFilter.setProfile(singleLogoutProfile(samlProcessor));
        samlLogoutFilter.setContextProvider(contextProvider);
        samlLogoutFilter.setSamlLogger(this.samlLogger);
        return samlLogoutFilter;
    }

    private SingleLogoutProfileImpl singleLogoutProfile(SAMLProcessor samlProcessor) {
        SingleLogoutProfileImpl singleLogoutProfile = new SingleLogoutProfileImpl();
        singleLogoutProfile.setProcessor(samlProcessor);
        return singleLogoutProfile;
    }

    private SecurityContextLogoutHandler logoutHandler() {
        return new SecurityContextLogoutHandler();
    }

    private SimpleUrlLogoutSuccessHandler successLogoutHandler() {
        final SimpleUrlLogoutSuccessHandler successLogoutHandler = new SimpleUrlLogoutSuccessHandler();
        successLogoutHandler.setDefaultTargetUrl(TARGET_URL_AFTER_LOGOUT);
        return successLogoutHandler;
    }

    private MetadataDisplayFilter metadataDisplayFilter(SAMLContextProvider contextProvider) {
        final MetadataDisplayFilter metadataDisplayFilter = new MetadataDisplayFilter();
        metadataDisplayFilter.setContextProvider(contextProvider);
        metadataDisplayFilter.setManager(cachingMetadataManager);
        metadataDisplayFilter.setKeyManager(this.serviceProvider.keyManager);
        return metadataDisplayFilter;
    }

    private SAMLAuthenticationProvider samlAuthenticationProvider() {
        SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider();
        samlAuthenticationProvider.setForcePrincipalAsString(false);
        samlAuthenticationProvider.setExcludeCredential(serviceProvider.excludeCredential);
        samlAuthenticationProvider.setSamlLogger(samlLogger);
        samlAuthenticationProvider.setConsumer(new WebSSOProfileConsumerImpl());
        samlAuthenticationProvider.setUserDetails(this.samlUserDetailsService);
        return samlAuthenticationProvider;
    }

    private SAMLContextProvider contextProvider() {
        SAMLContextProviderLB contextProvider = new SAMLContextProviderLB();
        contextProvider.setMetadata(cachingMetadataManager);
        contextProvider.setScheme(serviceProvider.protocol);
        contextProvider.setServerName(serviceProvider.hostName);
        contextProvider.setContextPath(serviceProvider.basePath);
        contextProvider.setKeyManager(serviceProvider.keyManager);
        // TODO: workaroung that InResponseToField check fails, because of different HttpSessions -> remove following line and fix problem!
        contextProvider.setStorageFactory(new EmptyStorageFactory());

        MetadataCredentialResolver resolver = new MetadataCredentialResolver(cachingMetadataManager, serviceProvider.keyManager);
        PKIXTrustEvaluator pkixTrustEvaluator = new CertPathPKIXTrustEvaluator();
        PKIXInformationResolver pkixInformationResolver = new PKIXInformationResolver(resolver, cachingMetadataManager, serviceProvider.keyManager);

        contextProvider.setPkixResolver(pkixInformationResolver);
        contextProvider.setPkixTrustEvaluator(pkixTrustEvaluator);
        contextProvider.setMetadataResolver(resolver);

        if (serviceProvider.withEmptyStorage) {
            contextProvider.setStorageFactory(new EmptyStorageFactory());
        }
        return contextProvider;
    }

    private MetadataGenerator getMetadataGenerator(SAMLEntryPoint samlEntryPoint, ExtendedMetadata extendedMetadata) {
        MetadataGenerator metadataGenerator = new MetadataGenerator();

        metadataGenerator.setSamlEntryPoint(samlEntryPoint);
        metadataGenerator.setEntityBaseURL(entityBaseURL());
        metadataGenerator.setKeyManager(serviceProvider.keyManager);
        metadataGenerator.setEntityId(serviceProvider.entityId);
        metadataGenerator.setIncludeDiscoveryExtension(false);
        metadataGenerator.setExtendedMetadata(extendedMetadata);

        return metadataGenerator;
    }

    private AuthenticationEntryPoint prepareEntryPoint(SAMLEntryPoint samlEntryPoint) {
        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<RequestMatcher, AuthenticationEntryPoint>();
        entryPoints.put(new XmlHttpRequestedWithMatcher(), this.fallbackAuthenticationEntryPoint);
        DelegatingAuthenticationEntryPoint defaultEntryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
        defaultEntryPoint.setDefaultEntryPoint(samlEntryPoint);
        return defaultEntryPoint;
    }

    private SAMLBinding artifactBinding(ParserPool parserPool) {
        return new HTTPArtifactBinding(parserPool, VelocityFactory.getEngine(), artifactResolutionProfile(parserPool));
    }

    private SAMLBinding httpPAOS11Binding(ParserPool parserPool) {
        return new HTTPPAOS11Binding(parserPool);
    }

    private SAMLBinding httpSOAP11Binding(ParserPool parserPool) {
        return new HTTPSOAP11Binding(parserPool);
    }

    private ArtifactResolutionProfile artifactResolutionProfile(ParserPool parserPool) {
        final ArtifactResolutionProfileImpl artifactResolutionProfile = new ArtifactResolutionProfileImpl(httpClient());
        artifactResolutionProfile.setProcessor(new SAMLProcessorImpl(Collections.singleton(new HTTPSOAP11Binding(parserPool))));
        return artifactResolutionProfile;
    }

    private HttpClient httpClient() {
        return new HttpClient(new MultiThreadedHttpConnectionManager());
    }

    public class IdentityProvider {

        private String metadataFilePath;

        private boolean discoveryEnabled = true;

        private boolean signMetadata = false;

        public IdentityProvider metadataFilePath(String metadataFilePath) {
            this.metadataFilePath = metadataFilePath;
            return this;
        }

        public IdentityProvider discoveryEnabled(boolean discoveryEnabled) {
            this.discoveryEnabled = discoveryEnabled;
            return this;
        }

        public IdentityProvider signMetadata(boolean signMetadata) {
            this.signMetadata = signMetadata;
            return this;
        }

        private MetadataProvider metadataProvider() {
            if (metadataFilePath.startsWith("http")) {
                return httpMetadataProvider();
            } else {
                return fileSystemMetadataProvider();
            }
        }

        private HTTPMetadataProvider httpMetadataProvider() {
            try {
                HTTPMetadataProvider httpMetadataProvider = new HTTPMetadataProvider(new Timer(), new HttpClient(), metadataFilePath);
                httpMetadataProvider.setParserPool(parserPool);
                return httpMetadataProvider;
            } catch (MetadataProviderException e) {
                throw new IllegalStateException("could not initialize HTTPMetadataProvider", e);
            }
        }

        private FilesystemMetadataProvider fileSystemMetadataProvider() {
            DefaultResourceLoader loader = new DefaultResourceLoader();
            Resource metadataResource = loader.getResource(metadataFilePath);
            try {
                FilesystemMetadataProvider filesystemMetadataProvider = new FilesystemMetadataProvider(metadataResource.getFile());
                filesystemMetadataProvider.setParserPool(parserPool);
                return filesystemMetadataProvider;
            } catch (MetadataProviderException | IOException e) {
                throw new IllegalStateException("could not initialize FilesystemMetadataProvider", e);
            }
        }

        public SAMLConfigurer and() {
            return SAMLConfigurer.this;
        }
    }

    public class ServiceProvider {

        private KeyStore keyStore = new KeyStore();
        private KeyManager keyManager;
        private String protocol;
        private String hostName;
        private String basePath;
        private String entityId;
        private String entityAlias;
        private boolean withEmptyStorage;
        private boolean excludeCredential;

        public ServiceProvider protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public ServiceProvider hostname(String hostname) {
            this.hostName = hostname;
            return this;
        }

        public ServiceProvider basePath(String basePath) {
            this.basePath = basePath;
            return this;
        }

        public ServiceProvider entityId(String entityId) {
            this.entityId = entityId;
            return this;
        }

        public ServiceProvider withEmptyStorage(boolean withEmptyStorage) {
            this.withEmptyStorage = withEmptyStorage;
            return this;
        }

        public ServiceProvider excludeCredential(boolean excludeCredential) {
            this.excludeCredential = excludeCredential;
            return this;
        }

        public ServiceProvider entityAlias(String entityAlias) {
            this.entityAlias = entityAlias;
            return this;
        }

        public KeyStore keyStore() {
            return keyStore;
        }

        public SAMLConfigurer and() {
            return SAMLConfigurer.this;
        }

        private KeyManager keyManager() {
            DefaultResourceLoader loader = new DefaultResourceLoader();
            Resource storeFile = loader.getResource(keyStore.getStoreFilePath());
            Map<String, String> passwords = new HashMap<>();
            passwords.put(keyStore.getKeyname(), keyStore.getKeyPassword());
            return new JKSKeyManager(storeFile, keyStore.getPassword(), passwords, keyStore.getKeyname());
        }

        public class KeyStore {
            private String storeFilePath;
            private String password;
            private String keyname;
            private String keyPassword;

            public KeyStore storeFilePath(String storeFilePath) {
                this.storeFilePath = storeFilePath;
                return this;
            }

            public KeyStore password(String password) {
                this.password = password;
                return this;
            }

            public KeyStore keyname(String keyname) {
                this.keyname = keyname;
                return this;
            }

            public KeyStore keyPassword(String keyPasswordword) {
                this.keyPassword = keyPasswordword;
                return this;
            }

            public ServiceProvider and() {
                return ServiceProvider.this;
            }

            public String getStoreFilePath() {
                return storeFilePath;
            }

            public String getPassword() {
                return password;
            }

            public String getKeyname() {
                return keyname;
            }

            public String getKeyPassword() {
                return keyPassword;
            }

            @Override
            public String toString() {
                return "KeyStore{" +
                    "storeFilePath='" + storeFilePath + '\'' +
                    ", password='" + password + '\'' +
                    ", keyname='" + keyname + '\'' +
                    ", keyPassword='" + keyPassword + '\'' +
                    '}';
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }

                KeyStore keyStore = (KeyStore) o;

                if (storeFilePath != null ? !storeFilePath.equals(keyStore.storeFilePath) : keyStore.storeFilePath != null) {
                    return false;
                }
                if (password != null ? !password.equals(keyStore.password) : keyStore.password != null) {
                    return false;
                }
                if (keyname != null ? !keyname.equals(keyStore.keyname) : keyStore.keyname != null) {
                    return false;
                }
                return keyPassword != null ? keyPassword.equals(keyStore.keyPassword) : keyStore.keyPassword == null;
            }

            @Override
            public int hashCode() {
                int result = storeFilePath != null ? storeFilePath.hashCode() : 0;
                result = 31 * result + (password != null ? password.hashCode() : 0);
                result = 31 * result + (keyname != null ? keyname.hashCode() : 0);
                result = 31 * result + (keyPassword != null ? keyPassword.hashCode() : 0);
                return result;
            }
        }

    }

    private static final class DefaultRequiresCsrfMatcher implements RequestMatcher {
        private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");

        public boolean matches(HttpServletRequest request) {
            return !allowedMethods.matcher(request.getMethod()).matches();
        }
    }
}
