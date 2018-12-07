import { JobDescription } from '../../../shared/job-advertisement/job-advertisement.model';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { LanguageSkill } from '../../../shared';
import { OccupationOption } from '../../../shared/reference-service';

export interface OccupationFormModel {
    occupationSuggestion: OccupationOption;
    degree: string;
    experience: string;
}

export interface EmploymentStartDate {
    immediate: boolean;
    date: NgbDateStruct;
}

export interface EmploymentEndDate {
    permanent: boolean;
    date: NgbDateStruct;
}

export interface LocationFormModel {
    countryCode: string;
    additionalDetails: string;
    zipCode: ZipCode;
}

export interface EmploymentFormModel {
    workload: [number, number];
    employmentStartDate: EmploymentStartDate;
    employmentEndDate: EmploymentEndDate;
    shortEmployment: boolean;
    workForms: [boolean, boolean, boolean, boolean],
}

export interface ZipCode {
    zip: string;
    city: string;
    communalCode?: string;
}

export interface CompanyFormModel {
    name: string;
    street: string;
    houseNumber: string;
    zipCode: ZipCode;
    postboxNumber: string;
    countryCode: string;
    surrogate: boolean;
}

export interface ContactFormModel {
    language: string;
    salutation: string;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    email: string;
}

export interface PublicContactFormModel {
    salutation: string;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    email: string;
}

export interface ApplicationFormModel {
    selectElectronicApplicationUrl: boolean;
    selectElectronicApplicationEmail: boolean;
    selectPhoneNumber: boolean;
    selectPaperApp: boolean;

    postAddress: {
        paperAppCompanyName: string;
        paperAppStreet: string;
        paperAppHouseNr: string;
        paperAppPostboxNr: string;
        paperAppZip: ZipCode;
        paperAppCountryCode: string;
    };

    electronicApplicationEmail: string;
    electronicApplicationUrl: string;
    phoneNumber: string;
    additionalInfo: string;
}

export interface PublicationFormModel {
    publicDisplay: boolean;
    eures: boolean;
}

export interface EmployerFormModel {
    name: string;
    zipCode: ZipCode;
    countryCode: string;
}

export interface JobPublicationForm {
    numberOfJobs: string;
    jobDescriptions: JobDescription[];
    occupation: OccupationFormModel;
    languageSkills: LanguageSkill[];
    employment: EmploymentFormModel;
    location: LocationFormModel;
    company: CompanyFormModel;
    employer: EmployerFormModel;
    contact: ContactFormModel;
    publicContact: PublicContactFormModel;
    application: ApplicationFormModel;
    publication: PublicationFormModel;
}
