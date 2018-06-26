export const PEA_JOB_AD_DEFAULT_SORT = 'jobAdvertisement.stellennummerEgov,desc';

export interface PEAJobAdsSearchRequestBody {
    jobTitle: string;
    onlineSinceDays: number;
    companyId: string;
}

export interface PEAJobAdsSearchRequest {
    page: number;
    size: number;
    sort: string;
    body: PEAJobAdsSearchRequestBody;
}
