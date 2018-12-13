import { DateUtils, Degree, WorkForm } from '../../../shared';
import {
    ApplyChannel,
    Company,
    CreateJobAdvertisement,
    JobAdvertisement,
    LanguageSkill,
    Salutation,
    WorkExperience
} from '../../../shared/job-advertisement/job-advertisement.model';
import {
    ApplicationFormModel,
    CompanyFormModel,
    JobPublicationForm
} from './job-publication-form.model';
import * as moment from 'moment';

export class JobPublicationMapper {

    private static readonly EMPTY_POST_ADDRESS = {
        name: '',
        street: '',
        houseNumber: '',
        postalCode: '',
        city: '',
        postOfficeBoxNumber: '',
        postOfficeBoxPostalCode: '',
        postOfficeBoxCity: '',
        countryIsoCode: ''
    };

    private static readonly DEGREE = {
        [Degree[Degree.SEK_II_WEITERFUEHRENDE_SCHULE]]: '130',
        [Degree[Degree.SEK_II_GRUNDBILDUNG_EBA]]: '131',
        [Degree[Degree.SEK_II_GRUNDBILDUNG_EFZ]]: '132',
        [Degree[Degree.SEK_II_FACHMITTELSCHULE]]: '133',
        [Degree[Degree.SEK_II_BERUFSMATURITAET]]: '134',
        [Degree[Degree.SEK_II_FACHMATURITAET]]: '135',
        [Degree[Degree.SEK_II_GYMNASIALE_MATURITAET]]: '136',
        [Degree[Degree.TER_BERUFSBILDUNG_FA]]: '150',
        [Degree[Degree.TER_BERUFSBILDUNG_DIPL]]: '160',
        [Degree[Degree.TER_BACHELOR_FACHHOCHSCHULE]]: '170',
        [Degree[Degree.TER_BACHELOR_UNIVERSITAET]]: '171',
        [Degree[Degree.TER_MASTER_FACHHOCHSCHULE]]: '172',
        [Degree[Degree.TER_MASTER_UNIVERSITAET]]: '173',
        [Degree[Degree.TER_DOKTORAT_UNIVERSITAET]]: '180',
        '130': Degree[Degree.SEK_II_WEITERFUEHRENDE_SCHULE],
        '131': Degree[Degree.SEK_II_GRUNDBILDUNG_EBA],
        '132': Degree[Degree.SEK_II_GRUNDBILDUNG_EFZ],
        '133': Degree[Degree.SEK_II_FACHMITTELSCHULE],
        '134': Degree[Degree.SEK_II_BERUFSMATURITAET],
        '135': Degree[Degree.SEK_II_FACHMATURITAET],
        '136': Degree[Degree.SEK_II_GYMNASIALE_MATURITAET],
        '150': Degree[Degree.TER_BERUFSBILDUNG_FA],
        '160': Degree[Degree.TER_BERUFSBILDUNG_DIPL],
        '170': Degree[Degree.TER_BACHELOR_FACHHOCHSCHULE],
        '171': Degree[Degree.TER_BACHELOR_UNIVERSITAET],
        '172': Degree[Degree.TER_MASTER_FACHHOCHSCHULE],
        '173': Degree[Degree.TER_MASTER_UNIVERSITAET],
        '180': Degree[Degree.TER_DOKTORAT_UNIVERSITAET]
    };

