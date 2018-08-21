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
