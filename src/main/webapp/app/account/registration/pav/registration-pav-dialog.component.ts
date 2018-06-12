import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {
    formatOrganizationName,
    OrganizationAutocomplete,
    OrganizationSuggestion
} from '../../../shared/organization/organization.model';
import { Observable } from 'rxjs/Observable';
import { OrganizationService } from '../../../shared/organization/organization.service';
import { RegistrationService } from '../registration.service';
import { Router } from '@angular/router';

@Component({
    selector: 'jr2-registration-pav-dialog',
    templateUrl: './registration-pav-dialog.component.html',
    styleUrls: ['./registration-pav-dialog.component.scss']
})
export class RegistrationPavDialogComponent implements OnInit {

    private static readonly ORGANIZATION_SUGGESTIONS_SIZE = 25;
    isSubmitted: boolean;

    userOrganization: OrganizationSuggestion;
    searchOrganizations = (text$: Observable<string>): Observable<Array<OrganizationSuggestion>> =>
        text$
            .debounceTime(200)
            .distinctUntilChanged()
            .flatMap((term) => term.length < 2 ? Observable.of(null)
                : this.organizationService.suggest(term, RegistrationPavDialogComponent.ORGANIZATION_SUGGESTIONS_SIZE))
            .map((autocomplete: OrganizationAutocomplete) => autocomplete ? autocomplete.organizations : []);
    formatter = (suggestion: OrganizationSuggestion) => formatOrganizationName(suggestion);

    constructor(public activeModal: NgbActiveModal,
                private router: Router,
                private registrationService: RegistrationService,
                private organizationService: OrganizationService) {
    }

    ngOnInit(): void {
        this.isSubmitted = false;
    }

    goToHomePage() {
        this.activeModal.dismiss();
    }

    requestActivationCode() {
        this.registrationService.requestAgentAccessCode(this.userOrganization.externalId)
            .subscribe((result) => {
                this.isSubmitted = true;
            });
    }

    close() {
        this.activeModal.dismiss();
    }
}
