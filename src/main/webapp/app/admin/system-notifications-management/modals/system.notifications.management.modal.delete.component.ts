import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotification } from '../../../shared/system-notification/system.notification.model';

@Component({
    selector: 'jhi-system-notifications-management-delete',
    templateUrl: './system.notifications.management.modal.delete.component.html'
})
export class SystemNotificationsManagementModalDeleteComponent
    implements OnInit {

    @Input()
    systemNotification: SystemNotification;

    constructor(public activeModal: NgbActiveModal) {}

    ngOnInit() {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }
}
