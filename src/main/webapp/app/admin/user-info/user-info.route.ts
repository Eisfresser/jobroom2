import { Route } from '@angular/router';

import { UserInfoComponent } from './user-info.component';

export const userInfoRoute: Route = {
    path: 'user-info',
    component: UserInfoComponent,
    data: {
        pageTitle: 'global.menu.admin.user-info'
    }
};
