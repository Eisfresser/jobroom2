import { Route } from '@angular/router';

import { ReLoginComponent } from './relogin.component';

export const reloginRoute: Route = {
    path: 'relogin',
    component: ReLoginComponent,
    data: {
        authorities: [],
        pageTitle: 'relogin.title'
    }
};
