import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CancellationData } from './cancellation-data';
import { CancellationReason } from '../../shared/job-advertisement/job-advertisement.model';

@Component({
    selector: 'jr2-job-publication-cancel-dialog',
    templateUrl: './job-publication-cancel-dialog.component.html'
})
export class JobPublicationCancelDialogComponent implements OnInit {

    @Input() id: string;
    @Input() token: string;
    @Output() submitCancellation = new EventEmitter<CancellationData>();

    jobCancelForm: FormGroup;

    private static mapToCancellationReason(formValue): CancellationReason {
        switch (formValue.positionOccupied) {
            case 'jobCenter':
                return CancellationReason.OCCUPIED_JOBCENTER;
            case 'privateAgency':
                return CancellationReason.OCCUPIED_AGENCY;
            case 'jobRoom':
                return CancellationReason.OCCUPIED_JOBROOM;
            case 'notJobRoom':
                return CancellationReason.OCCUPIED_OTHER;
            case 'changeOrRepost':
                return CancellationReason.CHANGE_OR_REPOSE;
            default:
               return  CancellationReason.NOT_OCCUPIED;
        }
    }

    constructor(private fb: FormBuilder,
                public activeModal: NgbActiveModal) {
    }

    ngOnInit(): void {
        this.jobCancelForm = this.fb.group({ positionOccupied: ['', Validators.required] });
    }

    cancelJobPublication(formValue: any) {
        const cancellationData = Object.assign({}, {
            id: this.id,
            token: this.token,
            cancellationReason: JobPublicationCancelDialogComponent.mapToCancellationReason(formValue)
        });
        this.submitCancellation.emit(cancellationData);
        this.activeModal.close();
    }
}
