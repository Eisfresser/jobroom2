import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotification } from '../../../shared/system-notification/system.notification.model';
import { SystemNotificationService } from '../../../shared/system-notification/system.notification.service';

@Component({
    selector: 'jhi-system-notifications-management-delete',
    templateUrl: './system.notifications.management.modal.delete.component.html'
})
export class SystemNotificationsManagementModalDeleteComponent
    implements OnInit {

    @Input()
    systemNotification: SystemNotification;
    systemNotificationService: SystemNotificationService;

    constructor(public activeModal: NgbActiveModal,
    systemNotificationService: SystemNotificationService) {
        this.systemNotificationService = systemNotificationService;
    }

    ngOnInit() {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    delete() {
        console.log(this.systemNotification.id.toString());
        this.systemNotificationService
            .delete(this.systemNotification.id.toString())
            .subscribe(
                (response) => this.onDeleteSuccess(response),
                () => this.onSaveError()
            );
    }

    private onDeleteSuccess(result) {
        this.activeModal.dismiss(result.body);
    }

    private onSaveError() {
        console.log('Error');
    }
}
