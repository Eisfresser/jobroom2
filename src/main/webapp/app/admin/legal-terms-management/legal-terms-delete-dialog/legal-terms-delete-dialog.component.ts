import { Component } from '@angular/core';
import { ModalUtils } from '../../../shared';

@Component({
    selector: 'jr2-delete-legal-terms-dialog',
    templateUrl: './legal-terms-delete-dialog.component.html',
    styles: []
})
export class LegalTermsDeleteDialogComponent {

    effectiveAt: string;

    constructor(public modalUtils: ModalUtils) {
    }

    delete() {
        this.modalUtils.closeActiveModal();
    }

    close() {
        this.modalUtils.dismissActiveModal();
    }
}
