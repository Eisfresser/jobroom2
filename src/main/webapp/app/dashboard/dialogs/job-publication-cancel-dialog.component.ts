import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder, FormGroup, ValidatorFn } from '@angular/forms';
import { Subject } from 'rxjs/Subject';
import { CancellationData } from './cancellation-data';
import { CancellationReason } from '../../shared/job-advertisement/job-advertisement.model';

const jobCancelFormValidator: ValidatorFn = (jobCancelForm: FormGroup) => {
    const positionOccupiedValue = jobCancelForm.get('positionOccupied').value;
    const jobCenterValue = jobCancelForm.get('occupiedWith.jobCenter').value;
    const privateAgencyValue = jobCancelForm.get('occupiedWith.privateAgency').value;
    const jobRoomValue = jobCancelForm.get('selfOccupiedWith.jobRoom').value;
    const notJobRoomValue = jobCancelForm.get('selfOccupiedWith.notJobRoom').value;

    if (!positionOccupiedValue
        || (positionOccupiedValue === 'occupied' && (!jobCenterValue && !privateAgencyValue))
        || (positionOccupiedValue === 'self' && (!jobRoomValue && !notJobRoomValue))) {
        return {
            occupiedWith: 'required'
        };
    } else {
        return null;
    }
};

@Component({
    selector: 'jr2-job-publication-cancel-dialog',
    templateUrl: './job-publication-cancel-dialog.component.html'
})
export class JobPublicationCancelDialogComponent implements OnInit, OnDestroy {

    @Input() id: string;
    @Input() token: string;
    @Output() submitCancellation = new EventEmitter<CancellationData>();

    jobCancelForm: FormGroup;
    private unsubscribe$ = new Subject<void>();

    private static mapToCancellationReason(formValue): CancellationReason {
        if (formValue.positionOccupied === 'occupied') {
            if (formValue.occupiedWith.jobCenter) {
                return CancellationReason.OCCUPIED_JOBCENTER;
            }
            if (formValue.occupiedWith.privateAgency) {
                return CancellationReason.OCCUPIED_AGENCY;
            }
        }

        if (formValue.positionOccupied === 'self') {
            if (formValue.selfOccupiedWith.jobRoom) {
                return CancellationReason.OCCUPIED_JOBROOM;
            }
            if (formValue.selfOccupiedWith.notJobRoom) {
                return CancellationReason.OCCUPIED_OTHER;
            }
        }

        if (formValue.positionOccupied === 'changeOrRepost') {
            return CancellationReason.CHANGE_OR_REPOSE;
        }

        return CancellationReason.NOT_OCCUPIED;
    }

    constructor(private fb: FormBuilder,
                public activeModal: NgbActiveModal) {
    }

    ngOnInit(): void {
        this.jobCancelForm = this.fb.group({
            positionOccupied: 'self',
            occupiedWith: this.fb.group({
                jobCenter: [false],
                privateAgency: [false]
            }),
            selfOccupiedWith: this.fb.group({
                jobRoom: [false],
                notJobRoom: [false]
            })
        }, {
            validator: jobCancelFormValidator
        });

        this.jobCancelForm.get('positionOccupied').valueChanges
            .takeUntil(this.unsubscribe$)
            .startWith(this.jobCancelForm.get('positionOccupied').value)
            .subscribe((value) => {
                if (value === 'occupied') {
                    this.jobCancelForm.get('occupiedWith').enable({ emitEvent: false });
                } else {
                    this.jobCancelForm.get('occupiedWith').reset();
                    this.jobCancelForm.get('occupiedWith').disable({ emitEvent: false });
                }

                if (value === 'self') {
                    this.jobCancelForm.get('selfOccupiedWith').enable({ emitEvent: false });
                } else {
                    this.jobCancelForm.get('selfOccupiedWith').reset();
                    this.jobCancelForm.get('selfOccupiedWith').disable({ emitEvent: false });
                }
            });

        this.configureCheckboxGroup('occupiedWith', 'privateAgency', 'jobCenter');
        this.configureCheckboxGroup('selfOccupiedWith', 'jobRoom', 'notJobRoom');
    }

    private configureCheckboxGroup(groupName, checkboxName1, checkboxName2) {
        this.jobCancelForm.get(groupName).valueChanges
            .takeUntil(this.unsubscribe$)
            .subscribe((value) => {
                if (value[checkboxName1]) {
                    this.jobCancelForm.get(groupName).get(checkboxName2).disable({ emitEvent: false });
                } else if (value[checkboxName2]) {
                    this.jobCancelForm.get(groupName).get(checkboxName1).disable({ emitEvent: false });
                } else {
                    this.jobCancelForm.get(groupName).enable({ emitEvent: false });
                }
            });
    }

    ngOnDestroy(): void {
        this.unsubscribe$.next();
        this.unsubscribe$.complete();
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
