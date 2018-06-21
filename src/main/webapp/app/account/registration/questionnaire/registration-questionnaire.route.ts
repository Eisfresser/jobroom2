import { Route } from '@angular/router';

import { RegistrationQuestionnaireComponent } from './registration-questionnaire.component';
import { RegistrationGuardService } from '../registration-guard.service';

export const registrationQuestionnaireRoute: Route = {
    path: 'registrationQuestionnaire',
    component: RegistrationQuestionnaireComponent,
    canActivate: [RegistrationGuardService],
    data: {
        authorities: [],
        pageTitle: 'userRoleSelection.title'
    }
};
