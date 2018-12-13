import { BackgroundUtils } from '../../../shared';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { RegistrationDialogService } from '../registration-dialog.service';
import { LegalTermsService } from '../legal-terms/legal-terms.service';
import { Observable } from 'rxjs/Observable';

@Component({
    selector: 'jr2-registration-questionnaire',
    templateUrl: './registration-questionnaire.component.html',
    styleUrls: ['./registration-questionnaire.component.scss']
})
export class RegistrationQuestionnaireComponent implements OnInit, OnDestroy {
    public roleForm: FormGroup;
    public legalTermsUrl$: Observable<string>;

    constructor(private fb: FormBuilder,
                private backgroundUtils: BackgroundUtils,
                private registrationDialogService: RegistrationDialogService,
                private legalTermsService: LegalTermsService) {
        this.legalTermsUrl$ = this.legalTermsService.getCurrentLegalTermsUrl()
            .catch((error) => Observable.of('error'));
    }

    ngOnInit() {
        this.backgroundUtils.addBackgroundForJobseekers();
        this.roleForm = this.fb.group({
            selectedRole: [null],
            termsAccepted: [false]
        });
        this.roleForm.get('selectedRole').valueChanges
            .subscribe(() => {
                this.onSelectedRoleChanged();
            })
    }

    ngOnDestroy(): void {
        this.backgroundUtils.removeAllBackgroundClasses();
    }

    hasRoleSelected(): boolean {
        return this.roleForm.get('selectedRole').value;
    }

    isTermsAndConditionsAccepted(): boolean {
        return this.roleForm.get('termsAccepted').value === true;
    }

    cancel() {
        this.roleForm.reset();
    }

    next() {
        if (!this.roleForm.valid) {
            return;
        }
        switch (this.roleForm.get('selectedRole').value) {
            case 'employer':
                this.registrationDialogService.openRegisterCompanyDialog();
                break;
            case 'agency':
                this.registrationDialogService.openRegisterPavDialog();
                break;
            case 'jobseeker':
                this.registrationDialogService.openRegisterJobSeekerDialog();
                break;
            default:
                return null;
        }
    }

    private onSelectedRoleChanged(): void {
        this.roleForm.patchValue({ termsAccepted: false });
    }
}
