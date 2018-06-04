import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { NgbActiveModal, NgbDateStruct, NgbTimeStruct } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotification } from '../../../home/system-notification/system.notification.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DateUtils } from '../../../shared';
import { Subject } from 'rxjs/Subject';
import * as moment from 'moment';

@Component({
    selector: 'jhi-system-notifications-management-create',
    templateUrl: './system.notifications.management.modal.detail.component.html',
    styleUrls: [
        './system.notifications.management.modal.detail.component.scss'
    ]
})
export class SystemNotificationsManagementModalDetailComponent implements OnInit, OnDestroy {
    @Input() systemNotification: SystemNotification;
    @Output() updateEvent = new EventEmitter<SystemNotification>();

    updateForm: FormGroup;
    startDateMin = DateUtils.mapDateToNgbDateStruct();
    endDateMin = DateUtils.mapDateToNgbDateStruct();

    private unsubscribe$ = new Subject<void>();

    constructor(public activeModal: NgbActiveModal, private fb: FormBuilder) {
    }

    clear() {
        this.activeModal.dismiss('Cancel');
    }

    ngOnInit() {
        const startDate = DateUtils.dateStringToNgbDateTimeStruct(this.systemNotification.startDate);
        const endDate = DateUtils.dateStringToNgbDateTimeStruct(this.systemNotification.endDate);

        this.updateForm = this.fb.group({
            title: [this.systemNotification.title, Validators.required],
            text_de: [this.systemNotification.text_de, Validators.required],
            text_fr: [this.systemNotification.text_fr, Validators.required],
            text_it: [this.systemNotification.text_it, Validators.required],
            text_en: [this.systemNotification.text_en, Validators.required],
            type: [this.systemNotification.type, Validators.required],
            startDate: [startDate, Validators.required],
            startDateTime: [startDate, Validators.required],
            endDate: [endDate, Validators.required],
            endDateTime: [endDate, Validators.required],
            active: [this.systemNotification.active, Validators.required]
        });

        this.configureDatePicker('startDate', 'startDateTime');
        this.configureDatePicker('endDate', 'endDateTime');
        this.configureDatePickerMinDate();
    }

    ngOnDestroy() {
        this.unsubscribe$.next();
        this.unsubscribe$.complete();
    }

    updateSystemNotification(form: FormGroup) {
        const { valid, touched } = form;
        const value = Object.assign({}, form.value, {
            startDate:  this.convertDate(form, 'startDate', 'startDateTime'),
            endDate:  this.convertDate(form, 'endDate', 'endDateTime'),
        });

        delete value.startDateTime;
        delete value.endDateTime;

        if (touched && valid) {
            this.updateEvent.emit({ ...this.systemNotification, ...value });
        }
        this.activeModal.dismiss();
    }

    private configureDatePicker(dateField, timeField) {
        this.updateForm.get(dateField).valueChanges
            .startWith(this.updateForm.get(dateField).value)
            .takeUntil(this.unsubscribe$)
            .subscribe((value) => {
                if (value) {
                    this.updateForm.get(timeField).enable();
                } else {
                    this.updateForm.get(timeField).disable();
                }
            });
    }

    private configureDatePickerMinDate() {
        this.updateForm.get('startDate').valueChanges
            .takeUntil(this.unsubscribe$)
            .subscribe((value) => {
                if (value) {
                    const endDateControl = this.updateForm.get('endDate');
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

    private convertDate(form: FormGroup, date: string, time: string) {
        const startDate: NgbDateStruct = form.get(date).value;
        const startDateTime: NgbTimeStruct = form.get(time).value;
        return moment(new Date(startDate.year, startDate.month - 1, startDate.day, startDateTime.hour, startDateTime.minute))
            .toISOString();
    }
}
