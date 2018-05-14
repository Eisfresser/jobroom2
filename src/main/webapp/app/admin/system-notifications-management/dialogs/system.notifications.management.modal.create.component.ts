import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotification } from '../../../shared/system-notification/system.notification.model';
import {
    FormBuilder,
    FormGroup,
    Validators
} from '@angular/forms';

@Component({
    selector: 'jhi-system-notifications-management-create',
    templateUrl: './system.notifications.management.modal.create.component.html'
})
export class SystemNotificationsManagementModalCreateComponent
    implements OnInit {
    @Input() systemNotification: SystemNotification;
    @Output() createEvent = new EventEmitter<SystemNotification>();

    createForm: FormGroup;

    constructor(public activeModal: NgbActiveModal, private fb: FormBuilder) {
    }

    ngOnInit() {
        this.createForm = this.fb.group({
            title: [
                this.systemNotification.title,
                Validators.required,
                Validators.minLength(5),
                Validators.maxLength(50)
            ],
            text_de: [
                this.systemNotification.text_de,
                Validators.required,
                Validators.maxLength(150)
            ],
            text_fr: [
                this.systemNotification.text_fr,
                Validators.required,
                Validators.maxLength(150)
            ],
            text_it: [
                this.systemNotification.text_it,
                Validators.required,
                Validators.maxLength(150)
            ],
            text_en: [
                this.systemNotification.text_en,
                Validators.required,
                Validators.maxLength(150)
            ],
            type: [
                this.systemNotification.type,
                Validators.required,
                Validators.maxLength(50)
            ],
            startDate: [this.systemNotification.startDate, Validators.required],
            endDate: [this.systemNotification.endDate, Validators.required],
            active: [this.systemNotification.active, Validators.required]
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
