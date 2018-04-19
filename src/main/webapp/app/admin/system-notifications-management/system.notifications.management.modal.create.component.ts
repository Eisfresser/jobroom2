import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SystemNotificationsManagementModalService } from './system.notifications.management.modal.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-system-notifications-management-create',
    templateUrl: './system.notifications.management.modal.create.component.html'
})
export class SystemNotificationsManagementModalCreateComponent
    implements OnInit, OnDestroy {
    routeSub: any;

    constructor(
        public activeModal: NgbActiveModal,
        private route: ActivatedRoute,
        private systemNotificationService: SystemNotificationsManagementModalService
    ) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            setTimeout(() => {
                    this.systemNotificationService.open(SystemNotificationsManagementModalCreateComponent as Component);
                });
            });
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
