import {
    ApplyChannelPostAddress
} from '../job-advertisement/job-advertisement.model';
import { TranslateService } from '@ngx-translate/core';

export class AddressMapper {

    public static mapAddressToString(address: ApplyChannelPostAddress, translate: TranslateService): string {
        if (address) {
            let paperApplicationAddress = address.name;

            if (address.postOfficeBoxNumber) {
                paperApplicationAddress += ', ' + translate.instant('home.tools.job-publication.company.postbox') + ' ' + address.postOfficeBoxNumber;
                paperApplicationAddress += ', ' + address.postOfficeBoxPostalCode + ' ' + address.postOfficeBoxCity;
            } else {
                if (address.street) {
                    paperApplicationAddress += ', ' + address.street;
                }
                if (address.houseNumber) {
                    paperApplicationAddress += ' ' + address.houseNumber;
                }
                paperApplicationAddress += ', ' + address.postalCode + ' ' + address.city;
            }
            return paperApplicationAddress;
        }
        return null;
    }
}
