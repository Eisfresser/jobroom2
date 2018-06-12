import { Route } from '@angular/router';

import { RegistrationComponent } from './registration.component';

export const registrationRoute: Route = {
    path: 'registration',
    component: RegistrationComponent,
    data: {
        authorities: [],
        pageTitle: 'registration.title'
    }
};
