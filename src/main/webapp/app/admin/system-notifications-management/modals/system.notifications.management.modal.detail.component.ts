import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotification } from '../../../shared/system-notification/system.notification.model';

@Component({
    selector: 'jhi-system-notifications-management-create',
    templateUrl: './system.notifications.management.modal.detail.component.html'
})
export class SystemNotificationsManagementModalDetailComponent {

    @Input()
    systemNotification: SystemNotification;

    @Output()
    update: EventEmitter<SystemNotification> = new EventEmitter<SystemNotification>();

    constructor(public activeModal: NgbActiveModal) {
    }

    clear() {
        this.activeModal.dismiss('Cancel');
    }

    handleSubmit(systemNotification: SystemNotification, isValid: boolean) {
        if (isValid) {
            this.update.emit(systemNotification);
        }
    }
}
