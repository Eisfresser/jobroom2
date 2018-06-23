import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { JobPublicationCancelDialogComponent } from './job-publication-cancel-dialog.component';
import { Injectable } from '@angular/core';
import { CancellationData } from './cancellation-data';

@Injectable()
export class JobAdvertisementCancelDialogService {

    constructor(private modalService: NgbModal) {
    }

    open(id: string, onSubmit: (cancellationData: CancellationData) => void, token?: string) {
        const modalRef = this.modalService.open(JobPublicationCancelDialogComponent, {
            container: 'nav',
            size: 'lg'
        });
        modalRef.componentInstance.id = id;
        modalRef.componentInstance.token = token;

        const subscription = modalRef.componentInstance.submitCancellation.subscribe(onSubmit);

        modalRef.result.then(() => {
            subscription.unsubscribe();
        });
    }
}
