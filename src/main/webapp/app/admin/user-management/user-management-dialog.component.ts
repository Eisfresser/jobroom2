import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { PHONE_NUMBER_REGEX } from '../../shared/validation/regex-patterns';

import { UserModalService } from './user-modal.service';
import { JhiLanguageHelper, User, UserService } from '../../shared';
import {
    formatOrganizationName,
    Organization,
    OrganizationAutocomplete,
    OrganizationSuggestion
} from '../../shared/organization/organization.model';
import { OrganizationService } from '../../shared/organization/organization.service';
import { Observable } from 'rxjs/Observable';
import { Principal } from '../../shared/auth/principal.service';

@Component({
    selector: 'jhi-user-mgmt-dialog',
    templateUrl: './user-management-dialog.component.html'
})
export class UserMgmtDialogComponent implements OnInit {
    private static readonly ORGANIZATION_SUGGESTIONS_SIZE = 25;

    user: User;
    languages: any[];
    authorities: any[];
    isSaving: Boolean;
    phoneRegex = PHONE_NUMBER_REGEX;
    userOrganization: OrganizationSuggestion;

    constructor(
        public activeModal: NgbActiveModal,
        private languageHelper: JhiLanguageHelper,
        private userService: UserService,
        private principal: Principal,
        private eventManager: JhiEventManager,
        private organizationService: OrganizationService
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.authorities = [];
        this.userService.authorities().subscribe((authorities) => {
            if (this.principal.hasAnyAuthorityDirect(['ROLE_SYSADMIN'])) {
                this.authorities = authorities;
            } else {
                authorities.splice(authorities.indexOf('ROLE_SYSADMIN'), 1);
                this.authorities = authorities
            }
        });
        this.languageHelper.getAll().then((languages) => {
            this.languages = languages;
        });

        if (this.user.organizationId) {
            this.organizationService.findByExternalId(this.user.organizationId).subscribe((organization: Organization) => {
                this.userOrganization = organization as OrganizationSuggestion;
            });
        }
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        this.user.organizationId = this.userOrganization ? this.userOrganization.externalId : null;

        if (this.user.id !== null) {
            this.userService.update(this.user).subscribe((response) => this.onSaveSuccess(response), () => this.onSaveError());
        } else {
            this.userService.create(this.user).subscribe((response) => this.onSaveSuccess(response), () => this.onSaveError());
        }
    }

    private onSaveSuccess(result) {
        this.eventManager.broadcast({ name: 'userListModification', content: 'OK' });
        this.isSaving = false;
        this.activeModal.dismiss(result.body);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    searchOrganizations = (text$: Observable<string>): Observable<Array<OrganizationSuggestion>> =>
        text$
            .debounceTime(200)
            .distinctUntilChanged()
            .flatMap((term) => term.length < 2 ? Observable.of(null)
                : this.organizationService.suggest(term, UserMgmtDialogComponent.ORGANIZATION_SUGGESTIONS_SIZE))
                    .map((autocomplete: OrganizationAutocomplete) => autocomplete ? autocomplete.organizations : []);

    formatter = (suggestion: OrganizationSuggestion) => formatOrganizationName(suggestion);
}

@Component({
    selector: 'jhi-user-dialog',
    template: ''
})
export class UserDialogComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private userModalService: UserModalService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            setTimeout(() => {
                if ( params['login'] ) {
                    this.userModalService.open(UserMgmtDialogComponent as Component, params['login']);
                } else {
                    this.userModalService.open(UserMgmtDialogComponent as Component);
                }
            });
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
