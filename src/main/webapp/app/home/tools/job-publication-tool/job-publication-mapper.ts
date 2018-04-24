import { JobPublication } from '../../../shared/job-publication/job-publication.model';
import { OccupationOption } from '../../../shared/reference-service';
import { DateUtils, WorkForm } from '../../../shared';
import {
    CreateJobAdvertisement,
    LanguageSkill, Salutation
} from '../../../shared/job-advertisement/job-advertisement.model';
import { JobPublicationForm } from './job-publication-form.model';
import * as moment from 'moment';

export class JobPublicationMapper {

    private static readonly DEGREE = {
        'SEK_II_WEITERFUEHRENDE_SCHULE': '130',
        'SEK_II_GRUNDBILDUNG_EBA': '131',
        'SEK_II_GRUNDBILDUNG_EFZ': '132',
        'SEK_II_FACHMITTELSCHULE': '133',
        'SEK_II_BERUFSMATURITAET': '134',
        'SEK_II_FACHMATURITAET': '135',
        'SEK_II_GYMNASIALE_MATURITAET': '136',
        'TER_BERUFSBILDUNG_FA': '150',
        'TER_BERUFSBILDUNG_DIPL': '160',
        'TER_BACHELOR_FACHHOCHSCHULE': '170',
        'TER_BACHELOR_UNIVERSITAET': '171',
        'TER_MASTER_FACHHOCHSCHULE': '172',
        'TER_MASTER_UNIVERSITAET': '173',
        'TER_DOKTORAT_UNIVERSITAET': '180'
    };

    // TODO: update
    static mapJobPublicationToFormModel(jobPublication: JobPublication): JobPublicationForm {
        const value: any = Object.assign({}, jobPublication);
        const workload = [jobPublication.job.workingTimePercentageMin,
            jobPublication.job.workingTimePercentageMax];

        value.job.occupation.occupationSuggestion = {
            key: jobPublication.idAvam,
            label: jobPublication.job.occupation.avamOccupation
        } as OccupationOption;

        value.job.languageSkills = value.job.languageSkills
            .map((languageSkill) => ({
                code: languageSkill.code,
                spoken: languageSkill.spokenLevel,
                written: languageSkill.writtenLevel
            }));

        Object.assign(value.job.location, {
            zipCode: {
                zip: jobPublication.job.location.zipCode,
                city: jobPublication.job.location.city,
                communalCode: jobPublication.job.location.communalCode
            }
        });

        Object.assign(value.job, {
            workload,
            publicationStartDate: {
                immediate: jobPublication.job.startsImmediately,
                date: DateUtils.dateStringToToNgbDateStruct(jobPublication.job.startDate)
            },
            publicationEndDate: {
                permanent: jobPublication.job.permanent,
                date: DateUtils.dateStringToToNgbDateStruct(jobPublication.job.endDate)
            },
        });

        Object.assign(value.company, {
            zipCode: {
                zip: jobPublication.company.zipCode,
                city: jobPublication.company.city,
            },
            postboxZipCode: {
                zip: jobPublication.company.postboxZipCode,
                city: jobPublication.company.postboxCity,
            }
        });

        return value;
    }

