import { Routes } from '@angular/router';

import {
    activateRoute,
    passwordRoute,
    passwordResetFinishRoute,
    passwordResetInitRoute,
    registrationRoute,
    reloginRoute,
    settingsRoute,
    registrationQuestionnaireRoute,
    jobseekerDialogRoute,
    registrationCompanyDialogRoute,
    registrationPavDialogRoute,
    registrationAccessCodeRoute
} from './';

const ACCOUNT_ROUTES = [
    activateRoute,
    passwordRoute,
    passwordResetFinishRoute,
    passwordResetInitRoute,
    registrationRoute,
    reloginRoute,
    settingsRoute,
    registrationQuestionnaireRoute,
    jobseekerDialogRoute,
    registrationCompanyDialogRoute,
    registrationPavDialogRoute,
    registrationAccessCodeRoute
];

export const accountState: Routes = [{
    path: '',
    children: ACCOUNT_ROUTES
}];
