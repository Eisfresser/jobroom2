package ch.admin.seco.jobroom.security.registration.uid;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import ch.ech.xmlns.ech_0010_f._6.AddressInformationType;
import ch.ech.xmlns.ech_0046_f._3.AddressType;
import ch.ech.xmlns.ech_0097_f._2.NamedOrganisationIdType;
import ch.ech.xmlns.ech_0097_f._2.OrganisationIdentificationType;
import ch.ech.xmlns.ech_0097_f._2.UidOrganisationIdCategorieType;
import ch.ech.xmlns.ech_0097_f._2.UidStructureType;
import ch.ech.xmlns.ech_0098_f._3.OrganisationType;
import ch.ech.xmlns.ech_0108_f._3.CommercialRegisterInformationType;
import org.datacontract.schemas._2004._07.ch_admin_bit_uid.BusinessFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;

import ch.admin.uid.xmlns.uid_wse.GetByUID;
import ch.admin.uid.xmlns.uid_wse.GetByUIDResponse;
import ch.admin.uid.xmlns.uid_wse.ObjectFactory;

class DefaultUidClient implements UidClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUidClient.class);

    private static final String CH_ID_CATEGORY = "CH.HR";
    private static final String MAIN_ADDRESS_CATEGORY = "main";
    private static final String ADDITIONAL_ADDRESS_CATEGORY = "additional";
    private static final String SWISS_ZIP_CODE = "swissZipCode";
    private static final String UID_STATUS_PRIVATE = "0";
    private static final String UID_STATUS_PUBLIC = "1";
    @SuppressWarnings("unused")
    private static final String ENTERPRISE_STATUS_PROVISIONAL = "1";
    private static final String ENTERPRISE_STATUS_REACTIVATION = "2";
    private static final String ENTERPRISE_STATUS_DEFINITIVE = "3";
    private static final String ENTERPRISE_STATUS_MUTATING = "4";
    private static final String ENTERPRISE_STATUS_DELETED = "5";
    @SuppressWarnings("unused")
    private static final String ENTERPRISE_STATUS_DEFINITIVELY_DELETED = "6";
    @SuppressWarnings("unused")
    private static final String ENTERPRISE_STATUS_ANNULLED = "7";
    private static final String COMMERCIAL_REGISTER_ACTIVE = "1";
    private static final String SEARCH_SOAP_ACTION = "http://www.uid.admin.ch/xmlns/uid-wse/IPublicServices/GetByUID";

    private static final WebServiceMessageCallback ACTION_ADDING_CALLBACK = message -> {
        SoapMessage soapMessage = (SoapMessage) message;
        soapMessage.setSoapAction(SEARCH_SOAP_ACTION);
    };

    private final WebServiceTemplate webServiceTemplate;

    DefaultUidClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public FirmData getCompanyByUid(long uid) throws UidCompanyNotFoundException {
        ObjectFactory factory = new ObjectFactory();
        GetByUID getByUID = factory.createGetByUID();
        ch.ech.xmlns.ech_0097_f._2.ObjectFactory factory1 = new ch.ech.xmlns.ech_0097_f._2.ObjectFactory();
        UidStructureType uidStructureType = factory1.createUidStructureType();
        uidStructureType.setUidOrganisationIdCategorie(UidOrganisationIdCategorieType.CHE);
        uidStructureType.setUidOrganisationId(BigInteger.valueOf(uid));
        getByUID.setUid(uidStructureType);

        LOGGER.debug("Client sending uid[uid={},category={}]", uidStructureType.getUidOrganisationId().toString(), uidStructureType.getUidOrganisationIdCategorie().value());

        Object response = webServiceTemplate.marshalSendAndReceive(getByUID, ACTION_ADDING_CALLBACK);

        // workaround for handling exceptions
        if (response instanceof JAXBElement && ((JAXBElement) response).getValue() instanceof BusinessFault) {
            BusinessFault faultDetails = (BusinessFault) ((JAXBElement) response).getValue();
            throw new UidClientRuntimeException("A problem occured while retrieving a company from the UID register: "
                + faultDetails.getErrorDetail());
        } else if (!(response instanceof GetByUIDResponse)) {
            throw new UidClientRuntimeException("An unknown response type was returned by the UID client call.");
        }

        GetByUIDResponse result = (GetByUIDResponse) response;
        List<ch.ech.xmlns.ech_0108_f._3.OrganisationType> orgs = result.getGetByUIDResult().getOrganisationType();

        if (orgs.isEmpty()) {
            LOGGER.debug("Client received no organisation for UID ='{}'", uid);
            throw new UidCompanyNotFoundException();
        }

        List<FirmData> firms = getVisibleFirmDTOs(orgs, false);

        if (firms.isEmpty()) {
            LOGGER.debug("Client received no visible organisation for UID ='{}'", uid);
            throw new UidCompanyNotFoundException();
        }

        if (firms.size() > 1) {
            LOGGER.error("Client received more than one organisation for UID = '{}':{}", uid, firmListToString(firms));
            throw new UidNotUniqueException(uid);
        }

        LOGGER.debug("Client received the following organisation for UID = '{}': {}", uid, firms.get(0).toString());
        return firms.get(0);
    }

    private List<FirmData> getVisibleFirmDTOs(List<ch.ech.xmlns.ech_0108_f._3.OrganisationType> organisations, boolean includingPrivate) {
        List<FirmData> firms = new ArrayList<>();
        for (ch.ech.xmlns.ech_0108_f._3.OrganisationType organisation : organisations) {
            if (isPublicAndStillActive(organisation) || (includingPrivate && isPrivateButAvailableForUidSearch(organisation))) {
                firms.add(getDto(organisation));
            }
        }
        return firms;
    }

    private boolean isPublicAndStillActive(ch.ech.xmlns.ech_0108_f._3.OrganisationType organisation) {
        return UID_STATUS_PUBLIC.equals(organisation.getUidregInformation().getUidregPublicStatus()) &&
            (ENTERPRISE_STATUS_REACTIVATION.equals(organisation.getUidregInformation().getUidregStatusEnterpriseDetail()) ||
                ENTERPRISE_STATUS_DEFINITIVE.equals(organisation.getUidregInformation().getUidregStatusEnterpriseDetail()) ||
                ENTERPRISE_STATUS_MUTATING.equals(organisation.getUidregInformation().getUidregStatusEnterpriseDetail()) ||
                ENTERPRISE_STATUS_DELETED.equals(organisation.getUidregInformation().getUidregStatusEnterpriseDetail()));
    }

    private boolean isPrivateButAvailableForUidSearch(final ch.ech.xmlns.ech_0108_f._3.OrganisationType organisation) {
        return UID_STATUS_PRIVATE.equals(organisation.getUidregInformation().getUidregPublicStatus()) &&
            (ENTERPRISE_STATUS_DEFINITIVE.equals(organisation.getUidregInformation().getUidregStatusEnterpriseDetail()) ||
                ENTERPRISE_STATUS_MUTATING.equals(organisation.getUidregInformation().getUidregStatusEnterpriseDetail()) ||
                ENTERPRISE_STATUS_DELETED.equals(organisation.getUidregInformation().getUidregStatusEnterpriseDetail()));
    }

    private FirmData getDto(ch.ech.xmlns.ech_0108_f._3.OrganisationType organisation) {
        FirmData firm = new FirmData();
        setFirmData(firm, organisation);
        setFirmActive(firm, organisation);
        setFirmPublicFlag(firm, organisation);
        setCommercialRegisterEntryDate(firm, organisation);
        return firm;
    }

    private void setFirmData(FirmData firm, ch.ech.xmlns.ech_0108_f._3.OrganisationType organisation) {
        OrganisationType org = organisation.getOrganisation();
        OrganisationIdentificationType ident = org.getOrganisationIdentification();
        firm.setChId(findOrganisationIdByCategory(ident.getOtherOrganisationId(), CH_ID_CATEGORY));
        firm.setName(ident.getOrganisationName());
        firm.setAdditionalName(ident.getOrganisationAdditionalName());
        firm.setUid(ident.getUid().getUidOrganisationId().intValue());
        firm.setUidPrefix(ident.getUid().getUidOrganisationIdCategorie().value());

        if (ident.getOtherOrganisationId() != null && ident.getOtherOrganisationId().size() > 0) {
            final NamedOrganisationIdType namedOrganisationIdType = ident.getOtherOrganisationId().get(0);

            if (namedOrganisationIdType != null && organisation.getVatRegisterInformation() != null) {

                final XMLGregorianCalendar vatLiquidationDate = organisation.getVatRegisterInformation().getVatLiquidationDate();
                if (vatLiquidationDate != null) {
                    firm.setVatLiquidationDate(vatLiquidationDate.toGregorianCalendar().getTime());
                }
                firm.setVatEntryStatus(organisation.getVatRegisterInformation().getVatEntryStatus());
                firm.setMwst(namedOrganisationIdType.getOrganisationId());
            }
        }

        AddressData address = new AddressData();
        fillInAddress(findAddressByCategory(org.getContact().getAddress(), MAIN_ADDRESS_CATEGORY), address, organisation);
        fillInAddress(findAddressByCategory(org.getContact().getAddress(), ADDITIONAL_ADDRESS_CATEGORY), address, organisation);
        firm.setAddress(address);
    }


    private void fillInAddress(AddressInformationType addr, AddressData address, ch.ech.xmlns.ech_0108_f._3.OrganisationType organisation) {
        if (addr != null) {
            if (isEmpty(address.getStreet())) {
                address.setStreet(addr.getStreet());
            }
            if (isEmpty(address.getBuildingNum())) {
                address.setBuildingNum(addr.getHouseNumber());
            }
            if (isEmpty(address.getCity())) {
                address.setCity(addr.getTown());
            }
            if (isEmpty(address.getCountry())) {
                address.setCountry(addr.getCountry().getCountryNameShort());
            }
            if (isEmpty(address.getZip())) {
                address.setZip(findPlz(addr.getForeignZipCodeOrSwissZipCodeOrSwissZipCodeAddOn()));
            }
            if (isEmpty(address.getCanton())) {
                if (organisation.getCantonAbbreviationMainAddress() != null) {
                    address.setCanton(organisation.getCantonAbbreviationMainAddress().value());
                }
            }
            if (isEmpty(address.getCommunityNumber())) {
                if (organisation.getOrganisationMunicipality() != null
                    && organisation.getOrganisationMunicipality().getMunicipalityId() != null) {
                    address.setCommunityNumber(organisation.getOrganisationMunicipality().getMunicipalityId().toString());
                }
            }

        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

    private String findOrganisationIdByCategory(List<NamedOrganisationIdType> ids, String category) {
        for (NamedOrganisationIdType id : ids) {
            if (id.getOrganisationIdCategory().equals(category)) {
                return id.getOrganisationId();
            }
        }
        return null;
    }

    private AddressInformationType findAddressByCategory(List<AddressType> addresses, String category) {
        for (AddressType address : addresses) {
            if (address.getOtherAddressCategory().equals(category)) {
                return address.getPostalAddress().getAddressInformation();
            }
        }
        return null;
    }

    private String findPlz(List<JAXBElement<? extends Serializable>> swissZipCodeIdOrSwissZipCodeOrSwissZipCodeAddOn) {
        for (JAXBElement<?> element : swissZipCodeIdOrSwissZipCodeOrSwissZipCodeAddOn) {
            if (element.getName().getLocalPart().equals(SWISS_ZIP_CODE)) {
                return "" + element.getValue();
            }
        }
        return null;
    }

    private void setFirmActive(FirmData firm, ch.ech.xmlns.ech_0108_f._3.OrganisationType organisation) {
        boolean detailOk = isEnterpriseStatusValid(organisation);

        boolean statusOk = true;
        CommercialRegisterInformationType crInfo = organisation.getCommercialRegisterInformation();
        if (crInfo != null) {
            String commercialEntryStatus = crInfo.getCommercialRegisterEntryStatus();
            statusOk = COMMERCIAL_REGISTER_ACTIVE.equals(commercialEntryStatus);
        }
        firm.setActive(detailOk && statusOk);
    }

    private void setFirmPublicFlag(FirmData firm, ch.ech.xmlns.ech_0108_f._3.OrganisationType organisation) {
        boolean isPublic = UID_STATUS_PUBLIC.equals(organisation.getUidregInformation().getUidregPublicStatus());
        firm.setUidPublic(isPublic);
    }

    private boolean isEnterpriseStatusValid(ch.ech.xmlns.ech_0108_f._3.OrganisationType organisation) {
        String detail = organisation.getUidregInformation().getUidregStatusEnterpriseDetail();
        return detail.equals(ENTERPRISE_STATUS_DEFINITIVE) || detail.equals(ENTERPRISE_STATUS_MUTATING);
    }

    private void setCommercialRegisterEntryDate(FirmData firm, ch.ech.xmlns.ech_0108_f._3.OrganisationType organisation) {
        CommercialRegisterInformationType commercialInfo = organisation.getCommercialRegisterInformation();
        if (commercialInfo != null) {
            XMLGregorianCalendar entryDate = commercialInfo.getCommercialRegisterEntryDate();
            if (entryDate != null) {
                firm.setCommercialRegisterEntryDate(entryDate.toGregorianCalendar().getTime());
            }
        }
    }

    private String firmListToString(List<FirmData> firms) {
        StringBuilder sb = new StringBuilder();
        for (FirmData firm : firms) {
            sb.append("\n").append(firm.toString());
        }
        return sb.toString();
    }

}
