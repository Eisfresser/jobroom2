import { CompanyContactTemplate } from '../user-info/user-info.model';
import { CurrentUser } from '..';

export class CompanyContactTemplateModel {

    private readonly _companyContactTemplate: CompanyContactTemplate;

    private readonly _firstName: string;

    private readonly _lastName: string;

    constructor(companyContactTemplate: CompanyContactTemplate, currentUser: CurrentUser) {
        this._companyContactTemplate = companyContactTemplate;
        this._firstName = currentUser.firstName;
        this._lastName = currentUser.lastName;
    }

    get companyContactTemplate(): CompanyContactTemplate {
        return this._companyContactTemplate;
    }

    get companyId(): string {
        return this._companyContactTemplate.companyId;
    }

    get companyName(): string {
        return this._companyContactTemplate.companyName;
    }

    get companyStreet(): string {
        return this._companyContactTemplate.companyStreet;
    }

    get companyHouseNr(): string {
        return this._companyContactTemplate.companyHouseNr;
    }

    get companyCity(): string {
        return this._companyContactTemplate.companyCity;
    }

    get companyZipCode(): string {
        return this._companyContactTemplate.companyZipCode;
    }

    get salutation(): string {
        return this._companyContactTemplate.salutation;
    }

    get phone(): string {
        return this._companyContactTemplate.phone;
    }

    get email(): string {
        return this._companyContactTemplate.email;
    }

    get firstName(): string {
        return this._firstName;
    }

    get lastName(): string {
        return this._lastName;
    }

}
