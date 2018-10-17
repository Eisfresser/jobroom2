export enum BlacklistedAgentStatus {
    ACTIVE = 'ACTIVE',
    INACTIVE = 'INACTIVE'
}

export interface BlacklistedAgent {
    id: string;
    externalId: string;
    status: BlacklistedAgentStatus;
    name: string;
    street: string;
    zipCode: string;
    city: string;
    createdBy: string;
    blacklistedAt: Date;
    blacklistingCounter: number;
}
