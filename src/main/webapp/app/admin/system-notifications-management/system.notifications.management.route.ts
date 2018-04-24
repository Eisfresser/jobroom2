import { Route, Routes } from '@angular/router';
import { SystemNotificationsManagementComponent } from './system.notifications.management.component';
import { SystemNotificationsManagementModalCreateComponent } from './modals/system.notifications.management.modal.create.component';
import { SystemNotificationsManagementModalDeleteComponent } from './modals/system.notifications.management.modal.delete.component';

export const systemNotificationsManagementRoute: Route = {
    path: 'system-notifications-management',
    component: SystemNotificationsManagementComponent,
    data: {
        pageTitle: 'system-notifications-management.title'
    }
};
