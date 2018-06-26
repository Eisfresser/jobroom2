import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EMAIL_REGEX, POSTBOX_NUMBER_REGEX } from '../../shared';
import { EmailContent, MailService } from '../services/mail.service';
import { Subject } from 'rxjs/Subject';

@Component({
    templateUrl: './candidate-anonymous-contact-dialog.component.html',
    styleUrls: ['./candidate-anonymous-contact-dialog.component.scss']
})
export class CandidateAnonymousContactDialogComponent implements OnInit, OnDestroy {
    readonly MESSAGE_BODY_MAX_LENGTH = 10000;

    @Input() emailContent: EmailContent;

    anonymousContactForm: FormGroup;

    private unsubscribe$ = new Subject<void>();

    constructor(private formBuilder: FormBuilder,
                private mailService: MailService,
                public activeModal: NgbActiveModal) {
    }

    ngOnInit(): void {
        const requiredOneCheckboxValidator: ValidatorFn = (fg: FormGroup) => {
            const email = fg.get('sendEmail').value;
            const phone = fg.get('sendPhone').value;
            const address = fg.get('sendAddress').value;
            return email || phone || address ? null : { requiredOneCheckbox: true };
        };

        const requiredDisabledValidator: ValidatorFn = (fg: FormGroup) => {
            const email = !fg.get('sendEmail').value || !!fg.get('email').value;
            const phone = !fg.get('sendPhone').value || !!fg.get('phone').value ;
            return email && phone ? null : { requiredDisabled: true };
        };

        this.anonymousContactForm = this.formBuilder.group({
            subject: [{
                value: this.emailContent.subject,
                disabled: true
            }, Validators.required],
            body: [{
                value: this.emailContent.body,
                disabled: true
            }, Validators.required],
            companyName: [{
                value: this.emailContent.companyName,
                disabled: true
            }, Validators.required],
            sendPhone: !!this.emailContent.phone,
            phone: [{
                value: this.emailContent.phone,
                disabled: true
            }, Validators.required],
            sendEmail: !!this.emailContent.email,
            email: [{
                value: this.emailContent.email,
                disabled: true
            }, [Validators.pattern(EMAIL_REGEX), Validators.required]],
            sendAddress: true,
            company: this.formBuilder.group({
                name: [this.emailContent.company ? this.emailContent.company.name : '', Validators.required],
                contactPerson: this.emailContent.company ? this.emailContent.company.contactPerson : '',
                street: [this.emailContent.company ? this.emailContent.company.street : '', Validators.required],
                houseNumber: [this.emailContent.company ? this.emailContent.company.houseNumber : '', Validators.required],
                zipCode: [this.emailContent.company ? this.emailContent.company.zipCode : null, Validators.pattern(POSTBOX_NUMBER_REGEX)],
                city: [this.emailContent.company ? this.emailContent.company.city : '', Validators.required],
                country: this.emailContent.company ? this.emailContent.company.country : ''
            })
        }, { validator: [requiredOneCheckboxValidator, requiredDisabledValidator] });
        this.anonymousContactForm.get('company').disable();

        this.toggleValue('sendPhone', 'phone');
        this.toggleValue('sendEmail', 'email');
        this.toggleValue('sendAddress', 'company');
    }


    ngOnDestroy(): void {
        this.unsubscribe$.next();
        this.unsubscribe$.complete();
    }

    sendMessage(): void {
        if (this.anonymousContactForm.valid) {
            this.mailService.sendAnonymousContactMessage(this.emailContent)
                .subscribe((success) => {
                    this.activeModal.close()
                });
        }
    }

    edit(controlName: string, toggleControl?: string): void {
        this.anonymousContactForm.get(controlName).enable();
        if (toggleControl) {
            this.anonymousContactForm.get(toggleControl).disable();
        }
    }

    discardChanges(controlName: string, toggleControl?: string): void {
        const control = this.anonymousContactForm.get(controlName);
        switch (controlName) {
            case 'company':
                control.reset({
                    name: this.emailContent.company ? this.emailContent.company.name : '',
                    contactPerson: this.emailContent.company ? this.emailContent.company.contactPerson : '',
                    street: this.emailContent.company ? this.emailContent.company.street : '',
                    houseNumber: this.emailContent.company ? this.emailContent.company.houseNumber : '',
                    zipCode: this.emailContent.company ? this.emailContent.company.zipCode : '',
                    city: this.emailContent.company ? this.emailContent.company.city : '',
                    country: this.emailContent.company ? this.emailContent.company.country : ''
                });
                break;
            default:
                control.setValue(this.emailContent[controlName]);
        }
        control.disable();
        if (toggleControl) {
            this.anonymousContactForm.get(toggleControl).enable()
        }
    }

    applyChanges(controlName: string, toggleControl?: string): void {
        const formControl = this.anonymousContactForm.get(controlName);
        if (formControl.invalid) {
            return;
        }
        this.updateEmailContentProperty(controlName);
        formControl.disable();
        if (toggleControl) {
            this.anonymousContactForm.get(toggleControl).enable()
        }
    }

    private toggleValue(toggleControl: string, source: string) {
        this.anonymousContactForm.get(toggleControl).valueChanges
            .takeUntil(this.unsubscribe$)
            .subscribe((active) => {
                if (active) {
                    this.updateEmailContentProperty(source);
                } else {
                    this.emailContent[source] = null;
                }
            });
    }

    private updateEmailContentProperty(property: string) {
        switch (property) {
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
                this.emailContent[property] = this.anonymousContactForm.get(property).value;
        }
    }
}
