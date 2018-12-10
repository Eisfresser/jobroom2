import { Route } from '@angular/router';
import { LegalTermsManagementComponent } from './legal-terms-management.component';

export const legalTermsManagementRoute: Route = {
    path: 'legal-terms-management',
    component: LegalTermsManagementComponent,
    data: {
        pageTitle: 'legal-terms-management.title'
    }
};
