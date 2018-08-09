import { Injectable } from '@angular/core';
import { JobseekerDialogComponent } from './jobseeker/jobseeker-dialog.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { RegistrationPavDialogComponent } from './pav/registration-pav-dialog.component';
import { RegistrationCompanyDialogComponent } from './company/registration-company-dialog.component';
import { Router } from '@angular/router';
import { Principal } from '../../shared';

@Injectable()
export class RegistrationDialogService {

    constructor(private modalService: NgbModal,
                private router: Router,
                private principal: Principal) {
    }

    openRegisterJobSeekerDialog() {
        const modalRef = this.modalService.open(JobseekerDialogComponent, {
            size: 'lg',
            backdrop: 'static'
        });

        return modalRef.result.then(() => {
            this.principal.identity(true)
                .then((result) => {
                    this.router.navigate(['/jobseekers']);
                });
        }, () => {});
    }

    openRegisterPavDialog() {
        const modalRef = this.modalService.open(RegistrationPavDialogComponent, {
            size: 'lg',
            backdrop: 'static'
        });
        return modalRef.result.then(() => {
            this.router.navigate(['/home']);
        }, () => {});
    }

    openRegisterCompanyDialog() {
        const modalRef = this.modalService.open(RegistrationCompanyDialogComponent, {
            size: 'lg',
            backdrop: 'static'
        });
        return modalRef.result.then(() => {
            this.router.navigate(['/home']);
        }, () => {});
    }

}
