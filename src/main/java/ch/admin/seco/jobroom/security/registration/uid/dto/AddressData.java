package ch.admin.seco.jobroom.security.registration.uid.dto;

/**
 * Address DTO.
 */
public class AddressData {
    private String street;
    private String buildingNum;
    private String streetAddOn;
    private String zip;
    private String city;
    private String canton;
    private String country;
    private String communityNumber;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCanton() {
        return canton;
    }

    public void setCanton(String canton) {
        this.canton = canton;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getStreetAddOn() {
        return streetAddOn;
    }

    public void setStreetAddOn(String streetAddOn) {
        this.streetAddOn = streetAddOn;
    }

    public String getBuildingNum() {
        return buildingNum;
    }

    public void setBuildingNum(String buildingNum) {
        this.buildingNum = buildingNum;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCommunityNumber() {
        return communityNumber;
    }

    public void setCommunityNumber(String communityNumber) {
        this.communityNumber = communityNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AddressData address = (AddressData) o;

        if (buildingNum != null ? !buildingNum.equals(address.buildingNum) : address.buildingNum != null) {
            return false;
        }
        if (canton != null ? !canton.equals(address.canton) : address.canton != null) {
            return false;
        }
        if (city != null ? !city.equals(address.city) : address.city != null) {
            return false;
        }
        if (country != null ? !country.equals(address.country) : address.country != null) {
            return false;
        }
        if (street != null ? !street.equals(address.street) : address.street != null) {
            return false;
        }
        if (streetAddOn != null ? !streetAddOn.equals(address.streetAddOn) : address.streetAddOn != null) {
            return false;
        }
        if (zip != null ? !zip.equals(address.zip) : address.zip != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = street != null ? street.hashCode() : 0;
        result = 31 * result + (buildingNum != null ? buildingNum.hashCode() : 0);
        result = 31 * result + (streetAddOn != null ? streetAddOn.hashCode() : 0);
        result = 31 * result + (zip != null ? zip.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (canton != null ? canton.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AddressData{" +
                "street='" + street + '\'' +
                ", buildingNum='" + buildingNum + '\'' +
                ", streetAddOn='" + streetAddOn + '\'' +
                ", zip='" + zip + '\'' +
                ", city='" + city + '\'' +
                ", canton='" + canton + '\'' +
                ", country='" + country + '\'' +
                ", communityNumber='" + communityNumber + '\'' +
                '}';
    }
}
