import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
    ApiUserService,
    ApiUserUpdatePasswordRequest
} from '../service/api-user.service';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
    templateUrl: './api-user-management-password-update-dialog.component.html'
})
export class ApiUserManagementPasswordUpdateDialogComponent implements OnInit {

    @Output() updatePasswordEmitter = new EventEmitter<ApiUserUpdatePasswordRequest>();

    passwordForm: FormGroup;

    constructor(public activeModal: NgbActiveModal,
                private fb: FormBuilder,
                private apiUserService: ApiUserService) {
    }

    ngOnInit(): void {
        this.passwordForm = this.fb.group({
            password: ['', Validators.required]
        })
    }

    generatePassword(): void {
        this.passwordForm.get('password')
            .setValue(this.apiUserService.generatePassword());
    }

    submit(updatePasswordRequest: ApiUserUpdatePasswordRequest): void {
        if (this.passwordForm.valid) {
            this.updatePasswordEmitter.emit(updatePasswordRequest);
            this.activeModal.close();
        }
    }
}
