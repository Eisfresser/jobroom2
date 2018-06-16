import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DateUtils, PERSON_NUMBER_REGEX } from '../../../shared';
import { JhiAlertService } from 'ng-jhipster';
import { NgbActiveModal, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { RegistrationService } from '../registration.service';

export const BIRTHDAY_MIN_DATE = new Date(1900, 1, 1);
export const BIRTHDAY_MAX_DATE = new Date();
export const BIRTHDAY_START_DATE = new Date(new Date().getFullYear() - 30, 0);

@Component({
    selector: 'jr2-jobseeker-dialog',
    templateUrl: './jobseeker-dialog.component.html',
    styleUrls: ['./jobseeker-dialog.component.scss']
})
export class JobseekerDialogComponent implements OnInit {

    registerForm: FormGroup;
    mismatchInfo = false;
    buttonDisabled = false;
    birthdayDateMin = DateUtils.mapDateToNgbDateStruct(BIRTHDAY_MIN_DATE);
    birthdayDateMax = DateUtils.mapDateToNgbDateStruct(BIRTHDAY_MAX_DATE);
    birthdayDateStart = DateUtils.mapDateToNgbDateStruct(BIRTHDAY_START_DATE);

    constructor(private formBuilder: FormBuilder,
                private alertService: JhiAlertService,
                private activeModal: NgbActiveModal,
                private registrationService: RegistrationService) {
    }

    ngOnInit() {
        this.initFormControls();
    }

    onSubmit() {
        // TODO implement spinner
        this.buttonDisabled = true;
        const birthday: NgbDateStruct = this.registerForm.get('customerBirthday').value;
        this.registrationService.registerJobSeeker(
            {
                personNumber: parseInt(this.registerForm.get('customerPN').value, 10),
                birthdateYear: birthday.year,
                birthdateMonth: birthday.month,
                birthdateDay: birthday.day
            }).subscribe((res: HttpResponse<any>) => {
            this.mismatchInfo = !res.body;
            if (this.mismatchInfo) {
                this.buttonDisabled = false;
            } else {
                this.activeModal.close(true);
            }
        }, (error: HttpErrorResponse) => {
            this.buttonDisabled = false;
        });
    }

    checkButtonDisabled(): boolean {
        return (this.registerForm.invalid || this.buttonDisabled);
    };

    close() {
        this.activeModal.dismiss('cancel');
    }

    private initFormControls() {
        this.registerForm = this.formBuilder.group({
            customerPN: ['', [Validators.required, Validators.pattern(PERSON_NUMBER_REGEX)]],
            customerBirthday: [undefined, Validators.required]
        });
    }

}
