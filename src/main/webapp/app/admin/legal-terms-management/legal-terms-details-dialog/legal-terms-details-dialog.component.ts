import { Component, OnInit } from '@angular/core';
import { DateUtils, ModalUtils, URL_REGEX } from '../../../shared';
import { LegalTerms } from '../legal-terms.model';
import {
    AbstractControl,
    FormBuilder,
    FormGroup,
    ValidatorFn,
    Validators
} from '@angular/forms';
import { now } from 'moment';
import * as moment from 'moment';

@Component({
    selector: 'jr2-add-legal-terms-dialog',
    templateUrl: './legal-terms-details-dialog.component.html',
    styles: []
})
export class LegalTermsDetailsDialogComponent implements OnInit {

    legalTerms: LegalTerms;

    legalTermsForm: FormGroup;

    readonly: boolean;

    minEffectiveAt = DateUtils.mapDateToNgbDateStruct(moment(moment().add(1, 'days')).toDate());

    constructor(
        private modalUtils: ModalUtils,
        private fb: FormBuilder) {
    }

    ngOnInit() {
        const formValue = this.legalTerms != null ? this.legalTerms : this.emptyLegalTerms();
        this.legalTermsForm = this.fb.group(
            {
                effectiveAt: [DateUtils.dateStringToNgbDateTimeStruct(formValue.effectiveAt), [Validators.required, this.pastEffectiveAtValidator()]],
                linkDe: [formValue.linkDe, [Validators.required, Validators.pattern(URL_REGEX)]],
                linkEn: [formValue.linkEn, [Validators.required, Validators.pattern(URL_REGEX)]],
                linkFr: [formValue.linkFr, [Validators.required, Validators.pattern(URL_REGEX)]],
                linkIt: [formValue.linkIt, [Validators.required, Validators.pattern(URL_REGEX)]]
            });
    }

    onClose() {
        this.modalUtils.dismissActiveModal();
    }

    onSubmit() {
        if (this.legalTermsForm.valid) {
            this.modalUtils.closeActiveModal(this.mapFormToLegalTerms(this.legalTermsForm.value));
        }
    }

    private mapFormToLegalTerms(formValue) {
        const id = this.legalTerms != null ? this.legalTerms.id : null;
        const effectiveAt = DateUtils.convertNgbDateStructToString(formValue.effectiveAt);
        return { id, ...formValue, effectiveAt } as LegalTerms;
    }

    private emptyLegalTerms() {
        return {
            id: null,
            effectiveAt: null,
            linkDe: '',
            linkEn: '',
            linkFr: '',
            linkIt: ''
        } as LegalTerms;
    }

    private pastEffectiveAtValidator(): ValidatorFn {
        return (control: AbstractControl) => {
            const effectiveAt = control.value;
            return effectiveAt != null && DateUtils.mapNgbDateStructToDate(effectiveAt).getTime() > now()
                ? null
                : { pastEffectiveAt: true }
        };
    }
}
