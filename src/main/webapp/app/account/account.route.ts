import { Routes } from '@angular/router';

import {
    activateRoute,
    passwordResetFinishRoute,
    passwordResetInitRoute,
    passwordRoute,
    registrationAccessCodeRoute,
    registrationQuestionnaireRoute,
    reloginRoute,
    settingsRoute
} from './';

const ACCOUNT_ROUTES = [
    activateRoute,
    passwordRoute,
    passwordResetFinishRoute,
    passwordResetInitRoute,
    reloginRoute,
    settingsRoute,
    registrationQuestionnaireRoute,
    registrationAccessCodeRoute
];

export const accountState: Routes = [{
    path: '',
    children: ACCOUNT_ROUTES
}];
