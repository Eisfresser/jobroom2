import { Route } from '@angular/router';
import { ApiUserManagementComponent } from './api-user-management.component';

export const apiUserManagementRoutes: Route[] = [{
    path: 'api-user-management',
    component: ApiUserManagementComponent,
    data: {
        pageTitle: 'global.menu.admin.api-user-management'
    }
}];
