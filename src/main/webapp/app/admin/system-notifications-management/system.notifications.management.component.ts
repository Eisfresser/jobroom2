import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { SystemNotificationsManagementModalCreateComponent } from './dialogs/system.notifications.management.modal.create.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotificationsManagementModalDeleteComponent } from './dialogs/system.notifications.management.modal.delete.component';
import { SystemNotificationsManagementModalDetailComponent } from './dialogs/system.notifications.management.modal.detail.component';
import { Observable } from 'rxjs/Observable';
import { Store } from '@ngrx/store';
import {
    getAllSystemNotifications,
    SystemNotificationState
} from './state-management/state/system-notification-management.state';
import {
    CreateSystemNotificationAction,
    DeleteSystemNotificationAction,
    GetAllSystemNotificationsAction,
    UpdateSystemNotificationAction
} from './state-management/actions/system-notification-management.actions';
import { SystemNotification } from '../../shared/system-notification/system.notification.model';
import { SystemNotificationService } from '../../shared/system-notification/system.notification.service';

@Component({
    selector: 'jhi-sys-notifications',
    templateUrl: './system.notifications.management.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SystemNotificationsManagementComponent implements OnInit {
    systemNotifications$: Observable<SystemNotification[]>;
    systemNotificationService: SystemNotificationService;

    constructor(
        private store: Store<SystemNotificationState>,
        private modalService: NgbModal,
        systemNotificationService: SystemNotificationService
    ) {
        this.systemNotificationService = systemNotificationService;
    }

    ngOnInit(): void {
        this.systemNotifications$ = this.store.select(
            getAllSystemNotifications
        );
        this.store.dispatch(new GetAllSystemNotificationsAction());
    }

    openCreateModal() {
        const modalRef = this.modalService.open(
            SystemNotificationsManagementModalCreateComponent
        );
        modalRef.componentInstance.createEvent.subscribe(
            (systemNotificationToCreate) => {
                this.store.dispatch(
                    new CreateSystemNotificationAction(
                        systemNotificationToCreate
                    )
                );
            }
        );
    }

    openDetailModal(systemNotification: SystemNotification) {
        const modalRef = this.modalService.open(
            SystemNotificationsManagementModalDetailComponent
        );
        modalRef.componentInstance.systemNotification = systemNotification;
        modalRef.componentInstance.updateEvent.subscribe(
            (systemNotificationToUpdate) => {
                this.store.dispatch(
                    new UpdateSystemNotificationAction(
                        systemNotificationToUpdate
                    )
                );
            }
        );
    }

    openDeleteModal(systemNotification: SystemNotification) {
        const modalRef = this.modalService.open(
            SystemNotificationsManagementModalDeleteComponent
        );
        modalRef.componentInstance.systemNotification = systemNotification;
        modalRef.componentInstance.deleteEvent.subscribe(
            (systemNotificationToDelete) => {
                this.store.dispatch(
                    new DeleteSystemNotificationAction(
                        systemNotificationToDelete
                    )
                );
            }
        );
    }

    setActive(
        systemNotificationToUpdate: SystemNotification,
        isActivated: boolean
    ) {
        systemNotificationToUpdate.active = isActivated;
        this.store.dispatch(
            new UpdateSystemNotificationAction(systemNotificationToUpdate)
        );
    }
}
