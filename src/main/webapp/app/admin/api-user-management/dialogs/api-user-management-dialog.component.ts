import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EMAIL_REGEX } from '../../../shared';
import { ApiUser } from '../service/api-user.service';

@Component({
    selector: 'jr2-api-user-management-dialog',
    templateUrl: './api-user-management-dialog.component.html'
})
export class ApiUserManagementDialogComponent implements OnInit {

    @Output() apiUserEmitter = new EventEmitter<ApiUser>();
    @Input() apiUser: ApiUser;

    apiUserForm: FormGroup;

    constructor(private fb: FormBuilder,
                public activeModal: NgbActiveModal) {
    }

    ngOnInit(): void {
        this.apiUserForm = this.fb.group({
            username: [this.apiUser ? this.apiUser.username : '', Validators.required],
            companyEmail: [this.apiUser ? this.apiUser.companyEmail : '', [Validators.required, Validators.pattern(EMAIL_REGEX)]],
            companyName: [this.apiUser ? this.apiUser.companyName : '', Validators.required],
            technicalContactName: [this.apiUser ? this.apiUser.technicalContactName : '', Validators.required],
            technicalContactEmail: [this.apiUser ? this.apiUser.technicalContactEmail : '', [Validators.required, Validators.pattern(EMAIL_REGEX)]],
            active: [this.apiUser ? this.apiUser.active : true],
            password: [this.apiUser ? this.apiUser.password : '', Validators.required]
        });
    }

    generatePassword(): void {
        this.apiUserForm.get('password')
            .setValue(Math.random().toString(36).slice(-10));
    }

    submit(formValue: ApiUser): void {
        if (this.apiUserForm.valid) {
            this.apiUserEmitter.emit(formValue);
            this.activeModal.close();
        }
    }
}
