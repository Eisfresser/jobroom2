export enum RegistrationStatus {
    UNREGISTERED = <any>'UNREGISTERED',
    REGISTERED = <any>'REGISTERED',
    VALIDATION_EMP = <any>'VALIDATION_EMP',
    VALIDATION_PAV = <any>'VALIDATION_PAV'
}

export interface Accountability {
    companyId: string;
    companyName: string;
    companyExternalId: string;
}

export interface CompanyContactTemplate {
    companyId: string
    companyName?: string;
    companyStreet?: string;
    companyHouseNr?: string;
    companyZipCode?: string;
    companyCity?: string;
    phone?: string;
    email?: string;
    salutation?: string;
}

export class UserInfoDto {
    id: string;
    userExternalId: string;
    firstName: string;
    lastName: string;
    email: string;
    registrationStatus: RegistrationStatus;
    accountabilities: Array<{ companyName: string, companyExternalId: string, companySource: string }> = [];
    createdAt: Date;
    modifiedAt: Date;
    lastLoginAt: Date;
    stesInformation: { personNumber: string, verificationType: string, verifiedAt: Date } = null;
}
