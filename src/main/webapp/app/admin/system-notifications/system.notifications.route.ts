import {Route} from '@angular/router';
import {SystemNotificationsComponent} from "./system.notifications.component";

export const systemNotificationsRoute: Route = {
    path: 'system-notifications',
    component: SystemNotificationsComponent,
    data: {
        pageTitle: 'system.notifications.title'
    }
};
