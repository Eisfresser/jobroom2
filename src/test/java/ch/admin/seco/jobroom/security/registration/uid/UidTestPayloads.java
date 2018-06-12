package ch.admin.seco.jobroom.security.registration.uid;

final class UidTestPayloads {

	private UidTestPayloads() {
		// Avoid instantiation
	}

	private static final String GET_BY_UID_VALID_COMPANY =
        "<organisationType>" +
        "<organisation xmlns=\"http://www.ech.ch/xmlns/eCH-0108-f/3\">" +
        "<organisationIdentification xmlns=\"http://www.ech.ch/xmlns/eCH-0098-f/3\">" +
        "<uid xmlns=\"http://www.ech.ch/xmlns/eCH-0097-f/2\">" +
        "<uidOrganisationIdCategorie>CHE</uidOrganisationIdCategorie>" +
        "<uidOrganisationId>115635627</uidOrganisationId>" +
        "</uid>" +
        "<OtherOrganisationId xmlns=\"http://www.ech.ch/xmlns/eCH-0097-f/2\">" +
        "<organisationIdCategory>CH.HR</organisationIdCategory>" +
        "<organisationId>CH03630474042</organisationId>" +
        "</OtherOrganisationId>" +
        "<OtherOrganisationId xmlns=\"http://www.ech.ch/xmlns/eCH-0097-f/2\">" +
        "<organisationIdCategory>CH.MWST</organisationIdCategory>" +
        "<organisationId>750417</organisationId>" +
        "</OtherOrganisationId>" +
        "<organisationName xmlns=\"http://www.ech.ch/xmlns/eCH-0097-f/2\">mimacom ag</organisationName>" +
        "<organisationLegalName xmlns=\"http://www.ech.ch/xmlns/eCH-0097-f/2\">mimacom ag</organisationLegalName>" +
        "<legalForm xmlns=\"http://www.ech.ch/xmlns/eCH-0097-f/2\">0106</legalForm>" +
        "</organisationIdentification>" +
        "<contact xmlns=\"http://www.ech.ch/xmlns/eCH-0098-f/3\">" +
        "<address xmlns=\"http://www.ech.ch/xmlns/eCH-0046-f/3\">" +
        "<otherAddressCategory>main</otherAddressCategory>" +
        "<postalAddress>" +
        "<organisation xmlns=\"http://www.ech.ch/xmlns/eCH-0010-f/6\">" +
        "<organisationName>mimacom ag</organisationName>" +
        "</organisation>" +
        "<addressInformation xmlns=\"http://www.ech.ch/xmlns/eCH-0010-f/6\">" +
        "<street>Galgenfeldweg</street>" +
        "<houseNumber>16</houseNumber>" +
        "<town>Bern</town>" +
        "<swissZipCode>3006</swissZipCode>" +
        "<swissZipCodeAddOn>00</swissZipCodeAddOn>" +
        "<country>" +
        "<countryIdISO2>CH</countryIdISO2>" +
        "<countryNameShort>CH</countryNameShort>" +
        "</country>" +
        "</addressInformation>" +
        "</postalAddress>" +
        "</address>" +
        "</contact>" +
        "</organisation>" +
        "<uidregInformation xmlns=\"http://www.ech.ch/xmlns/eCH-0108-f/3\">" +
        "<uidregStatusEnterpriseDetail>3</uidregStatusEnterpriseDetail>" +
        "<uidregPublicStatus>1</uidregPublicStatus>" +
        "</uidregInformation>" +
        "<commercialRegisterInformation xmlns=\"http://www.ech.ch/xmlns/eCH-0108-f/3\">" +
        "<commercialRegisterStatus>2</commercialRegisterStatus>" +
        "<commercialRegisterEntryStatus>1</commercialRegisterEntryStatus>" +
        "</commercialRegisterInformation>" +
        "<vatRegisterInformation xmlns=\"http://www.ech.ch/xmlns/eCH-0108-f/3\">" +
        "<vatStatus>2</vatStatus>" +
        "<vatEntryStatus>1</vatEntryStatus>" +
        "<vatEntryDate>2010-01-01</vatEntryDate>" +
        "<uidVat>" +
        "<uidOrganisationIdCategorie xmlns=\"http://www.ech.ch/xmlns/eCH-0097-f/2\">CHE</uidOrganisationIdCategorie>" +
        "<uidOrganisationId xmlns=\"http://www.ech.ch/xmlns/eCH-0097-f/2\">115635627</uidOrganisationId>" +
        "</uidVat>" +
        "</vatRegisterInformation>" +
        "<organisationMunicipality xmlns=\"http://www.ech.ch/xmlns/eCH-0108-f/3\">" +
        "<municipalityId xmlns=\"http://www.ech.ch/xmlns/eCH-0007-f/6\">351</municipalityId>" +
        "<municipalityName xmlns=\"http://www.ech.ch/xmlns/eCH-0007-f/6\">Bern</municipalityName>" +
        "</organisationMunicipality>" +
        "<cantonAbbreviationMainAddress xmlns=\"http://www.ech.ch/xmlns/eCH-0108-f/3\">BE</cantonAbbreviationMainAddress>" +
        "</organisationType>";

    public static final String GET_BY_UID_VALID_PAYLOAD_RESPONSE =
        "<GetByUIDResponse xmlns=\"http://www.uid.admin.ch/xmlns/uid-wse\">" +
            "<GetByUIDResult>" +
                GET_BY_UID_VALID_COMPANY +
            "</GetByUIDResult>" +
        "</GetByUIDResponse>";

    public static final String GET_BY_UID_INVALID_PAYLOAD_RESPONSE =
        "<BusinessFault xmlns=\"http://schemas.datacontract.org/2004/07/CH.Admin.BIT.UID.PublicWebService\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">"+
        "<Error>Data_validation_failed</Error>"+
        "<ErrorDetail>CHE115.635.627 is not a valid UID</ErrorDetail>"+
        "<Operation>Data validation</Operation>"+
        "</BusinessFault>";

	public static final String GET_BY_UID_EMPTY_RESPONSE =
        "<GetByUIDResponse xmlns=\"http://www.uid.admin.ch/xmlns/uid-wse\">" +
            "<GetByUIDResult/>" +
            "</GetByUIDResponse>";

    public static final String GET_BY_UID_MULTIPLE_COMPANIES_PAYLOAD_RESPONSE =
        "<GetByUIDResponse xmlns=\"http://www.uid.admin.ch/xmlns/uid-wse\">" +
            "<GetByUIDResult>" +
                GET_BY_UID_VALID_COMPANY +
                GET_BY_UID_VALID_COMPANY +
            "</GetByUIDResult>" +
        "</GetByUIDResponse>";

}
