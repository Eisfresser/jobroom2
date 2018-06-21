import { Route } from '@angular/router';

import { RegistrationAccessCodeComponent } from './registration-access-code.component';
import { RegistrationGuardService } from '../registration-guard.service';

export const registrationAccessCodeRoute: Route = {
    path: 'accessCode',
    component: RegistrationAccessCodeComponent,
    canActivate: [RegistrationGuardService],
    data: {
        authorities: [],
        pageTitle: 'registrationAccessCode.title'
    }
};
