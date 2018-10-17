import { Component, OnInit } from '@angular/core';

import { Observable } from 'rxjs';
import { BlacklistedAgentService } from './blacklisted-agent.service';
import { ModalUtils } from '../../shared/index';
import { BlacklistedAgentChangeStatusDialogComponent } from './blacklisted-agent-change-status-dialog/blacklisted-agent-change-status-dialog.component';
import { BlacklistedAgent, BlacklistedAgentStatus } from './blacklisted.agent.model';

@Component({
    selector: 'jr2-blacklisted-agent-management',
    templateUrl: './blacklisted-agent-management.component.html',
    styleUrls: []
})
export class BlacklistedAgentManagementComponent implements OnInit {
    blacklistedAgents$: Observable<BlacklistedAgent[]>;
    filterText: string;

    constructor(
        private blacklistedAgentService: BlacklistedAgentService,
        private modalUtils: ModalUtils) {
    }

    ngOnInit(): void {
        this.getAllBlacklistedAgents();
    }

    isActive(agent: BlacklistedAgent) {
        return !!agent && agent.status === BlacklistedAgentStatus.ACTIVE;
    }

    activate(agent: BlacklistedAgent) {
        this.changeStatus(agent, BlacklistedAgentStatus.ACTIVE);
    }

    deactivate(agent: BlacklistedAgent) {
        this.changeStatus(agent, BlacklistedAgentStatus.INACTIVE);
    }

    private getAllBlacklistedAgents() {
        this.blacklistedAgents$ = this.blacklistedAgentService.getAllBlacklistedAgents();
    }

    private changeStatus(agent: BlacklistedAgent, status: BlacklistedAgentStatus) {
        this.modalUtils.openLargeModal(BlacklistedAgentChangeStatusDialogComponent)
            .result
            .then(() => this.blacklistedAgentService.changeStatus(agent, status)
                .subscribe(() => this.getAllBlacklistedAgents()))
            .catch((error) => console.log(error));
    }
}
