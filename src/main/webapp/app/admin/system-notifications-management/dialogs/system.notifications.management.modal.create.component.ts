import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotification } from '../../../shared/system-notification/system.notification.model';

@Component({
    selector: 'jhi-system-notifications-management-create',
    templateUrl: './system.notifications.management.modal.create.component.html'
})
export class SystemNotificationsManagementModalCreateComponent
    implements OnInit {
    @Input()
    systemNotification: SystemNotification;
    @Output() createEvent = new EventEmitter<SystemNotification>();

    constructor(public activeModal: NgbActiveModal) {
    }

    ngOnInit() {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    createSystemNotification() {
        this.createEvent.emit(this.systemNotification);
        this.activeModal.dismiss();
    }
}
