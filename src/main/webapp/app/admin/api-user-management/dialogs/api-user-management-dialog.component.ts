import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EMAIL_REGEX } from '../../../shared';
import { ApiUser, ApiUserService } from '../service/api-user.service';

@Component({
    selector: 'jr2-api-user-management-dialog',
    templateUrl: './api-user-management-dialog.component.html'
})
export class ApiUserManagementDialogComponent implements OnInit {

    @Output() apiUserEmitter = new EventEmitter<ApiUser>();
    @Input() apiUser: ApiUser;

    apiUserForm: FormGroup;

    constructor(private fb: FormBuilder,
                public activeModal: NgbActiveModal,
                private apiUserService: ApiUserService) {
    }

    ngOnInit(): void {
        this.apiUserForm = this.fb.group({
            username: [this.apiUser ? this.apiUser.username : '', Validators.required],
            companyEmail: [this.apiUser ? this.apiUser.companyEmail : '', [Validators.required, Validators.pattern(EMAIL_REGEX)]],
            companyName: [this.apiUser ? this.apiUser.companyName : '', Validators.required],
            technicalContactName: [this.apiUser ? this.apiUser.technicalContactName : '', Validators.required],
            technicalContactEmail: [this.apiUser ? this.apiUser.technicalContactEmail : '', [Validators.required, Validators.pattern(EMAIL_REGEX)]],
        });
        if (!this.apiUser) {
            this.apiUserForm.addControl('password', this.fb.control('', Validators.required));
            this.apiUserForm.addControl('active', this.fb.control(true));
        }
    }

    generatePassword(): void {
        this.apiUserForm.get('password')
            .setValue(this.apiUserService.generatePassword());
    }

    submit(formValue: ApiUser): void {
        if (this.apiUserForm.valid) {
            this.apiUserEmitter.emit(formValue);
            this.activeModal.close();
        }
    }
}
