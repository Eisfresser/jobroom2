import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SystemNotificationsManagementModalService } from './system.notifications.management.modal.service';

@Component({
    selector: 'jhi-system-notifications-management-delete',
    templateUrl: './system.notifications.management.modal.delete.component.html'
})
export class SystemNotificationsManagementModalDeleteComponent
    implements OnInit, OnDestroy {
    routeSub: any;

    constructor(
        public activeModal: NgbActiveModal,
        private route: ActivatedRoute,
        private systemNotificationService: SystemNotificationsManagementModalService
    ) {
    }

    ngOnInit() {
    }


    clear() {
        this.activeModal.dismiss('cancel');
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
