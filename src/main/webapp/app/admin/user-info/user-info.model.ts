export class UserInfoDto {
    id: string;
    userExternalId: string;
    firstName: string;
    lastName: string;
    email: string;
    registrationStatus: string;
    accountabilities: Array<{ companyName: string, companyExternalId: string, companySource: string }> = [];
    createdAt: Date;
    modifiedAt: Date;
    lastLoginAt: Date;
    stesInformation: { personNumber: string, verificationType: string, verifiedAt: Date } = null;
}
