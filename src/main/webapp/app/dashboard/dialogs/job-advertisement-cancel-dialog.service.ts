import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { JobPublicationCancelDialogComponent } from './job-publication-cancel-dialog.component';
import { Injectable } from '@angular/core';
import { CancellationData } from './cancellation-data';

@Injectable()
export class JobAdvertisementCancelDialogService {

    constructor(private modalService: NgbModal) {
    }

    open(id: string, onSubmit: (cancellationData: CancellationData) => void) {
        const modalRef = this.modalService.open(JobPublicationCancelDialogComponent, {
            container: 'nav',
            size: 'lg'
        });
        modalRef.componentInstance.id = id;

        const subscription = modalRef.componentInstance.submitCancellation.subscribe(onSubmit);

        modalRef.result.then(() => {
            subscription.unsubscribe();
        });
    }
}
