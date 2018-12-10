import { Routes } from '@angular/router';

import {
    auditsRoute,
    configurationRoute,
    docsRoute,
    gatewayRoute,
    healthRoute,
    logsRoute,
    metricsRoute,
    systemNotificationsManagementRoute,
    userDialogRoute,
    userInfoRoute,
    userMgmtRoute,
} from './';

import { UserRouteAccessService } from '../shared';
import { apiUserManagementRoutes } from './api-user-management/api-user-management.route';
import { blacklistedAgentRoute } from './blacklist-agent/blacklisted-agent.route';
import { legalTermsManagementRoute } from './legal-terms-management/legal-terms-management.route';

const ADMIN_ROUTES = [
    auditsRoute,
    configurationRoute,
    userInfoRoute,
    docsRoute,
    healthRoute,
    logsRoute,
    gatewayRoute,
    ...userMgmtRoute,
    metricsRoute,
    systemNotificationsManagementRoute,
    ...apiUserManagementRoutes,
    blacklistedAgentRoute,
    legalTermsManagementRoute
];

export const adminState: Routes = [
    {
        path: '',
        data: {
            authorities: ['ROLE_ADMIN']
        },
        canActivate: [UserRouteAccessService],
        children: ADMIN_ROUTES
    },
    ...userDialogRoute
];
