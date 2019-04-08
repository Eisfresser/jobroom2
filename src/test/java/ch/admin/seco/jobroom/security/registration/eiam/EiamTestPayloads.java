package ch.admin.seco.jobroom.security.registration.eiam;

final class EiamTestPayloads {

	private EiamTestPayloads() {
		// Avoid instantiation
	}

	private static final String GET_USER_BY_EXT_ID_VALID_USER =
        "<return>" +
        "<loginId>CHL9100100004</loginId>" +
        "<extId>CH2102565</extId>" +
        "<clientExtId>9100</clientExtId>" +
        "<clientName>ALV</clientName>" +
        "<state>ACTIVE</state>" +
        "<firstName>Hans</firstName>" +
        "<name>Muster</name>" +
        "<remarks>Organisation: n/a; Remarks: -</remarks>" +
        "<email>hans.muster@mail.ch</email>" +
        "<language>DE</language>" +
        "<templateCollection>default_for_-ALV</templateCollection>" +
        "<profiles defaultProfile=\"true\">" +
        "<name>Profile-CHL9100100004</name>" +
        "<extId>176988</extId>" +
        "<userExtId>CH2102565</userExtId>" +
        "<state>ACTIVE</state>" +
        "<unit>" +
        "<name>AccessRequest</name>" +
        "<extId>9100.access-request</extId>" +
        "<state>ACTIVE</state>" +
        "<displayName>" +
        "<entries>" +
        "<lang>DE</lang>" +
        "<value>AccessRequest</value>" +
        "</entries>" +
        "<entries>" +
        "<lang>FR</lang>" +
        "<value>AccessRequest</value>" +
        "</entries>" +
        "<entries>" +
        "<lang>IT</lang>" +
        "<value>AccessRequest</value>" +
        "</entries>" +
        "<entries>" +
        "<lang>EN</lang>" +
        "<value>AccessRequest</value>" +
        "</entries>" +
        "</displayName>" +
        "<displayAbbreviation>" +
        "<entries>" +
        "<lang>DE</lang>" +
        "<value>AR</value>" +
        "</entries>" +
        "<entries>" +
        "<lang>FR</lang>" +
        "<value>AR</value>" +
        "</entries>" +
        "<entries>" +
        "<lang>IT</lang>" +
        "<value>AR</value>" +
        "</entries>" +
        "<entries>" +
        "<lang>EN</lang>" +
        "<value>AR</value>" +
        "</entries>" +
        "</displayAbbreviation>" +
        "<description>Hier werden die Profile beim erstmaligen AccessRequest in diesem Mandanten angelegt.</description>" +
        "<hname>/9100.access-request</hname>" +
        "<localizedHname>" +
        "<entries>" +
        "<lang>DE</lang>" +
        "<value>AccessRequest</value>" +
        "</entries>" +
        "<entries>" +
        "<lang>FR</lang>" +
        "<value>AccessRequest</value>" +
        "</entries>" +
        "<entries>" +
        "<lang>IT</lang>" +
        "<value>AccessRequest</value>" +
        "</entries>" +
        "<entries>" +
        "<lang>EN</lang>" +
        "<value>AccessRequest</value>" +
        "</entries>" +
        "</localizedHname>" +
        "<profileless>false</profileless>" +
        "</unit>" +
        "<roles>" +
        "<name>ROLE_REGISTRATION</name>" +
        "<extId>9100.ALV-jobroom.ROLE_REGISTRATION</extId>" +
        "<applicationName>ALV-jobroom</applicationName>" +
        "<applicationExtId>9100.APPL_ALV-jobroom</applicationExtId>" +
        "</roles>" +
        "<roles>" +
        "<name>ALLOW</name>" +
        "<extId>9100.ALV-jobroom.ALLOW</extId>" +
        "<applicationName>ALV-jobroom</applicationName>" +
        "<applicationExtId>9100.APPL_ALV-jobroom</applicationExtId>" +
        "</roles>" +
        "<authorizations>" +
        "<role>" +
        "<name>ALLOW</name>" +
        "<extId>9100.ALV-jobroom.ALLOW</extId>" +
        "<applicationName>ALV-jobroom</applicationName>" +
        "<applicationExtId>9100.APPL_ALV-jobroom</applicationExtId>" +
        "</role>" +
        "<directAssignment>true</directAssignment>" +
        "</authorizations>" +
        "<authorizations>" +
        "<role>" +
        "<name>ROLE_REGISTRATION</name>" +
        "<extId>9100.ALV-jobroom.ROLE_REGISTRATION</extId>" +
        "<applicationName>ALV-jobroom</applicationName>" +
        "<applicationExtId>9100.APPL_ALV-jobroom</applicationExtId>" +
        "</role>" +
        "<directAssignment>true</directAssignment>" +
        "</authorizations>" +
        "</profiles>" +
        "<credentials>" +
        "<userExtId>CH2102565</userExtId>" +
        "<state>ACTIVE</state>" +
        "<type>GENERIC</type>" +
        "<lastChange>2018-05-09T13:17:53.000+02:00</lastChange>" +
        "<validFrom>2018-05-09T13:17:53.000+02:00</validFrom>" +
        "<validTo>2028-05-06T13:17:53.000+02:00</validTo>" +
        "<failureCount>0</failureCount>" +
        "<successCount>0</successCount>" +
        "<resetCount>0</resetCount>" +
        "<value>eid\\5300\\177221</value>" +
        "<extId>440318</extId>" +
        "<policyName>Generic Credential Policy E-ID</policyName>" +
        "<policyExtId>9100.GenCrdPol-E-ID</policyExtId>" +
        "</credentials>" +
        "<credentials>" +
        "<userExtId>CH2102565</userExtId>" +
        "<state>ACTIVE</state>" +
        "<type>SAML_FEDERATION</type>" +
        "<lastChange>2018-05-09T13:17:53.000+02:00</lastChange>" +
        "<validFrom>2018-05-09T13:17:53.000+02:00</validFrom>" +
        "<validTo>2118-05-09T13:17:53.000+02:00</validTo>" +
        "<failureCount>0</failureCount>" +
        "<successCount>0</successCount>" +
        "<resetCount>0</resetCount>" +
        "<value>{SSHA256}QaOwJ9BSsdkMdILfJ9pnHvKVd0cOdYITJB3ugzv/ThRGq7wroTT9Vgz4</value>" +
        "<extId>440319</extId>" +
        "<policyName>SamlFederation Generic-IDP</policyName>" +
        "<policyExtId>9100.SFPOL-GenericIDP</policyExtId>" +
        "</credentials>" +
        "<credentials>" +
        "<userExtId>CH2102565</userExtId>" +
        "<state>ACTIVE</state>" +
        "<type>SAML_FEDERATION</type>" +
        "<lastChange>2018-05-09T13:17:53.000+02:00</lastChange>" +
        "<validFrom>2018-05-09T13:17:53.000+02:00</validFrom>" +
        "<validTo>2118-05-09T13:17:53.000+02:00</validTo>" +
        "<failureCount>0</failureCount>" +
        "<successCount>0</successCount>" +
        "<resetCount>0</resetCount>" +
        "<value>{SSHA256}d5bH6N4+sh8cexoNqv/NSdkRV7jqAcHP+65K03R2IHjpV7xYabQSHH07</value>" +
        "<extId>440320</extId>" +
        "<policyName>SamlFederation Generic-IDP</policyName>" +
        "<policyExtId>9100.SFPOL-GenericIDP</policyExtId>" +
        "</credentials>" +
        "<samlFederations>" +
        "<userExtId>CH2102565</userExtId>" +
        "<credentialExtId>440319</credentialExtId>" +
        "<state>ACTIVE</state>" +
        "<subjectNameId>eid\\5300\\177221</subjectNameId>" +
        "<subjectNameIdFormat>urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified</subjectNameIdFormat>" +
        "<issuerNameId>E-ID CH-LOGIN</issuerNameId>" +
        "<issuerNameIdFormat>urn:oasis:names:tc:SAML:2.0:nameid-format:entity</issuerNameIdFormat>" +
        "<properties>" +
        "<name>ClaimsProvidedForRegistration</name>" +
        "<value>Jobroom,Test5,jobroom-test5@yopmail.com,de,177221,CH2102565,5300,CH-LOGIN,5300.selfreg,SelfReg,uid=177221,ou=5300.selfreg,ou=5300.extern,o=5300,o=EIAM,o=ADMIN,c=CH</value>" +
        "</properties>" +
        "</samlFederations>" +
        "<samlFederations>" +
        "<userExtId>CH2102565</userExtId>" +
        "<credentialExtId>440320</credentialExtId>" +
        "<state>ACTIVE</state>" +
        "<subjectNameId>CH2102565</subjectNameId>" +
        "<subjectNameIdFormat>urn:oasis:names:tc:SAML:2.0:nameid-format:persistent</subjectNameIdFormat>" +
        "<issuerNameId>eIAM</issuerNameId>" +
        "<issuerNameIdFormat>urn:oasis:names:tc:SAML:2.0:nameid-format:entity</issuerNameIdFormat>" +
        "</samlFederations>" +
        "<properties>" +
        "<name>source</name>" +
        "<value>Selfregistration</value>" +
        "</properties>" +
        "</return>";

