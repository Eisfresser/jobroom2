import { Component, OnInit } from '@angular/core';
import { ModalUtils } from '../../../shared/index';
import {
    formatOrganizationName,
    OrganizationAutocomplete,
    OrganizationSuggestion
} from '../../../shared/organization/organization.model';
import { Observable } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { RegistrationService } from '../../../account';
import { OrganizationService } from '../../../shared/organization/organization.service';

@Component({
    selector: 'jr2-add-blacklisted-agent-dialog',
    templateUrl: './add-blacklisted-agent-dialog.component.html',
    styles: []
})
export class AddBlacklistedAgentDialogComponent {

    private static readonly ORGANIZATION_SUGGESTIONS_SIZE = 25;

    userOrganization: OrganizationSuggestion;
    searchOrganizations = (text$: Observable<string>): Observable<Array<OrganizationSuggestion>> =>
        text$
            .debounceTime(200)
            .distinctUntilChanged()
            .flatMap((term) => term.length < 2 ? Observable.of(null)
                : this.organizationService.suggest(term, AddBlacklistedAgentDialogComponent.ORGANIZATION_SUGGESTIONS_SIZE))
            .map((autocomplete: OrganizationAutocomplete) => autocomplete ? autocomplete.organizations : []);
    formatter = (suggestion: OrganizationSuggestion) => formatOrganizationName(suggestion);

    constructor(public modalUtils: ModalUtils,
                private router: Router,
                private registrationService: RegistrationService,
                private organizationService: OrganizationService) {
    }

    selectOrganisation() {
        this.modalUtils.closeActiveModal(this.userOrganization.externalId);
    }

    close() {
        this.modalUtils.dismissActiveModal();
    }
}
