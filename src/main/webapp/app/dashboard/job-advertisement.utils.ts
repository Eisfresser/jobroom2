import { JobAdvertisement, JobDescription } from '../shared/job-advertisement/job-advertisement.model';
import { DEFAULT_LANGUAGE } from '../shared';

export class JobAdvertisementUtils {

    static getJobDescription(jobAdvertisement: JobAdvertisement, lang: string): JobDescription {
        let jobDescription = jobAdvertisement.jobContent.jobDescriptions
            .find((jobDesc) => jobDesc.languageIsoCode === lang);
        if (!jobDescription) {
            jobDescription = jobAdvertisement.jobContent.jobDescriptions
                .find((jobDesc) => jobDesc.languageIsoCode === DEFAULT_LANGUAGE);
        }
        if (!jobDescription) {
            jobDescription = jobAdvertisement.jobContent.jobDescriptions[0];
        }
        return jobDescription;
    }
}