    static mapJobPublicationToFormModel(jobPublicationForm: JobPublicationForm, jobAdvertisement: JobAdvertisement): JobPublicationForm {
        jobPublicationForm.jobDescriptions = jobAdvertisement.jobContent.jobDescriptions;

        if (jobAdvertisement.jobContent.occupations && jobAdvertisement.jobContent.occupations.length) {
            const occupation = jobAdvertisement.jobContent.occupations[0];
            jobPublicationForm.occupation = {
                occupationSuggestion: {
                    key: 'avam:' + occupation.avamOccupationCode,
                    label: occupation.occupationLabel
                },
                degree: occupation.educationCode ? JobPublicationMapper.DEGREE[occupation.educationCode] : null,
                experience: occupation.workExperience ? WorkExperience[WorkExperience[occupation.workExperience]] : null
            };
        }

        jobPublicationForm.languageSkills = jobAdvertisement.jobContent.languageSkills
            .map((languageSkill) => ({
                code: languageSkill.languageIsoCode,
                spoken: languageSkill.spokenLevel,
                written: languageSkill.writtenLevel
            }));

        jobPublicationForm.employment = {
            workload: [jobAdvertisement.jobContent.employment.workloadPercentageMin, jobAdvertisement.jobContent.employment.workloadPercentageMax],
            employmentStartDate: {
                immediate: jobAdvertisement.jobContent.employment.immediately,
                date: DateUtils.dateStringToNgbDateStruct(jobAdvertisement.jobContent.employment.startDate)
            },
            employmentEndDate: {
                permanent: jobAdvertisement.jobContent.employment.permanent,
                date: DateUtils.dateStringToNgbDateStruct(jobAdvertisement.jobContent.employment.endDate)
            },
            shortEmployment: jobAdvertisement.jobContent.employment.shortEmployment,
            workForms: JobPublicationMapper.mapWorkFormsToBooleans(jobAdvertisement.jobContent.employment.workForms)
        };

        jobPublicationForm.location = {
            countryCode: jobAdvertisement.jobContent.location.countryIsoCode,
            additionalDetails: jobAdvertisement.jobContent.location.remarks,
            zipCode: {
                zip: jobAdvertisement.jobContent.location.postalCode,
                city: jobAdvertisement.jobContent.location.city,
                communalCode: jobAdvertisement.jobContent.location.communalCode
            },
        };

        this.mapCompanyToFormModel(jobAdvertisement.jobContent.company, jobPublicationForm);

        if (jobAdvertisement.jobContent.employer) {
            jobPublicationForm.employer = {
                name: jobAdvertisement.jobContent.employer.name,
                zipCode: {
                    zip: jobAdvertisement.jobContent.employer.postalCode,
                    city: jobAdvertisement.jobContent.employer.city
                },
                countryCode: jobAdvertisement.jobContent.employer.countryIsoCode
            };
        }

        if (jobAdvertisement.jobContent.publicContact) {
            jobPublicationForm.publicContact = {
                salutation: Salutation[Salutation[jobAdvertisement.jobContent.publicContact.salutation]],
                firstName: jobAdvertisement.jobContent.publicContact.firstName,
                lastName: jobAdvertisement.jobContent.publicContact.lastName,
                phoneNumber: jobAdvertisement.jobContent.publicContact.phone,
                email: jobAdvertisement.jobContent.publicContact.email
            };
        }

        this.mapApplyChannelToFormModel(jobAdvertisement.jobContent.applyChannel, jobPublicationForm)

        jobPublicationForm.publication = {
            publicDisplay: jobAdvertisement.publication.publicDisplay,
            eures: jobAdvertisement.publication.euresDisplay
        };

        return jobPublicationForm;
    }

    static mapCompanyToFormModel(company: Company, jobPublicationForm: JobPublicationForm) {
        if (company) {

            let addressPostalCode;
            let addressCity;

            if (company.postOfficeBoxNumber && company.postOfficeBoxPostalCode && company.postOfficeBoxCity) {
                addressPostalCode = company.postOfficeBoxPostalCode;
                addressCity = company.postOfficeBoxCity;
            } else {
                addressPostalCode = company.postalCode;
                addressCity = company.city;
            }

            jobPublicationForm.company = {
                name: company.name,
                street: company.street,
                houseNumber: company.houseNumber,
                zipCode: {
                    zip: addressPostalCode,
                    city: addressCity
                },
                postboxNumber: company.postOfficeBoxNumber,
                countryCode: company.countryIsoCode,
                surrogate: company.surrogate,
            };
        }

    }

