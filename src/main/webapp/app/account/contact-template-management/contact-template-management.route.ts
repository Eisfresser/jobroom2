import { Route } from '@angular/router';

import { ContactTemplateManagementComponent } from './contact-template-management.component';

export const contactTemplateManagementRoute: Route = {
    path: 'contact-template-management',
    component: ContactTemplateManagementComponent,
    data: {
        pageTitle: 'global.menu.admin.contact-template-management'
    }
};
