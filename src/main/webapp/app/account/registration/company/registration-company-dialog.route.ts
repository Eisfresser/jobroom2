import { Route } from '@angular/router';

import { RegistrationCompanyDialogComponent } from './registration-company-dialog.component';
import { RegistrationGuardService } from '../registration-guard.service';

export const registrationCompanyDialogRoute: Route = {
    path: 'registration-company-dialog',
    component: RegistrationCompanyDialogComponent,
    canActivate: [RegistrationGuardService],
    data: {
        authorities: [],
        pageTitle: 'registration.company.dialog.title'
    }
};
