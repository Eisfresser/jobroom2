import {Address} from "../job-advertisement/job-advertisement.model";

export class AddressMapper {

    public static mapAddressToString(address: Address): string {
        if (address) {
            let paperApplicationAddress = address.name;
            if (address.street) {
                paperApplicationAddress += ',' + address.street;
                if (address.houseNumber) {
                    paperApplicationAddress += ' ' + address.houseNumber;
                }
            }
            paperApplicationAddress += ',' + address.postalCode + ' ' + address.city;
            if (address.postOfficeBoxNumber) {
                paperApplicationAddress += "," + address.postOfficeBoxNumber;
            }
            if (address.postOfficeBoxPostalCode) {
                paperApplicationAddress += "," + address.postOfficeBoxPostalCode;
            }
            if (address.postOfficeBoxCity) {
                paperApplicationAddress += "," + address.postOfficeBoxCity;
            }
            return paperApplicationAddress;
        }
        return null;
    }
}
