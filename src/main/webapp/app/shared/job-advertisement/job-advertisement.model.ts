import { CEFR_Level, WorkForm } from '..';

export enum JobAdvertisementStatus {
    CREATED,
    INSPECTING,
    APPROVED,
    REJECTED,
    REFINING,
    PUBLISHED_RESTRICTED,
    PUBLISHED_PUBLIC,
    CANCELLED,
    ARCHIVE
}

export enum SourceSystem {
    JOBROOM,
    API,
    RAV,
    EXTERN
}

export enum Salutation {
    MR, MS
}

export enum WorkExperience {
    LESS_THAN_1_YEAR,
    MORE_THAN_1_YEAR,
    MORE_THAN_3_YEARS
}

export interface JobContent {
    externalUrl: string;
    jobDescriptions: JobDescription[];
    company: Company;
    employer: Employer;
    employment: Employment;
    location: Location;
    occupations: Occupation[];
    languageSkills: LanguageSkill[];
    applyChannel: ApplyChannel;
    publicContact: PublicContact;
}

export interface Owner {
    userId: string;
    companyId: string;
    accessToken: string;
}

export interface JobAdvertisement {
    id: string;
    status: JobAdvertisementStatus;
    sourceSystem: SourceSystem;
    externalReference: string;
    stellennummerEgov: string;
    stellennummerAvam: string;
    fingerprint: string;
    reportingObligation: boolean;
    reportingObligationEndDate: string;
    reportToAvam: boolean;
    jobCenterCode: string;
    approvalDate: string;
    rejectionDate: string;
    rejectionCode: string;
    rejectionReason: string;
    cancellationDate: string;
    cancellationCode: string;
    jobContent: JobContent;
    owner: Owner;
    contact: Contact;
    publication: Publication;
}

export interface Publication {
    startDate: string;
    endDate?: string;
    euresDisplay: boolean;
    euresAnonymous: boolean;
    publicDisplay: boolean;
    publicAnonynomous: boolean; // FIX typo on the back-end
    restrictedDisplay: boolean;
    restrictedAnonymous: boolean;
}

export interface JobDescription {
    languageIsoCode: string;
    title: string;
    description: string;
}

export interface Company {
    name: string;
    street: string;
    houseNumber?: string;
    postalCode: string;
    city: string;
    countryIsoCode: string;
    postOfficeBoxNumber?: string;
    postOfficeBoxPostalCode?: string;
    postOfficeBoxCity?: string;
    phone?: string;
    email?: string;
    website?: string;
    surrogate?: boolean;
}

export interface Employer {
    name: string;
    postalCode: string;
    city: string;
    countryIsoCode: string;
}

export interface Employment {
    startDate?: string;
    endDate?: string;
    shortEmployment: boolean;
    immediately: boolean;
    permanent: boolean;
    workloadPercentageMin: number;
    workloadPercentageMax: number;
    workForms?: string[];
}

export interface CreateLocation {
    remarks?: string;
    city: string;
    postalCode: string;
    countryIsoCode: string;
}

export interface Occupation {
    avamOccupationCode: string;
    workExperience: WorkExperience;
    educationCode: string;
}

export interface LanguageSkill {
    languageIsoCode: string;
    spokenLevel: CEFR_Level;
    writtenLevel: CEFR_Level;
}

export interface ApplyChannel {
    mailAddress: string;
    emailAddress: string;
    phoneNumber: string;
    formUrl: string;
    additionalInfo: string;
}

export interface Contact {
    salutation: Salutation;
    firstName: string;
    lastName: string;
    phone: string;
    email: string;
    languageIsoCode: string;
}

export interface PublicContact {
    salutation: Salutation;
    firstName: string;
    lastName: string;
    phone: string;
    email: string;
}

export interface CreateJobAdvertisement {
    reportToAvam: boolean;
    externalUrl?: string;
    contact: Contact;
    publication: Publication;
    jobDescriptions: JobDescription[];
    company: Company;
    employer: Employer;
    employment: Employment;
    location: CreateLocation;
    occupation: Occupation;
    languageSkills: LanguageSkill[];
    applyChannel: ApplyChannel;
    publicContact: PublicContact;
}
