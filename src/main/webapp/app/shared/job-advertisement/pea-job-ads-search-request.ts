export interface PEAJobAdsSearchRequestBody {
    jobTitle: string;
    onlineSinceDays: number;
    companyName: string;
}

export interface PEAJobAdsSearchRequest {
    page: number;
    size: number;
    body: PEAJobAdsSearchRequestBody;
}
