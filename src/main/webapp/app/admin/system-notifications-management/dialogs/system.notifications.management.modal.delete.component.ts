import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotification } from '../../../home/system-notification/system.notification.model';

@Component({
    selector: 'jhi-system-notifications-management-delete',
    templateUrl: './system.notifications.management.modal.delete.component.html'
})
export class SystemNotificationsManagementModalDeleteComponent {
    @Input() systemNotification: SystemNotification;
    @Output() deleteEvent = new EventEmitter<SystemNotification>();

    constructor(public activeModal: NgbActiveModal) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    deleteSystemNotification() {
        this.deleteEvent.emit(this.systemNotification);
        this.activeModal.dismiss();
    }
}
