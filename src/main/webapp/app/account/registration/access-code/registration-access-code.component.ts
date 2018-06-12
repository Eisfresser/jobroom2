import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BackgroundUtils } from '../../../shared/utils/background-utils';
import { RegistrationService } from '../registration.service';
import { HttpResponse } from '@angular/common/http';
import { Router } from '@angular/router';

const EMPTY_ACCESS_CODE = '';
const INVALID_ACCESS_CODE = 'invalidAccessCode';
const USER_TYPE_EMPLOYER = 'EMPLOYER';
const USER_TYPE_AGENT = 'AGENT';

@Component({
    selector: 'jr2-access-code',
    templateUrl: './registration-access-code.component.html',
    styleUrls: ['../questionnaire/registration-questionnaire.component.scss']
})
export class RegistrationAccessCodeComponent implements OnInit, OnDestroy {

    form: FormGroup;
    accessCodeInvalid: boolean;

    constructor(private registrationService: RegistrationService,
                private backgroundUtils: BackgroundUtils,
                private fb: FormBuilder,
                private router: Router) {
    }

    ngOnInit() {
        this.backgroundUtils.addBackgroundForJobseekers();
        this.form = this.fb.group({
            accessCode: [EMPTY_ACCESS_CODE, Validators.required]
        });
    }

    ngOnDestroy(): void {
        this.backgroundUtils.removeAllBackgroundClasses();
    }

    registerEmployerOrAgent() {
        this.registrationService.registerEmployerOrAgent(this.form.get('accessCode').value)
            .subscribe((res: HttpResponse<RegistrationResult>) => {
                if (res.body.success) {
                    if (res.body.type === USER_TYPE_EMPLOYER) {
                        this.router.navigate(['/companies/jobpublication']);
                    } else if (res.body.type === USER_TYPE_AGENT) {
                        this.router.navigate(['/agents/candidates']);
                    } else {
                        this.router.navigate(['/home']);
                    }
                } else {
                    this.accessCodeInvalid = true;
                    this.form.get('accessCode').reset(this.form.get('accessCode').value);
                }
            });
    }
}

interface RegistrationResult {
    success: boolean;
    type: string;
}