    public static final String GET_USER_BY_EXT_ID_VALID_PAYLOAD_RESPONSE =
        "<ns2:getUsersByExtIdResponse xmlns:ns2=\"http://adnovum.ch/nevisidm/ws/services/v1\">" +
            GET_USER_BY_EXT_ID_VALID_USER +
        "</ns2:getUsersByExtIdResponse>";

	public static final String GET_USER_BY_EXT_ID_EMPTY_RESPONSE = "<ns2:getUsersByExtIdResponse xmlns:ns2=\"http://adnovum.ch/nevisidm/ws/services/v1\"/>";

    public static final String GET_USER_BY_EXT_ID_MULTIPLE_USERS_PAYLOAD_RESPONSE =
        "<ns2:getUsersByExtIdResponse xmlns:ns2=\"http://adnovum.ch/nevisidm/ws/services/v1\">" +
            GET_USER_BY_EXT_ID_VALID_USER +
            GET_USER_BY_EXT_ID_VALID_USER +
        "</ns2:getUsersByExtIdResponse>";

    public static final String ADD_ROLE_SUCCESS_RESPONSE =
        "<ns2:addAuthorizationToProfileResponse xmlns:ns2=\"http://adnovum.ch/nevisidm/ws/services/v1\"/>";

    public static final String ADD_ROLE_UNKNOWN_ROLE_RESPONSE =
        "<ns2:BusinessException xmlns:ns2=\"http://adnovum.ch/nevisidm/ws/services/v1\">" +
        "<message>ch.adnovum.nevisidm.service.util.NevisidmBusinessException: Role or application does not exist.</message>" +
        "<reason>errors.invalidParameter</reason>" +
        "</ns2:BusinessException>";
}
