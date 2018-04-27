import { CEFR_Level } from '../../../../../../main/webapp/app/shared';
import { JobAdvertisement, JobAdvertisementStatus, SourceSystem } from '../../../../../../main/webapp/app/shared/job-advertisement/job-advertisement.model';

export function createJobAdvertisement(id = 'id', stellennummerAvam = 'avam'): JobAdvertisement {
    return {
        id,
        status: JobAdvertisementStatus.CREATED,
        sourceSystem: SourceSystem.JOBROOM,
        externalReference: null,
        stellennummerEgov: 'egov',
        stellennummerAvam,
        fingerprint: null,
        reportToAvam: true,
        jobCenterCode: 'jobcenter',
        jobContent: {
            jobDescriptions: [
                {
                    languageIsoCode: 'de',
                    title: 'title',
                    description: 'description'
                }
            ],
            company: {
                name: 'name',
                street: 'street',
                postalCode: '3333',
                city: 'Bern',
                countryIsoCode: 'CH'
            },
            employment: {
                shortEmployment: false,
                immediately: true,
                permanent: true,
                workloadPercentageMin: 100,
                workloadPercentageMax: 100
            },
            location: {
                city: 'Bern',
                postalCode: '3333',
                countryIsoCode: 'CH'
            },
            occupations: [{
                avamOccupationCode: 'avam'
            }],
            languageSkills: [{
                languageIsoCode: 'de',
                spokenLevel: CEFR_Level.BASIC,
                writtenLevel: CEFR_Level.BASIC
            }],
            applyChannel: {
                mailAddress: '',
                emailAddress: '',
                phoneNumber: '',
                formUrl: '',
                additionalInfo: ''
            }
        },
        publication: {
            startDate: '2018-05-05',
            euresDisplay: true,
            euresAnonymous: true,
            publicDisplay: true,
            publicAnonymous: true,
            restrictedDisplay: true,
            restrictedAnonymous: true
        }
    }
}
