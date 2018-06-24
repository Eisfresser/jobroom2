package ch.admin.seco.jobroom.security.registration.uid;

import java.util.Date;

/**
 * Firm DTO.
 */
public class FirmData implements Comparable<FirmData> {

    private String name;

    private String additionalName;

    private String chId;

    private String uidPrefix;

    private int uid;

    private boolean active;

    private Date commercialRegisterEntryDate;

    private AddressData address;

    private String mwst;

    private String vatEntryStatus;

    private Date vatLiquidationDate;

    private boolean uidPublic;

    public FirmData() {
    }

    public Date getVatLiquidationDate() {
        return vatLiquidationDate;
    }

    public void setVatLiquidationDate(Date vatLiquidationDate) {
        this.vatLiquidationDate = vatLiquidationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdditionalName() {
        return additionalName;
    }

    public void setAdditionalName(String additionalName) {
        this.additionalName = additionalName;
    }

    public String getUidPrefix() {
        return uidPrefix;
    }

    public void setUidPrefix(String uidPrefix) {
        this.uidPrefix = uidPrefix;
    }

    public String getChId() {
        return chId;
    }

    public void setChId(String chId) {
        this.chId = chId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCommercialRegisterEntryDate() {
        return commercialRegisterEntryDate;
    }

    public void setCommercialRegisterEntryDate(Date commercialRegisterEntryDate) {
        this.commercialRegisterEntryDate = commercialRegisterEntryDate;
    }

    public AddressData getAddress() {
        return address;
    }

    public void setAddress(AddressData address) {
        this.address = address;
    }

    public String getMwst() {
        return mwst;
    }

    public void setMwst(String mwst) {
        this.mwst = mwst;
    }

    public void setVatEntryStatus(String vatEntryStatus) {
        this.vatEntryStatus = vatEntryStatus;
    }

    public String getVatEntryStatus() {
        return vatEntryStatus;
    }

    public boolean isUidPublic() {
        return uidPublic;
    }

    public void setUidPublic(boolean uidPublic) {
        this.uidPublic = uidPublic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FirmData firm = (FirmData) o;

        if (active != firm.active) {
            return false;
        }
        if (uid != firm.uid) {
            return false;
        }
        if (address != null ? !address.equals(firm.address) : firm.address != null) {
            return false;
        }
        if (chId != null ? !chId.equals(firm.chId) : firm.chId != null) {
            return false;
        }
        if (commercialRegisterEntryDate != null ? !commercialRegisterEntryDate.equals(firm.commercialRegisterEntryDate) : firm.commercialRegisterEntryDate != null) {
            return false;
        }
        if (name != null ? !name.equals(firm.name) : firm.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (chId != null ? chId.hashCode() : 0);
        result = 31 * result + uid;
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (commercialRegisterEntryDate != null ? commercialRegisterEntryDate.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(FirmData f) {
        int res = Boolean.compare(f.active, active);
        if (res == 0) {
            res = name.compareToIgnoreCase(f.name);
        }
        if (res == 0 && address != null && f.address != null) {
            res = address.getCity().compareToIgnoreCase(f.address.getCity());
        }
        return res;
    }

    @Override
    public String toString() {
        return "FirmData{" +
            "name='" + name + '\'' +
            ", chId='" + chId + '\'' +
            ", uid=" + uid +
            ", active=" + active +
            ", commercialRegisterEntryDate=" + commercialRegisterEntryDate +
            ", address=" + address +
            ", mwst='" + mwst + '\'' +
            ", vatEntryStatus='" + vatEntryStatus + '\'' +
            ", vatLiquidationDate=" + vatLiquidationDate +
            ", uidPublic=" + uidPublic +
            '}';
    }
}
