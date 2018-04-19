import { Route, Routes } from '@angular/router';
import { SystemNotificationsManagementComponent } from './system.notifications.management.component';
import { SystemNotificationsManagementModalCreateComponent } from './system.notifications.management.modal.create.component';
import { SystemNotificationsManagementModalDeleteComponent } from './system.notifications.management.modal.delete.component';

export const systemNotificationsManagementRoute: Route = {
    path: 'system-notifications-management',
    component: SystemNotificationsManagementComponent,
    data: {
        pageTitle: 'system-notifications-management.title'
    }
};

export const systemNotificationsModalRoute: Routes = [
    {
        path: 'system-notification-new',
        component: SystemNotificationsManagementModalCreateComponent,
        outlet: 'popup'
    },
    {
        path: 'system-notification-delete',
        component: SystemNotificationsManagementModalDeleteComponent,
        outlet: 'popup'
    },
];
