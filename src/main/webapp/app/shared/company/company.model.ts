export const enum CompanySource {
    'AVG',
    'UID'
}

export interface Company {
    id: string;
    externalId: string;
    name: string;
    street: string;
    zipCode: string;
    city: string;
    email: string;
    phone: string;
    source: CompanySource;

}
