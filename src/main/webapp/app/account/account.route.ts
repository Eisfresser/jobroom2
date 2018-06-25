import { Routes } from '@angular/router';

import {
    activateRoute,
    jobseekerDialogRoute,
    passwordResetFinishRoute,
    passwordResetInitRoute,
    passwordRoute,
    registrationAccessCodeRoute,
    registrationCompanyDialogRoute,
    registrationPavDialogRoute,
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
    jobseekerDialogRoute,
    registrationCompanyDialogRoute,
    registrationPavDialogRoute,
    registrationAccessCodeRoute
];

export const accountState: Routes = [{
    path: '',
    children: ACCOUNT_ROUTES
}];
