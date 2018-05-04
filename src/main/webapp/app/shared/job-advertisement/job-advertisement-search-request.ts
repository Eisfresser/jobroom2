export interface ProfessionCode {
    type: string;
    value: string
}

export interface JobAdvertisementSearchRequestBody {
    language?: string;
    professionCodes?: ProfessionCode[];
    keywords?: string[];
    communalCodes?: string[];
    regionCodes?: string[];
    cantonCodes?: string[];
    workloadPercentageMin?: number;
    workloadPercentageMax?: number;
    permanent?: boolean;
    companyName?: string;
    onlineSince: number;
    displayRestricted: boolean;
}

export interface JobAdvertisementSearchRequest {
    page: number;
    size: number;
    sort?: string;
    body: JobAdvertisementSearchRequestBody;
}
