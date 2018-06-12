import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { JhiAlertService } from 'ng-jhipster';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { RegistrationService } from '../registration.service';

@Component({
    selector: 'jr2-existing-pav-dialog',
    templateUrl: './existing-pav-dialog.component.html',
    styleUrls: ['./existing-pav-dialog.component.scss']
})
export class ExistingPavDialogComponent implements OnInit {

    loginForm: FormGroup;
    mismatchInfo = false;
    buttonDisabled = false;

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
        this.mismatchInfo = false;
        this.registrationService.registerExistingAgent(
            {
                username: this.loginForm.get('username').value,
                password: this.loginForm.get('password').value
            }).subscribe((res: HttpResponse<boolean>) => {
            // Successfully logged in
            if (res.body) {
                this.activeModal.close(true);
            } else {  // wrong credentials
                this.buttonDisabled = false;
                this.mismatchInfo = true;
            }
        }, (error: HttpErrorResponse) => {
            this.buttonDisabled = false;
            this.mismatchInfo = true;
        });
    }

    checkButtonDisabled(): boolean {
        return (this.loginForm.invalid || this.buttonDisabled);
    };

    close() {
        this.activeModal.dismiss('cancel');
    }

    private initFormControls() {
        this.loginForm = this.formBuilder.group({
            username: ['', Validators.required],
            password: ['', Validators.required]
        });
    }

}
