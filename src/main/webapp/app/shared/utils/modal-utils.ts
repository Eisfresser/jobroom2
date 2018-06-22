import {
    NgbActiveModal,
    NgbModal,
    NgbModalOptions,
    NgbModalRef
} from '@ng-bootstrap/ng-bootstrap';
import { Injectable } from '@angular/core';

const SMALL_MODAL: NgbModalOptions = { size: 'sm', backdrop: 'static' },
    LARGE_MODAL: NgbModalOptions = { size: 'lg', backdrop: 'static' };

@Injectable()
export class ModalUtils {

    private modal: NgbModalRef;

    constructor(private modalService: NgbModal) {
    }

    openModal(content, options) {
        return this.modal = this.modalService.open(content, options);
    }

    openLargeModal(content) {
        return this.openModal(content, LARGE_MODAL);
    }

    openSmallModal(content) {
        return this.openModal(content, SMALL_MODAL);
    }

    dismissActiveModal(reason?: any) {
        if (this.modal) {
            this.modal.dismiss(reason);
        }
    }
    closeActiveModal(result?: any) {
        if (this.modal) {
            this.modal.close(result);
        }
    }
}
