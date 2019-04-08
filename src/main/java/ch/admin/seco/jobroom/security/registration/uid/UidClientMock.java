package ch.admin.seco.jobroom.security.registration.uid;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UidClientMock implements UidClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(UidClientMock.class);

    public FirmData getCompanyByUid(long uid) {
        LOGGER.debug("getCompanyByUid called with uid=" + uid);
        FirmData firm = new FirmData();
        firm.setName("mimacom ag");
        firm.setAdditionalName("Software Development");
        firm.setUid(115635627);
        firm.setChId("CH03630474042");
        firm.setUidPrefix("CHE");
        firm.setUidPublic(true);
        firm.setCommercialRegisterEntryDate(Date.from(LocalDate.of(2010, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        firm.setActive(true);
        firm.setMwst("CHE-115.635.627");
        firm.setVatEntryStatus("ACTIVE");
        firm.setVatLiquidationDate(null);
        AddressData address = new AddressData();
        address.setStreet("Galgenfeldweg");
        address.setBuildingNum("16");
        address.setZip("3006");
        address.setCity("Bern");
        address.setCanton("BE");
        address.setCommunityNumber("351");
        address.setCountry("CH");
        firm.setAddress(address);
        return firm;
    }

}
