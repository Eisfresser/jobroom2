import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';
import { HttpErrorResponse, HttpParams, HttpResponse } from '@angular/common/http';
import { UserInfoDto } from './user-info.model';
import { UserInfoService } from '../../shared/user-info/user-info.service';

@Component({
    selector: 'jr2-user-info',
    templateUrl: './user-info.component.html',
    styleUrls: [
        'user-info.component.scss'
    ]
})
export class UserInfoComponent {
    public email: FormControl;

    public selectedUserInfo: UserInfoDto;

    public userRoles: Array<String> = [];

    public errorMessageKey: string = null;
    private tmpErrorMsg: string = null;
    private techErrorMsg: string = null;

    constructor(private userInfoService: UserInfoService) {
        this.email = new FormControl();
    }

    public onlyEiamUserInfo(): boolean {
        return this.selectedUserInfo == null && !(this.userRoles == null) && this.userRoles.includes('ALLOW')
    }

    public actionsAvailable(): boolean {
        return !(this.selectedUserInfo == null) && !(this.userRoles == null) && this.userRoles.length > 0
    }

    public searchByEMail() {
        this.unsetErrorMessages()
        this.userInfoService.loadUserInfoByMail(this.email.value)
            .finally(() => {
                this.searchRolesByEMail();
            })
            .subscribe((res: HttpResponse<any>) => {
                this.selectedUserInfo = res.body
            }, (error: HttpErrorResponse) => {
                this.selectedUserInfo = null;
                if (error.error.reason === 'UserInfoNotFoundException') {
                    this.tmpErrorMsg = 'user-info.error.user-not-found';
                } else {
                    this.techErrorMsg = 'user-info.error.technical';
                }
            })

    }

    private searchRolesByEMail() {
        this.userInfoService.loadUserRolesByMail(this.email.value)
            .finally(() => {
                this.setErrorMessages();
            })
            .subscribe((res: HttpResponse<any>) => {
                this.userRoles = res.body
                this.tmpErrorMsg = null;
            }, (error: HttpErrorResponse) => {
                this.userRoles = [];
                if (error.error.reason === 'UserNotFoundException') {
                    if (this.tmpErrorMsg == null) {
                        this.tmpErrorMsg = 'user-info.error.eiam-roles-not-found';
                    }
                } else {
                    this.techErrorMsg = 'registration.customer.identificaton.technical.error';
                }
            })
    }

    private unsetErrorMessages() {
        this.tmpErrorMsg = null;
        this.techErrorMsg = null;
        this.errorMessageKey = null
    }

    private setErrorMessages() {
        if (this.techErrorMsg != null) {
            this.errorMessageKey = this.techErrorMsg;
        } else if (this.tmpErrorMsg != null) {
            this.errorMessageKey = this.tmpErrorMsg;
        }
    }

    public unregister() {
        const email = this.email.value;

        const confirmed = window.confirm(`Are you sure to unregister ${this.selectedUserInfo.email}?`);
        if (!confirmed) {
            return;
        }
        this.userInfoService.unregisterUser(email, this.prepareDeleteParams())
            .subscribe((res: HttpResponse<any>) => {
                this.searchByEMail();
            }, (error: HttpErrorResponse) => {
                this.errorMessageKey = 'user-info.error.delete.technical';
            })
    }

    private prepareDeleteParams() {
        let params = new HttpParams().set('role', 'NO_ROLE');
        if (this.userRoles == null) {
            return params;
        }
        if (this.userRoles.includes('ROLE_JOBSEEKER_CLIENT')) {
            params = params.set('role', 'JOB_SEEKER');
        } else if (this.userRoles.includes('ROLE_COMPANY')) {
            params = params.set('role', 'COMPANY');
        } else if (this.userRoles.includes('ROLE_PRIVATE_EMPLOYMENT_AGENT')) {
            params = params.set('role', 'PRIVATE_AGENT');
        }
        return params;
    }
}
