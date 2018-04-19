import {Route} from '@angular/router';
import {SystemNotificationsManagementComponent} from './system.notifications.management.component';

export const systemNotificationsManagementRoute: Route = {
    path: "system-notifications-management",
    component: SystemNotificationsManagementComponent,
    data: {
        pageTitle: "system-notifications-management.title"
    }
};
