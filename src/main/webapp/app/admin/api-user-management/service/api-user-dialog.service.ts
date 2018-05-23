import { Injectable } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ApiUserManagementDialogComponent } from '../dialogs/api-user-management-dialog.component';
import { ApiUser } from './api-user.service';

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
}
