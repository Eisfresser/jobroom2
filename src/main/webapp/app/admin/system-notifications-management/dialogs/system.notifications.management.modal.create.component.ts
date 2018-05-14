import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotification } from '../../../shared/system-notification/system.notification.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
    selector: 'jhi-system-notifications-management-create',
    templateUrl: './system.notifications.management.modal.create.component.html'
})
export class SystemNotificationsManagementModalCreateComponent
    implements OnInit {
    @Output() createEvent = new EventEmitter<SystemNotification>();

    createForm: FormGroup;

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
            endDate: ['', Validators.required],
            active: ['', Validators.required]
        });
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    createSystemNotification(form: FormGroup) {
        const { value } = form;
        this.createEvent.emit(value);
        this.activeModal.dismiss();
    }
}
