import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EMAIL_REGEX, HOUSE_NUMBER_REGEX, Principal } from '../../shared';
import { CurrentSelectedCompanyService } from '../../shared/company/current-selected-company.service';
import { CompanyContactTemplateModel } from '../../shared/company/company-contact-template.model';
import { UserInfoService } from '../../shared/user-info/user-info.service';
import {
    Accountability,
    CompanyContactTemplate
} from '../../shared/user-info/user-info.model';
import { Salutation } from '../../shared/job-advertisement/job-advertisement.model';
import { Observable } from 'rxjs/Observable';

@Component({
    selector: 'jr2-contact-template-management',
    templateUrl: './contact-template-management.component.html'
})
export class ContactTemplateManagementComponent implements OnInit {

    contactTemplateForm: FormGroup;

    selectedCompany$: Observable<Accountability>;

    salutations = Salutation;

    showSuccessSaveMessage: boolean;

    showFailureSaveMessage: boolean;

    private _editing;

    private selectedTemplate: CompanyContactTemplateModel;

    constructor(private fb: FormBuilder,
                private principal: Principal,
                private currentSelectedCompanyService: CurrentSelectedCompanyService,
                private userInfoService: UserInfoService) {
    }

    ngOnInit(): void {
        this.selectedCompany$ = this.currentSelectedCompanyService.getSelectedAccountability();

        this.contactTemplateForm = this.prepareForm();

        this.currentSelectedCompanyService.getSelectedCompanyContactTemplate()
            .subscribe((selectedTemplate: CompanyContactTemplateModel) => {
                this.selectedTemplate = selectedTemplate;
                this.editing = false;
                this.patchFormWithSelectedTemplateValues(selectedTemplate);
            });

        this.editing = false;
    }

    toggleEdit(): void {
        this.editing = !this.editing;
    }

    onSubmit() {
        this.showSuccessSaveMessage = false;
        this.principal.getAuthenticationState()
            .flatMap((currentUser) => this.userInfoService.createCompanyContactTemplate(currentUser.id, this.preparePayload()))
            .flatMap(() => this.currentSelectedCompanyService.reloadCurrentSelection())
            .finally(() => {
                window.scroll(0, 0);
            })
            .subscribe(() => {
                this.showSuccessSaveMessage = true;
                this.editing = false;
            }, () => {
                this.showFailureSaveMessage = true;
            });
    }

    private prepareForm(): FormGroup {
        const contactTemplateForm = this.fb.group({
            salutation: [null, Validators.required],
            firstName: [null],
            lastName: [null],
            phone: [null, Validators.required],
            email: [null, [Validators.required, Validators.pattern(EMAIL_REGEX)]],
            companyName: [null, Validators.required],
            companyStreet: [null, Validators.required],
            companyHouseNr: [null, Validators.pattern(HOUSE_NUMBER_REGEX)],
            companyZipCode: [null, Validators.required],
            companyCity: [null, Validators.required]
        });
        contactTemplateForm.get('phone').disable();
        return contactTemplateForm
    }

    private patchFormWithSelectedTemplateValues(selectedTemplate: CompanyContactTemplateModel): void {
        if (selectedTemplate == null) {
            return;
        }
        this.contactTemplateForm.patchValue({
            salutation: selectedTemplate.salutation,
            firstName: selectedTemplate.firstName,
            lastName: selectedTemplate.lastName,
            phone: selectedTemplate.phone,
            email: selectedTemplate.email,
            companyName: selectedTemplate.companyName,
            companyStreet: selectedTemplate.companyStreet,
            companyHouseNr: selectedTemplate.companyHouseNr,
            companyZipCode: selectedTemplate.companyZipCode,
            companyCity: selectedTemplate.companyCity,
        });
    }

    private preparePayload(): CompanyContactTemplate {
        return {
            salutation: this.contactTemplateForm.value.salutation,
            phone: this.contactTemplateForm.value.phone,
            email: this.contactTemplateForm.value.email,
            companyId: this.selectedTemplate.companyId,
            companyName: this.contactTemplateForm.value.companyName,
            companyStreet: this.contactTemplateForm.value.companyStreet,
            companyHouseNr: this.contactTemplateForm.value.companyHouseNr,
            companyZipCode: this.contactTemplateForm.value.companyZipCode,
            companyCity: this.contactTemplateForm.value.companyCity,
        };
    }

    get editing(): boolean {
        return this._editing;
    }

    set editing(value: boolean) {
        this._editing = value;
        if (this._editing) {
            this.contactTemplateForm.enable();
        } else {
            this.contactTemplateForm.disable();
            this.patchFormWithSelectedTemplateValues(this.selectedTemplate);
        }
        this.contactTemplateForm.get('firstName').disable();
        this.contactTemplateForm.get('lastName').disable();
    }

}
