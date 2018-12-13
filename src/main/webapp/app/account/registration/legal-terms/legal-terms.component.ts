import { Component, TemplateRef, ViewChild } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { CurrentUser, LoginService, Principal } from '../../../shared';
import { ProfileInfo, ProfileService } from '../../../layouts';
import { RegistrationService } from '../registration.service';
import { LegalTermsService } from './legal-terms.service';
import { Observable } from 'rxjs/Observable';
import { RegistrationStatus } from '../../../shared/user-info/user-info.model';

@Component({
    selector: 'jr2-legal-terms',
    templateUrl: './legal-terms.component.html',
    styleUrls: ['./legal-terms.component.scss']
})
export class LegalTermsComponent {
    private modalRef: NgbModalRef;

    private noEiam: boolean;

    legalTermsUrl$: Observable<string>;

    @ViewChild(TemplateRef)
    legalTermModalTemplate: TemplateRef<any>;

    static isLegalTermAcceptanceRequired(currentUser: CurrentUser): boolean {
        return currentUser
            && (currentUser.registrationStatus === RegistrationStatus.REGISTERED)
            && (!currentUser.legalTermsAccepted);
    }

    constructor(private principal: Principal,
                private modalService: NgbModal,
                private registrationService: RegistrationService,
                private loginService: LoginService,
                private profileService: ProfileService,
                private router: Router,
                private legalTermsService: LegalTermsService) {

        this.profileService.getProfileInfo()
            .subscribe((profileInfo: ProfileInfo) => {
                this.noEiam = profileInfo.noEiam;
            });

        this.legalTermsUrl$ = this.legalTermsService.getCurrentLegalTermsUrl()
            .catch((error) => Observable.of('error'));

        this.principal.getAuthenticationState()
            .map(LegalTermsComponent.isLegalTermAcceptanceRequired)
            .subscribe((showModal: boolean) => {
                if (showModal && !this.modalRef) {
                    this.modalRef = this.modalService.open(this.legalTermModalTemplate, {
                        backdrop: 'static',
                        keyboard: false,
                        size: 'lg'
                    });
                } else if (!showModal && this.modalRef) {
                    this.modalRef.close();
                    this.modalRef = null;
                }
            });
    }

    logout() {
        this.loginService.logout();
        if (this.noEiam) {
            this.router.navigate(['']);
        } else {
            document.location.href = 'authentication/logout';
        }
    }

    acceptLegalTerms(): void {
        this.registrationService.acceptLegalTerms()
            .subscribe((response) => {
                this.principal.identity(true)
            });
    }
}
