import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EMAIL_REGEX, PHONE_NUMBER_REGEX, POSTBOX_NUMBER_REGEX } from '../../shared';
import { EmailContent, MailService } from '../services/mail.service';

@Component({
    templateUrl: './candidate-anonymous-contact-dialog.component.html',
    styleUrls: ['./candidate-anonymous-contact-dialog.component.scss']
})
export class CandidateAnonymousContactDialogComponent implements OnInit {
    readonly MESSAGE_BODY_MAX_LENGTH = 10000;

    @Input() emailContent: EmailContent;

    anonymousContactForm: FormGroup;

    constructor(private formBuilder: FormBuilder,
                private mailService: MailService,
                public activeModal: NgbActiveModal) {
    }

    ngOnInit(): void {
        this.anonymousContactForm = this.formBuilder.group({
            subject: [{
                value: this.emailContent.subject,
                disabled: true
            }, Validators.required],
            body: [{
                value: this.emailContent.body,
                disabled: true
            }, Validators.required],
            sendPhone: true,
            phone: [{
                value: this.emailContent.phone,
                disabled: true
            }, Validators.pattern(PHONE_NUMBER_REGEX)],
            sendEmail: true,
            email: [{
                value: this.emailContent.email,
                disabled: true
            }, Validators.pattern(EMAIL_REGEX)],
            sendAddress: true,
            company: this.formBuilder.group(this.buildCompanyFormGroup())
        });
        this.anonymousContactForm.get('company').disable();
    }

    private buildCompanyFormGroup() {
        return {
            name: this.emailContent.company ? this.emailContent.company.name : '',
            contactPerson: this.emailContent.company ? this.emailContent.company.contactPerson : '',
            street: this.emailContent.company ? this.emailContent.company.street : '',
            houseNumber: this.emailContent.company ? this.emailContent.company.houseNumber : '',
            zipCode: [this.emailContent.company ? this.emailContent.company.zipCode : '', Validators.pattern(POSTBOX_NUMBER_REGEX)],
            city: this.emailContent.company ? this.emailContent.company.city : '',
            country: this.emailContent.company ? this.emailContent.company.country : ''
        }
    }

    sendMessage(): void {
        if (this.anonymousContactForm.valid) {
            this.mailService.senAnonymousContactMessage(this.emailContent);
            this.activeModal.close();
        }
    }

    edit(controlName: string): void {
        this.anonymousContactForm.get(controlName).enable();
    }

    discardChanges(controlName: string): void {
        const control = this.anonymousContactForm.get(controlName);
        switch (controlName) {
            case 'company':
                control.reset(this.buildCompanyFormGroup());
                break;
            default:
                control.setValue(this.emailContent[controlName]);
        }
        control.disable();
    }

    applyChanges(controlName: string): void {
        const formControl = this.anonymousContactForm.get(controlName);
        if (formControl.invalid) {
            return;
        }

        switch (controlName) {
            case 'company':
                const company = Object.assign({}, {
                    name: this.anonymousContactForm.get('company.name').value,
                    contactPerson: this.anonymousContactForm.get('company.contactPerson').value,
                    street: this.anonymousContactForm.get('company.street').value,
                    houseNumber: this.anonymousContactForm.get('company.houseNumber').value,
                    zipCode: this.anonymousContactForm.get('company.zipCode').value,
                    city: this.anonymousContactForm.get('company.city').value,
                    country: this.anonymousContactForm.get('company.country').value,
                });
                this.emailContent.company = company;
                break;
            default:
                this.emailContent[controlName] = formControl.value;
        }
        formControl.disable();
    }
}
