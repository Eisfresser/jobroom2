import { Component, EventEmitter, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotification } from '../../../shared/system-notification/system.notification.model';

@Component({
    selector: 'jhi-system-notifications-management-create',
    templateUrl: './system.notifications.management.modal.create.component.html'
})
export class SystemNotificationsManagementModalCreateComponent {

    @Output()
    update: EventEmitter<SystemNotification> = new EventEmitter<SystemNotification>();

    constructor(public activeModal: NgbActiveModal) {
    }

    clear() {
        this.activeModal.dismiss('Cancel');
    }
}
