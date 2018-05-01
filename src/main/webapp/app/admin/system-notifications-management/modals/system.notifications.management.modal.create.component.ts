import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotification } from '../../../shared/system-notification/system.notification.model';
import { SystemNotificationService } from '../../../shared/system-notification/system.notification.service';

@Component({
    selector: 'jhi-system-notifications-management-create',
    templateUrl: './system.notifications.management.modal.create.component.html'
})
export class SystemNotificationsManagementModalCreateComponent
    implements OnInit {
    systemNotification: SystemNotification;
    systemNotificationService: SystemNotificationService;

    @Output()
    new: EventEmitter<SystemNotification> = new EventEmitter<SystemNotification>();

    constructor(
        public activeModal: NgbActiveModal,
        systemNotificationService: SystemNotificationService
    ) {
        this.systemNotificationService = systemNotificationService;
    }

    ngOnInit() {
        this.systemNotification = new SystemNotification(
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
    }

    clear() {
        this.activeModal.dismiss('Cancel');
    }

    save() {
        this.systemNotificationService
            .create(this.systemNotification)
            .subscribe(
                (response) => this.onSaveSuccess(response),
                () => this.onSaveError()
            );
    }

    private onSaveSuccess(result) {
        this.activeModal.dismiss(result.body);
    }

    private onSaveError() {
        console.log('error');
    }
}
