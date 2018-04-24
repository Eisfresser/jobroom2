import { Component, OnInit } from '@angular/core';
import { SystemNotification } from '../../shared/system-notification/system.notification.model';
import { SystemNotificationsManagementModalCreateComponent } from './modals/system.notifications.management.modal.create.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotificationsManagementModalDeleteComponent } from './modals/system.notifications.management.modal.delete.component';
import { SystemNotificationsManagementModalDetailComponent } from './modals/system.notifications.management.modal.detail.component';

@Component({
    selector: 'jhi-sys-notifications',
    templateUrl: './system.notifications.management.component.html'
})
export class SystemNotificationsManagementComponent implements OnInit {
    systemNotifications: SystemNotification[];

    constructor(private modalService: NgbModal) {
    }

    ngOnInit() {
        this.systemNotifications = new Array(
            new SystemNotification(
                1,
                'Systemmitteilung 1',
                'SYSTEM',
                '01-01-2001',
                '01-01-2001',
                true
            ),
            new SystemNotification(
                2,
                'Systemmitteilung 2',
                'SYSTEM',
                '01-01-2001',
                '01-01-2001',
                false
            ),
            new SystemNotification(
                3,
                'Systemmitteilung 3',
                'SYSTEM',
                '01-01-2001',
                '01-01-2001',
                true
            ),
            new SystemNotification(
                4,
                'Systemmitteilung 4',
                'SYSTEM',
                '01-01-2001',
                '01-01-2001',
                false
            ),
            new SystemNotification(
                5,
                'Systemmitteilung 5',
                'SYSTEM',
                '01-01-2001',
                '01-01-2001',
                true
            )
        );
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
