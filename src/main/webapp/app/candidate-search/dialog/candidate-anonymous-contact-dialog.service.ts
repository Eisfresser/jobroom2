import { Injectable } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CandidateAnonymousContactDialogComponent, } from './candidate-anonymous-contact-dialog.component';
import { EmailContent } from '../services/mail.service';

@Injectable()
export class CandidateAnonymousContactDialogService {

    constructor(private modalService: NgbModal) {
    }

    open(emailContent: EmailContent) {
        const modalRef = this.modalService
            .open(CandidateAnonymousContactDialogComponent, { size: 'lg' });
        modalRef.componentInstance.emailContent = emailContent;
    }
}
