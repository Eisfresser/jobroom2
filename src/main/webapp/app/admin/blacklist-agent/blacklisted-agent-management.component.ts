import { Component, OnInit } from '@angular/core';

import { Observable } from 'rxjs';
import { BlacklistedAgentService } from './blacklisted-agent.service';
import { ModalUtils } from '../../shared/index';
import { BlacklistedAgentChangeStatusDialogComponent } from './blacklisted-agent-change-status-dialog/blacklisted-agent-change-status-dialog.component';
import { BlacklistedAgent, BlacklistedAgentStatus } from './blacklisted.agent.model';
import { AddBlacklistedAgentDialogComponent } from './add-blacklisted-agent-dialog/add-blacklisted-agent-dialog.component';

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

    openCreateDialog() {
        this.modalUtils.openLargeModal(AddBlacklistedAgentDialogComponent).result
            .then((organisationId) => this.create(organisationId))
    }

    create(organisationId: string) {
        this.blacklistedAgentService.createBlacklistEntryForPav(organisationId).subscribe(
            () => {
                console.log('create blacklist entry for organisation: ' + organisationId);
                this.getAllBlacklistedAgents();
            }, (error) => {
                console.log('could not create blacklist entry for organisation: %s reason: %o', organisationId, error);
            }
        )
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
