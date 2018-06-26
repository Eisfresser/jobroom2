export interface PEAJobAdsSearchRequestBody {
    jobTitle: string;
    onlineSinceDays: number;
    companyId: string;
}

export interface PEAJobAdsSearchRequest {
    page: number;
    size: number;
    body: PEAJobAdsSearchRequestBody;
}
