import { Route } from '@angular/router';
import { BlacklistedAgentManagementComponent } from './blacklisted-agent-management.component';

export const blacklistedAgentRoute: Route = {
    path: 'blacklisted-agent',
    component: BlacklistedAgentManagementComponent,
    data: {
        pageTitle: 'blacklisted-agent.title'
    }
};
