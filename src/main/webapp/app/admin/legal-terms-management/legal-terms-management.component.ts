import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { ModalUtils } from '../../shared';
import { LegalTerms } from './legal-terms.model';
import { LegalTermsManagementService } from './legal-terms-management.service';
import { LegalTermsDetailsDialogComponent } from './legal-terms-details-dialog/legal-terms-details-dialog.component';
import { LegalTermsDeleteDialogComponent } from './legal-terms-delete-dialog/legal-terms-delete-dialog.component';

@Component({
    selector: 'jr2-legal-terms-management',
    templateUrl: './legal-terms-management.component.html',
    styleUrls: ['./legal-terms-management.component.scss']
})
export class LegalTermsManagementComponent implements OnInit {

    legalTermsEntries$: Observable<LegalTerms[]>;

    constructor(
        private legalTermsManagementService: LegalTermsManagementService,
        private modalUtils: ModalUtils) {
    }

    ngOnInit() {
        this.getAllLegalTermsEntries();
    }

    openViewDialog(legalTerms: LegalTerms) {
        const modal = this.modalUtils.openLargeModal(LegalTermsDetailsDialogComponent);
        (<LegalTermsDetailsDialogComponent>modal.componentInstance).legalTerms = legalTerms;
        (<LegalTermsDetailsDialogComponent>modal.componentInstance).readonly = true;
    }

    addNewLegalTerms() {
        const modal = this.modalUtils.openLargeModal(LegalTermsDetailsDialogComponent);
        modal.result
            .then((legalTerms) => this.add(legalTerms))
    }

    updateLegalTerms(legalTerms: LegalTerms) {
        const modal = this.modalUtils.openLargeModal(LegalTermsDetailsDialogComponent);
        (<LegalTermsDetailsDialogComponent>modal.componentInstance).legalTerms = legalTerms;
        modal.result
            .then((updatedLegalTerms) => this.update(updatedLegalTerms))
    }

    deleteLegalTerms(legalTerms: LegalTerms) {
        const modal = this.modalUtils.openLargeModal(LegalTermsDeleteDialogComponent);
        (<LegalTermsDeleteDialogComponent>modal.componentInstance).effectiveAt = legalTerms.effectiveAt;
        modal.result
            .then(() => this.delete(legalTerms.id))
    }

    add(legalTerms: LegalTerms) {
        return this.legalTermsManagementService.addLegalTermsEntry(legalTerms).subscribe(
            () => {
                console.log('add legal terms entry');
                this.getAllLegalTermsEntries();
            }, (error) => {
                console.log('could not add legal terms entry', error);
            }
        )
    }

    update(legalTerms) {
        return this.legalTermsManagementService.update(legalTerms).subscribe(
            () => {
                console.log('update legal terms entry');
                this.getAllLegalTermsEntries();
            }, (error) => {
                console.log('could not update legal terms entry', error);
            }
        )
    }

    delete(id: String) {
        return this.legalTermsManagementService.delete(id).subscribe(
            () => {
                console.log('delete legal terms entry');
                this.getAllLegalTermsEntries();
            }, (error) => {
                console.log('could not delete legal terms entry', error);
            }
        )
    }

    isFutureEffective(effectiveAt) {
        return effectiveAt != null && Date.parse(effectiveAt) > Date.now();
    }

    private getAllLegalTermsEntries() {
        this.legalTermsEntries$ = this.legalTermsManagementService.getAllLegalTermsEntries();
    }
}
