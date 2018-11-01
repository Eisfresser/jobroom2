export interface Company {
    name: string;
    additionalName: string;
    chId: string;
    uidPrefix: string;
    uid: string;
    active: string;
    commercialRegisterEntryDate: string;
    address: CompanyAddress;
    mwst: string,
    vatEntryStatus: string,
    vatLiquidationDate: string,
    uidPublic: string
}

export interface CompanyAddress {
    street: string;
    buildingNum: string;
    streetAddOn: string;
    zip: string;
    city: string;
    canton: string;
    country: string;
    communityNumber: string;
}
