import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { NgbActiveModal, NgbDateStruct, NgbTimeStruct } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotification } from '../../../home/system-notification/system.notification.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs/Subject';
import { DateUtils } from '../../../shared';

@Component({
    selector: 'jhi-system-notifications-management-create',
    templateUrl: './system.notifications.management.modal.create.component.html',
    styleUrls: [
        './system.notifications.management.modal.create.component.scss'
    ]
})
export class SystemNotificationsManagementModalCreateComponent implements OnInit, OnDestroy {
    @Output() createEvent = new EventEmitter<SystemNotification>();

    createForm: FormGroup;
    startDateMin = DateUtils.mapDateToNgbDateStruct();
    endDateMin = DateUtils.mapDateToNgbDateStruct();

    private unsubscribe$ = new Subject<void>();

    constructor(public activeModal: NgbActiveModal, private fb: FormBuilder) {
    }

    ngOnInit() {
        this.createForm = this.fb.group({
            title: ['', Validators.required],
            text_de: ['', Validators.required],
            text_fr: ['', Validators.required],
            text_it: ['', Validators.required],
            text_en: ['', Validators.required],
            type: ['', Validators.required],
            startDate: ['', Validators.required],
            startDateTime: [{
                value: '',
                disabled: true
            }, Validators.required],
            endDate: ['', Validators.required],
            endDateTime: [{
                value: '',
                disabled: true
            }, Validators.required],
            active: [false, Validators.required]
        });

        this.configureDatePicker('startDate', 'startDateTime');
        this.configureDatePicker('endDate', 'endDateTime');
        this.configureDatePickerMinDate();
    }

    private configureDatePicker(dateField, timeField) {
        this.createForm.get(dateField).valueChanges
            .takeUntil(this.unsubscribe$)
            .subscribe((value) => {
                if (value) {
                    this.createForm.get(timeField).enable();
                } else {
                    this.createForm.get(timeField).disable();
                }
            });
    }

    private configureDatePickerMinDate() {
        this.createForm.get('startDate').valueChanges
            .takeUntil(this.unsubscribe$)
            .subscribe((value) => {
                if (value) {
                    const endDateControl = this.createForm.get('endDate');
                    const startDate = DateUtils.mapNgbDateStructToDate(value);
                    const endDate = DateUtils.mapNgbDateStructToDate(
                        endDateControl.value ? endDateControl.value : this.endDateMin);

                    if (startDate > endDate) {
                        endDateControl.setValue(null);
                    }
                    this.endDateMin = DateUtils.mapDateToNgbDateStruct(startDate);
                }
            });
    }

    ngOnDestroy() {
        this.unsubscribe$.next();
        this.unsubscribe$.complete();
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    createSystemNotification(form: FormGroup) {
        const value = Object.assign({}, form.value, {
            startDate:  this.convertDate(form, 'startDate', 'startDateTime'),
            endDate:  this.convertDate(form, 'endDate', 'endDateTime'),
        });

        delete value.startDateTime;
        delete value.endDateTime;

        this.createEvent.emit(value);
        this.activeModal.dismiss();
    }

    private convertDate(form: FormGroup, date: string, time: string) {
        const startDate: NgbDateStruct = form.get(date).value;
        const startDateTime: NgbTimeStruct = form.get(time).value;
        return new Date(Date.UTC(startDate.year, startDate.month - 1, startDate.day, startDateTime.hour, startDateTime.minute)).toISOString();
    }
}
