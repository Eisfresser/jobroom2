import { Route } from '@angular/router';

import { RegistrationCompanyDialogComponent } from './registration-company-dialog.component';

export const registrationCompanyDialogRoute: Route = {
    path: 'registration-company-dialog',
    component: RegistrationCompanyDialogComponent,
    data: {
        authorities: [],
        pageTitle: 'registration.company.dialog.title'
    }
};
