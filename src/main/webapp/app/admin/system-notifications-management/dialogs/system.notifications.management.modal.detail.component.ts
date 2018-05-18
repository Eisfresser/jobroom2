import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotification } from '../../../home/system-notification/system.notification.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
    selector: 'jhi-system-notifications-management-create',
    templateUrl: './system.notifications.management.modal.detail.component.html'
})
export class SystemNotificationsManagementModalDetailComponent
    implements OnInit {
    @Input() systemNotification: SystemNotification;
    @Output() updateEvent = new EventEmitter<SystemNotification>();

    updateForm: FormGroup;

    constructor(public activeModal: NgbActiveModal, private fb: FormBuilder) {
    }

    clear() {
        this.activeModal.dismiss('Cancel');
    }

    updateSystemNotification(form: FormGroup) {
        const { value, valid, touched } = form;
        if (touched && valid) {
            this.updateEvent.emit({ ...this.systemNotification, ...value });
        }
        this.activeModal.dismiss();
    }

    ngOnInit() {
        this.updateForm = this.fb.group({
            title: [this.systemNotification.title, Validators.required],
            text_de: [this.systemNotification.text_de, Validators.required],
            text_fr: [this.systemNotification.text_fr, Validators.required],
            text_it: [this.systemNotification.text_it, Validators.required],
            text_en: [this.systemNotification.text_en, Validators.required],
            type: [this.systemNotification.type, Validators.required],
            startDate: [this.systemNotification.startDate, Validators.required],
            endDate: [this.systemNotification.endDate, Validators.required],
            active: [this.systemNotification.active, Validators.required]
        });
    }
}