    static mapJobPublicationFormToCreateJobAdvertisement(jobPublicationForm: JobPublicationForm): CreateJobAdvertisement {
        const jobAd: CreateJobAdvertisement = {
            reportToAvam: true
        } as CreateJobAdvertisement;

        jobAd.jobDescriptions = jobPublicationForm.jobDescriptions;

        jobAd.occupation = {
            avamOccupationCode: JobPublicationMapper.getAvamOccupationCode(jobPublicationForm),
            workExperience: jobPublicationForm.occupation.experience,
            educationCode: jobPublicationForm.occupation.degree ? JobPublicationMapper.DEGREE[jobPublicationForm.occupation.degree.toString()] : null
        };

        jobAd.languageSkills = jobPublicationForm.languageSkills
            .filter((languageSkill) => languageSkill.code && languageSkill.code.length)
            .map((languageSkill) => ({
                languageIsoCode: languageSkill.code,
                spokenLevel: languageSkill.spoken,
                writtenLevel: languageSkill.written
            } as LanguageSkill));

        jobAd.employment = {
            startDate: DateUtils.convertNgbDateStructToString(jobPublicationForm.employment.employmentStartDate.date),
            endDate: DateUtils.convertNgbDateStructToString(jobPublicationForm.employment.employmentEndDate.date),
            shortEmployment: jobPublicationForm.employment.shortEmployment,
            immediately: jobPublicationForm.employment.employmentStartDate.immediate,
            permanent: jobPublicationForm.employment.employmentEndDate.permanent,
            workloadPercentageMin: jobPublicationForm.employment.workload[0],
            workloadPercentageMax: jobPublicationForm.employment.workload[1],
            workForms: JobPublicationMapper.mapWorkForms(jobPublicationForm.employment.workForms)
        };

        jobAd.location = {
            remarks: jobPublicationForm.location.additionalDetails,
            city: jobPublicationForm.location.zipCode.city,
            postalCode: jobPublicationForm.location.zipCode.zip,
            countryIsoCode: jobPublicationForm.location.countryCode
        };

        jobAd.company = {
            name: jobPublicationForm.company.name,
            street: jobPublicationForm.company.street,
            houseNumber: jobPublicationForm.company.houseNumber,
            postalCode: jobPublicationForm.company.zipCode.zip,
            city: jobPublicationForm.company.zipCode.city,
            countryIsoCode: jobPublicationForm.company.countryCode,
            postOfficeBoxNumber: jobPublicationForm.company.postboxNumber,
            surrogate: jobPublicationForm.company.surrogate
        };

        if (jobPublicationForm.company.postboxZipCode) {
            Object.assign(jobAd.company, {
                postOfficeBoxPostalCode: jobPublicationForm.company.postboxZipCode.zip,
                postOfficeBoxCity: jobPublicationForm.company.postboxZipCode.city
            });
        }

        jobAd.employer = {
            name: jobPublicationForm.employer.name,
            postalCode: jobPublicationForm.employer.zipCode.zip,
            city: jobPublicationForm.employer.zipCode.city,
            countryIsoCode: jobPublicationForm.employer.countryCode
        };

        jobAd.contact = {
            salutation: Salutation[jobPublicationForm.contact.salutation],
            firstName: jobPublicationForm.contact.firstName,
            lastName: jobPublicationForm.contact.lastName,
            phone: jobPublicationForm.contact.phoneNumber,
            email: jobPublicationForm.contact.email,
            languageIsoCode: jobPublicationForm.contact.language
        };

        jobAd.publicContact = {
            salutation: Salutation[jobPublicationForm.publicContact.salutation],
            firstName: jobPublicationForm.publicContact.firstName,
            lastName: jobPublicationForm.publicContact.lastName,
            phone: jobPublicationForm.publicContact.phoneNumber,
            email: jobPublicationForm.publicContact.email
        };

        jobAd.applyChannel = {
            mailAddress: jobPublicationForm.application.paperApplicationAddress,
            emailAddress: jobPublicationForm.application.electronicApplicationEmail,
            phoneNumber: jobPublicationForm.application.phoneNumber,
            formUrl: jobPublicationForm.application.electronicApplicationUrl,
            additionalInfo: jobPublicationForm.application.additionalInfo
        };

        jobAd.publication = {
            startDate: moment().format('YYYY-MM-DD'),
            endDate: null,
            euresDisplay: jobPublicationForm.publication.eures,
            euresAnonymous: jobPublicationForm.publication.euresAnonymous,
            publicDisplay: jobPublicationForm.publication.publicDisplay,
            publicAnonynomous: jobPublicationForm.publication.publicAnonymous,
            restrictedDisplay: jobPublicationForm.publication.restrictedDisplay,
            restrictedAnonymous: jobPublicationForm.publication.restrictedAnonymous
        };

        return jobAd;
    }

    private static getAvamOccupationCode(jobPublicationForm: JobPublicationForm): string {
        const jobFormOccupation = jobPublicationForm.occupation.occupationSuggestion;
        return jobFormOccupation ? jobFormOccupation.key.replace('avam:', '') : null;
    }

    private static mapWorkForms(workForms: [boolean, boolean, boolean, boolean]): string[] {
        return workForms
            .map((workFormSelected: boolean, index: number) => workFormSelected ? <string>WorkForm[index] : null)
            .filter((workForm) => !!workForm);
    }
}
