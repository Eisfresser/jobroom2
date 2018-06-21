import { Route } from '@angular/router';

import { RegistrationPavDialogComponent } from './registration-pav-dialog.component';
import { RegistrationGuardService } from '../registration-guard.service';

export const registrationPavDialogRoute: Route = {
    path: 'registration-pav-dialog',
    component: RegistrationPavDialogComponent,
    canActivate: [RegistrationGuardService],
    data: {
        authorities: [],
        pageTitle: 'registration.pav.dialog.title'
    }
};
