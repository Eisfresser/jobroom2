import { Route } from '@angular/router';

import { JobseekerDialogComponent } from './jobseeker-dialog.component';

export const jobseekerDialogRoute: Route = {
    path: 'registrationCustomer',
    component: JobseekerDialogComponent,
    data: {
        authorities: [],
        pageTitle: 'customer.title'
    }
};
