import { Component, OnInit } from '@angular/core';
import { SystemNotification } from '../../shared/system-notification/system.notification.model';
import { SystemNotificationsManagementModalCreateComponent } from './modals/system.notifications.management.modal.create.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotificationsManagementModalDeleteComponent } from './modals/system.notifications.management.modal.delete.component';
import { SystemNotificationsManagementModalDetailComponent } from './modals/system.notifications.management.modal.detail.component';
import { SystemNotificationService } from '../../shared/system-notification/system.notification.service';

@Component({
    selector: 'jhi-sys-notifications',
    templateUrl: './system.notifications.management.component.html'
})
export class SystemNotificationsManagementComponent implements OnInit {
    systemNotifications: SystemNotification[];
    systemNotificationService: SystemNotificationService;

    constructor(
        private modalService: NgbModal,
        systemNotificationService: SystemNotificationService
    ) {
        this.systemNotificationService = systemNotificationService;
    }

    ngOnInit(): void {
        this.systemNotifications = this.systemNotificationService.getAllSystemNotifications();
    }

    openCreateModal() {
        const modalRef = this.modalService.open(
            SystemNotificationsManagementModalCreateComponent
        );
    }

    openDetailModal(systemNotification: SystemNotification) {
        const modalRef = this.modalService.open(
            SystemNotificationsManagementModalDetailComponent
        );
        modalRef.componentInstance.systemNotification = systemNotification;
    }

    openDeleteModal(systemNotification: SystemNotification) {
        const modalRef = this.modalService.open(
            SystemNotificationsManagementModalDeleteComponent
        );
        modalRef.componentInstance.systemNotification = systemNotification;
    }
}
