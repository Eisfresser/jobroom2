import { Route } from '@angular/router';

import { RegistrationAccessCodeComponent } from './registration-access-code.component';

export const registrationAccessCodeRoute: Route = {
    path: 'accessCode',
    component: RegistrationAccessCodeComponent,
    data: {
        authorities: [],
        pageTitle: 'registrationAccessCode.title'
    }
};