    static mapApplyChannelToFormModel(applyChannel: ApplyChannel, jobPublicationForm: JobPublicationForm) {
        if (applyChannel) {
            const postAddress = applyChannel.postAddress || JobPublicationMapper.EMPTY_POST_ADDRESS;

            let addressPostalCode;
            let addressCity;
            if (postAddress.postOfficeBoxNumber && postAddress.postOfficeBoxPostalCode && postAddress.postOfficeBoxCity) {
                addressPostalCode = postAddress.postOfficeBoxPostalCode;
                addressCity = postAddress.postOfficeBoxCity;
            } else {
                addressPostalCode = postAddress.postalCode;
                addressCity = postAddress.city;
            }
            jobPublicationForm.application = {
                selectElectronicApplicationUrl: (!!applyChannel.formUrl),
                selectElectronicApplicationEmail: (!!applyChannel.emailAddress),
                selectPhoneNumber: (!!applyChannel.phoneNumber),
                selectPaperApp: (!!postAddress.name),
                postAddress: {
                    paperAppCompanyName: postAddress.name,
                    paperAppStreet: postAddress.street,
                    paperAppHouseNr: postAddress.houseNumber,
                    paperAppZip: {
                        zip: addressPostalCode,
                        city: addressCity
                    },
                    paperAppPostboxNr: postAddress.postOfficeBoxNumber,
                    paperAppCountryCode: postAddress.countryIsoCode
                },
                electronicApplicationEmail: applyChannel.emailAddress,
                electronicApplicationUrl: applyChannel.formUrl,
                phoneNumber: applyChannel.phoneNumber,
                additionalInfo: applyChannel.additionalInfo
            };
        }
    }

    static mapJobPublicationFormToCreateJobAdvertisement(jobPublicationForm: JobPublicationForm): CreateJobAdvertisement {
        const jobAd: CreateJobAdvertisement = {
            reportToAvam: true
        } as CreateJobAdvertisement;

        jobAd.numberOfJobs = jobPublicationForm.numberOfJobs;

        jobAd.jobDescriptions = jobPublicationForm.jobDescriptions;

        jobAd.occupation = {
            avamOccupationCode: JobPublicationMapper.getAvamOccupationCode(jobPublicationForm),
            workExperience: jobPublicationForm.occupation.experience ? WorkExperience[jobPublicationForm.occupation.experience] : null,
            educationCode: jobPublicationForm.occupation.degree ? JobPublicationMapper.DEGREE[jobPublicationForm.occupation.degree] : null
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
            workForms: JobPublicationMapper.mapWorkFormsToStrings(jobPublicationForm.employment.workForms)
        };

        jobAd.location = {
            remarks: jobPublicationForm.location.additionalDetails,
            city: jobPublicationForm.location.zipCode.city,
            postalCode: jobPublicationForm.location.zipCode.zip,
            countryIsoCode: jobPublicationForm.location.countryCode
        };
        this.mapCompanyToCreateJobAdvertisement(jobPublicationForm.company, jobAd);

        if (jobPublicationForm.employer) {
            jobAd.employer = {
                name: jobPublicationForm.employer.name,
                postalCode: jobPublicationForm.employer.zipCode.zip,
                city: jobPublicationForm.employer.zipCode.city,
                countryIsoCode: jobPublicationForm.employer.countryCode
            };
        }

        jobAd.contact = {
            salutation: <Salutation>Salutation[jobPublicationForm.contact.salutation],
            firstName: jobPublicationForm.contact.firstName,
            lastName: jobPublicationForm.contact.lastName,
            phone: jobPublicationForm.contact.phoneNumber,
            email: jobPublicationForm.contact.email,
            languageIsoCode: jobPublicationForm.contact.language
        };

        jobAd.publicContact = {
            salutation: <Salutation>Salutation[jobPublicationForm.publicContact.salutation],
            firstName: jobPublicationForm.publicContact.firstName,
            lastName: jobPublicationForm.publicContact.lastName,
            phone: jobPublicationForm.publicContact.phoneNumber,
            email: jobPublicationForm.publicContact.email
        };
        if (JobPublicationMapper.anyFieldSet(jobPublicationForm.application)) {
            this.mapApplyChannelToCreateJobAdvertisement(jobPublicationForm.application, jobAd);
        }

        jobAd.publication = {
            startDate: moment().format('YYYY-MM-DD'),
            endDate: null,
            euresDisplay: jobPublicationForm.publication.eures,
            publicDisplay: jobPublicationForm.publication.publicDisplay
        };

        return jobAd;
    }

