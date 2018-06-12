import { Route } from '@angular/router';

import { RegistrationPavDialogComponent } from './registration-pav-dialog.component';

export const registrationPavDialogRoute: Route = {
    path: 'registration-pav-dialog',
    component: RegistrationPavDialogComponent,
    data: {
        authorities: [],
        pageTitle: 'registration.pav.dialog.title'
    }
};
