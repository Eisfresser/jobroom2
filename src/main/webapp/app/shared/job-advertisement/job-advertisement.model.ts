import { CEFR_Level, WorkForm } from '..';
import { GeoPoint } from '../reference-service';

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

export enum CancellationReason {
    OCCUPIED_JOBCENTER,
    OCCUPIED_AGENCY,
    OCCUPIED_JOBROOM,
    OCCUPIED_OTHER,
    NOT_OCCUPIED,
    CHANGE_OR_REPOSE
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
    NO_EXPERIENCE,
    LESS_THAN_1_YEAR,
    MORE_THAN_1_YEAR,
    MORE_THAN_3_YEARS
}

export interface JobContent {
    externalUrl?: string;
    jobDescriptions: JobDescription[];
    company: Company;
    employer?: Employer;
    employment: Employment;
    location: Location;
    occupations: Occupation[];
    languageSkills: LanguageSkill[];
    applyChannel: ApplyChannel;
    publicContact?: PublicContact;
    numberOfJobs: string;
}

export interface JobAdvertisement {
    id: string;
    status: JobAdvertisementStatus;
    sourceSystem: SourceSystem;
    externalReference: string;
    stellennummerEgov: string;
    stellennummerAvam: string;
    fingerprint: string;
    reportingObligation?: boolean;
    reportingObligationEndDate?: string;
    reportToAvam: boolean;
    jobCenterCode: string;
    approvalDate?: string;
    rejectionDate?: string;
    rejectionCode?: string;
    rejectionReason?: string;
    cancellationDate?: string;
    cancellationCode?: string;
    jobContent: JobContent;
    publication: Publication;
    visited?: boolean;
}

export interface Publication {
    startDate: string;
    endDate?: string;
    euresDisplay: boolean;
    euresAnonymous?: boolean;
    publicDisplay: boolean;
    restrictedDisplay?: boolean;
    companyAnonymous?: boolean;
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

export interface Location {
    remarks?: string;
    city: string;
    postalCode: string;
    countryIsoCode: string;
    communalCode?: string;
    regionCode?: string;
    cantonCode?: string;
    coordinates?: GeoPoint;
}

export interface CreateLocation {
    remarks?: string;
    city: string;
    postalCode: string;
    countryIsoCode: string;
}

export interface Occupation {
    avamOccupationCode: string;
    occupationLabel?: string;
    workExperience?: WorkExperience;
    educationCode?: string;
}

export interface LanguageSkill {
    languageIsoCode: string;
    spokenLevel: CEFR_Level;
    writtenLevel: CEFR_Level;
}

export interface ApplyChannel {
    postAddress: ApplyChannelPostAddress;
    rawPostAddress: string;
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
    numberOfJobs: string;
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

export interface ApplyChannelPostAddress {
    name: string;
    street: string;
    houseNumber: string;
    postalCode: string;
    city: string;
    postOfficeBoxNumber: string;
    postOfficeBoxPostalCode: string;
    postOfficeBoxCity: string;
    countryIsoCode: string;
}
