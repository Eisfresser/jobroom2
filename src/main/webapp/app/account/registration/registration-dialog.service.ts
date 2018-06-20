import { Injectable } from '@angular/core';
import { JobseekerDialogComponent } from './jobseeker/jobseeker-dialog.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { RegistrationPavDialogComponent } from './pav/registration-pav-dialog.component';
import { ExistingPavDialogComponent } from './existing-pav/existing-pav-dialog.component';
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
            size: 'lg'
        });

        modalRef.result.then(() => {
            this.principal.identity(true).then((result) => {
                this.router.navigate(['/jobseekers']);
            });
        }, (error) => {
            // cancel dialog
        });
    }

    openRegisterPavDialog() {
        const modalRef = this.modalService.open(RegistrationPavDialogComponent, {
            size: 'lg'
        });

        modalRef.result.then(() => {
            this.router.navigate(['/home']);
        }, (error) => {
            // cancel dialog
        });
    }

    openExistingPavDialog() {
        const modalRef = this.modalService.open(ExistingPavDialogComponent, {
            size: 'lg'
        });

        modalRef.result.then(() => {
            this.principal.identity(true).then((result) => {
                this.router.navigate(['/agents', 'candidates'])
            });
        }, (error) => {
            // cancel dialog
        });
    }

}
