import { Component, OnInit } from '@angular/core';
import { Company, initialCompany } from './registration-company-data';
import { FormBuilder, FormGroup } from '@angular/forms';
import { RegistrationService } from '../registration.service';
import { ModalUtils } from '../../../shared';
import { Router } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jr2-registration-company-dialog',
    templateUrl: './registration-company-dialog.component.html',
    styleUrls: ['./registration-company-dialog.component.scss']
})
export class RegistrationCompanyDialogComponent implements OnInit {
    company: Company;
    companyForm: FormGroup;
    isSubmitted = false;
    companyNotFound = false;
    disableSubmit = false;

    constructor(private registrationService: RegistrationService,
                private router: Router,
                private activeModal: NgbActiveModal,
                private fb: FormBuilder) {
    }

    ngOnInit(): void {
        this.companyForm = this.fb.group(initialCompany);
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
