import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotification } from '../../../shared/system-notification/system.notification.model';

@Component({
    selector: 'jhi-system-notifications-management-create',
    templateUrl: './system.notifications.management.modal.detail.component.html'
})
export class SystemNotificationsManagementModalDetailComponent {
    @Input() systemNotification: SystemNotification;
    @Output() updateEvent = new EventEmitter<SystemNotification>();

    constructor(public activeModal: NgbActiveModal) {}

    clear() {
        this.activeModal.dismiss('Cancel');
    }

    updateSystemNotification() {
        this.updateEvent.emit(this.systemNotification);
        this.activeModal.dismiss();
    }

}
