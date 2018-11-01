import { Component, OnDestroy, OnInit } from '@angular/core';
import { Company } from './registration-company-data';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RegistrationService } from '../registration.service';
import { Router } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Subject } from 'rxjs';

@Component({
    selector: 'jr2-registration-company-dialog',
    templateUrl: './registration-company-dialog.component.html',
    styleUrls: ['./registration-company-dialog.component.scss']
})
export class RegistrationCompanyDialogComponent implements OnInit, OnDestroy {
    company: Company;
    companyForm: FormGroup;
    isSubmitted = false;
    companyNotFound = false;
    disableSubmit = false;
    displayValidationError = false;

    private unsubscribe$ = new Subject<void>();

    constructor(private registrationService: RegistrationService,
                private router: Router,
                private activeModal: NgbActiveModal,
                private fb: FormBuilder) {
    }

    ngOnInit(): void {
        this.companyForm = this.fb.group({
            uid: ['', Validators.pattern('^CHE\\-[0-9]{3}\\.[0-9]{3}\\.[0-9]{3}$')]
        });

        this.companyForm.get('uid').valueChanges
            .takeUntil(this.unsubscribe$)
            .subscribe(() => {
                this.displayValidationError = false;
            });
    }

    ngOnDestroy(): void {
        this.unsubscribe$.next();
        this.unsubscribe$.complete();
    }

    goToHomePage() {
        this.activeModal.close();
        this.router.navigate(['/home']);
    }

    requestActivationCode() {
        this.disableSubmit = true;
        this.registrationService.requestEmployerAccessCode(this.getCompanyUid())
            .finally(() => this.disableSubmit = false)
            .subscribe(() => this.isSubmitted = true);
    }

    close() {
        this.activeModal.dismiss('cancel');
    }

    findCompanyByUid() {
        if (this.companyForm.invalid) {
            this.displayValidationError = true;
            return;
        }

        this.companyNotFound = false;
        this.registrationService.getCompanyByUid(this.getCompanyUid())
            .subscribe(
                (company) => this.company = company,
                () => {
                    this.companyForm.get('uid').reset(this.companyForm.get('uid').value);
                    this.companyNotFound = true;
                });
    }

    // e.g. CHE-123.456.789 -> 123456789
    private getCompanyUid(): number {
        return parseInt(this.companyForm.get('uid')
            .value
            .replace(new RegExp('CHE\-', 'g'), '')
            .replace(new RegExp('\\.', 'g'), '')
            .replace(new RegExp('\-', 'g'), ''), 10);
    }
}
