import { CEFR_Level, Degree, DrivingLicenceCategory } from '../';

export enum Experience {
    NO_EXPERIENCE,
    LESS_THAN_1_YEAR,
    BETWEEN_1_AND_3_YEARS,
    MORE_THAN_3_YEARS
}

export interface Location {
    countryCode: string;
    zipCode?: string;
    city?: string;
    communalCode?: string;
    additionalDetails: string;
}

export interface LanguageSkill {
    code: string;
    spokenLevel: CEFR_Level;
    writtenLevel: CEFR_Level;
}

export interface Occupation {
    avamOccupation: string;
    degree?: Degree;
    experience?: Experience;
}

export interface Job {
    title: string;
    occupation: Occupation;
    description: string;
    workingTimePercentageMin: number;
    workingTimePercentageMax: number;
    startsImmediately: boolean;
    startDate: string;
    endDate: string;
    permanent: boolean;
    drivingLicenseLevel?: DrivingLicenceCategory;
    languageSkills: LanguageSkill[];
    location: Location;
}

export interface Company {
    name: string;
    street: string;
    houseNumber?: string;
    zipCode?: string;
    city?: string;
    postboxNumber: string;
    postboxZipCode?: string;
    postboxCity?: string;
    countryCode: string;
}

export interface Contact {
    salutation: string;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    email: string;
}

export interface Application {
    written: boolean;
    electronic: boolean;
    email: string;
    url: string;
    phoneEnabled: boolean;
    phoneNumber: string;
    additionalInfo: string;
}

export interface Publication {
    jobroom: boolean;
    eures: boolean;
}

export enum Status {
    INITIAL,
    IN_PROGRESS,
    DISMISSED,
    ACTIVE,
    UNSUBSCRIBED,
    UNKNOWN
}

export enum Locale {
    DE,
    FR,
    IT
}

export interface JobPublication {
    id?: string;
    idAvam?: string;
    accessToken: string;
    job: Job;
    company: Company;
    contact: Contact;
    application: Application;
    publication: Publication;
    creationDate: string;
    locale: Locale;
    status?: Status;
}

export enum CancellationReason {
    POSITION_OCCUPIED_SELF,
    POSITION_OCCUPIED_JOB_CENTER,
    POSITION_OCCUPIED_PRIVATE_AGENCY,
    POSITION_OCCUPIED_BOTH,
    POSITION_NOT_OCCUPIED
}
