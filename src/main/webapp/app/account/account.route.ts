import { Routes } from '@angular/router';

import {
    contactTemplateManagementRoute,
    registrationAccessCodeRoute,
    registrationQuestionnaireRoute,
    reloginRoute,
} from './';

const ACCOUNT_ROUTES = [
    reloginRoute,
    contactTemplateManagementRoute,
    registrationQuestionnaireRoute,
    registrationAccessCodeRoute
];

export const accountState: Routes = [{
    path: '',
    children: ACCOUNT_ROUTES
}];