    private static mapCompanyToCreateJobAdvertisement(companyFormModel: CompanyFormModel, jobAd: CreateJobAdvertisement) {
        if (companyFormModel.postboxNumber) {
            jobAd.company = {
                name: companyFormModel.name,
                postOfficeBoxNumber: companyFormModel.postboxNumber,
                postOfficeBoxCity: companyFormModel.zipCode.city,
                postOfficeBoxPostalCode: companyFormModel.zipCode.zip,
                countryIsoCode: companyFormModel.countryCode,
                surrogate: companyFormModel.surrogate
            }
        } else {
            jobAd.company = {
                name: companyFormModel.name,
                street: companyFormModel.street,
                houseNumber: companyFormModel.houseNumber,
                postalCode: companyFormModel.zipCode.zip,
                city: companyFormModel.zipCode.city,
                countryIsoCode: companyFormModel.countryCode,
                surrogate: companyFormModel.surrogate
            };
        }
    }

    static mapApplyChannelToCreateJobAdvertisement(applyChannel: ApplicationFormModel, jobAd: CreateJobAdvertisement) {
        jobAd.applyChannel = {
            rawPostAddress: null,
            postAddress: JobPublicationMapper.EMPTY_POST_ADDRESS,
            emailAddress: '',
            phoneNumber: '',
            formUrl: '',
            additionalInfo: ''
        }
        if (applyChannel.selectPhoneNumber) {
            jobAd.applyChannel.phoneNumber = applyChannel.phoneNumber;
        }

        if (applyChannel.selectElectronicApplicationEmail) {
            jobAd.applyChannel.emailAddress = applyChannel.electronicApplicationEmail;
        }

        if (applyChannel.selectElectronicApplicationUrl) {
            jobAd.applyChannel.formUrl = JobPublicationMapper.fixUrlScheme(applyChannel.electronicApplicationUrl);
        }

        if (applyChannel.selectPaperApp) {
            this.mapPostAddressToCreateJobAdvertisement(applyChannel.postAddress, jobAd);
        }

        if (applyChannel.additionalInfo) {
            jobAd.applyChannel.additionalInfo = applyChannel.additionalInfo;
        }
    }

    static mapPostAddressToCreateJobAdvertisement(postAddress, jobAd: CreateJobAdvertisement): void {

        jobAd.applyChannel.postAddress.name = postAddress.paperAppCompanyName;
        jobAd.applyChannel.postAddress.countryIsoCode = postAddress.paperAppCountryCode;

        const postalCode = postAddress.paperAppZip.zip;
        const city = postAddress.paperAppZip.city;

        if (postAddress.paperAppPostboxNr) {
            jobAd.applyChannel.postAddress.postOfficeBoxNumber = postAddress.paperAppPostboxNr;
            jobAd.applyChannel.postAddress.postOfficeBoxPostalCode = postalCode;
            jobAd.applyChannel.postAddress.postOfficeBoxCity = city;
        } else {
            jobAd.applyChannel.postAddress.street = postAddress.paperAppStreet;
            jobAd.applyChannel.postAddress.houseNumber = postAddress.paperAppHouseNr;
            jobAd.applyChannel.postAddress.postalCode = postalCode;
            jobAd.applyChannel.postAddress.city = city;
        }
    }

    private static anyFieldSet(obj: any): boolean {
        return Object.keys(obj)
            .some((key) => !!obj[key]);
    }

    private static getAvamOccupationCode(jobPublicationForm: JobPublicationForm): string {
        const jobFormOccupation = jobPublicationForm.occupation.occupationSuggestion;
        return jobFormOccupation ? jobFormOccupation.key.replace('avam:', '') : null;
    }

    private static mapWorkFormsToStrings(workForms: [boolean, boolean, boolean, boolean]): string[] {
        return workForms
            .map((workFormSelected: boolean, index: number) => workFormSelected ? <string>WorkForm[index] : null)
            .filter((workForm) => !!workForm);
    }

    private static mapWorkFormsToBooleans(workForms: string[]): [boolean, boolean, boolean, boolean] {
        return Object.keys(WorkForm)
            .filter((key) => isNaN(parseInt(key, 10)))
            .map((key) => workForms.includes(key)) as [boolean, boolean, boolean, boolean];
    }

    private static fixUrlScheme(url: string): string {
        if (!url) {
            return url
        }
        return url.startsWith('http://') || url.startsWith('https://')
            ? url : `http://${url}`;
    }
}
