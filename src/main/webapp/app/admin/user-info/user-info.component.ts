import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';
import { HttpClient, HttpParams } from '@angular/common/http';
import { SERVER_API_URL } from '../../app.constants';

@Component({
    selector: 'jr2-user-info',
    templateUrl: './user-info.component.html'
})
export class UserInfoComponent {

    public static BASE_URL = SERVER_API_URL + 'api/user-info/';

    public email: FormControl;

    public selectedUserInfo: UserInfoDto;

    public userRoles: Array<String>;

    constructor(private http: HttpClient) {
        this.email = new FormControl();
    }

    public searchByEMail() {
        const email = this.email.value;

        this.http.get<UserInfoDto>(UserInfoComponent.BASE_URL + email)
            .subscribe((userInfo) => {
                this.selectedUserInfo = userInfo;
            }, () => {
                this.selectedUserInfo = null;
            });

        this.http.get<Array<String>>(UserInfoComponent.BASE_URL + email + '/roles')
            .subscribe((roles) => {
                this.userRoles = roles;
            }, () => {
                this.userRoles = null;
            });
    }

    public unregister() {
        const confirmed = window.confirm(`Are you sure to unregister ${this.selectedUserInfo.email}?`);
        if (!confirmed) {
            return;
        }
        let params = new HttpParams();
        params = params.set('role', 'NO_ROLE');
        if (this.userRoles.includes('ROLE_JOBSEEKER_CLIENT')) {
            params = params.set('role', 'JOB_SEEKER');
        } else if (this.userRoles.includes('ROLE_COMPANY')) {
            params = params.set('role', 'COMPANY');
        } else if (this.userRoles.includes('ROLE_PRIVATE_EMPLOYMENT_AGENT')) {
            params = params.set('role', 'PRIVATE_AGENT');
        }
        this.http.delete(UserInfoComponent.BASE_URL + this.email.value, { params })
            .subscribe(() => {
                this.searchByEMail();
            });
    }
}

class UserInfoDto {
    id: string;
    userExternalId: string;
    firstName: string;
    lastName: string;
    email: string;
    registrationStatus: string;
    accountabilities: Array<{ companyName: string, companyExternalId: string, companySource: string }>;
    createdAt: Date;
    modifiedAt: Date;
    lastLoginAt: Date;
    stesInformation: { personNumber: string, verificationType: string, verifiedAt: Date };

}
