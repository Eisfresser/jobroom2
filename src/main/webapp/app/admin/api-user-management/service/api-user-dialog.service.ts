import { Injectable } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ApiUserManagementDialogComponent } from '../dialogs/api-user-management-dialog.component';
import { ApiUser, ApiUserUpdatePasswordRequest } from './api-user.service';
import { ApiUserManagementPasswordUpdateDialogComponent } from '../dialogs/api-user-management-password-update-dialog.component';

@Injectable()
export class ApiUserDialogService {

    constructor(private modalService: NgbModal) {
    }

    open(onSubmit: (updatedApiUser: ApiUser) => void, apiUser: ApiUser = null) {
        const modalRef = this.modalService.open(ApiUserManagementDialogComponent, {
            container: 'nav',
            size: 'lg'
        });
        modalRef.componentInstance.apiUser = apiUser;

        const subscription = modalRef.componentInstance.apiUserEmitter.subscribe(onSubmit);
        modalRef.result.then(() => {
            subscription.unsubscribe();
        });
    }

    openPasswordUpdateDialog(onSubmit: (passwordUpdateRequest: ApiUserUpdatePasswordRequest) => void) {
        const modalRef = this.modalService.open(ApiUserManagementPasswordUpdateDialogComponent, {
           container: 'nav',
           size: 'lg'
        });

        const subscription = modalRef.componentInstance.updatePasswordEmitter.subscribe(onSubmit);
        modalRef.result.then(() => {
            subscription.unsubscribe();
        });
    }
}
