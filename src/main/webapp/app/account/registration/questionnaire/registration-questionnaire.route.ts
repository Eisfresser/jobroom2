import { Route } from '@angular/router';

import { RegistrationQuestionnaireComponent } from './registration-questionnaire.component';

export const registrationQuestionnaireRoute: Route = {
    path: 'registrationQuestionnaire',
    component: RegistrationQuestionnaireComponent,
    data: {
        authorities: [],
        pageTitle: 'userRoleSelection.title'
    }
};
