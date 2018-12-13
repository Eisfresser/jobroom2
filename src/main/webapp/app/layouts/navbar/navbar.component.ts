import { Component, ElementRef, HostListener, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Location } from '@angular/common';

import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';

import { ProfileService } from '../profiles/profile.service';
import {
    CurrentUser,
    JhiLanguageHelper,
    LoginModalService,
    LoginService,
    Principal
} from '../../shared';

import { VERSION } from '../../app.constants';
import { RegistrationStatus } from '../../shared/user-info/user-info.model';
import { Subscription } from 'rxjs';

@Component({
    selector: 'jhi-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: [
        'navbar.scss'
    ]
})
export class NavbarComponent implements OnInit, OnDestroy {
    currentUser: CurrentUser;
    inProduction: boolean;
    isNavbarCollapsed: boolean;
    languages: any[];
    swaggerEnabled: boolean;
    noEiam: boolean;
    modalRef: NgbModalRef;
    version: string;
    isReindexMenuCollapsed: boolean;
    hasCompletedRegistration = true;
    private authenticateSubscription: Subscription;
    private REGISTRATION_QUESTIONNAIRE_PAGE = '/registrationQuestionnaire';
    private ACCESS_CODE_PAGE = '/accessCode';

    constructor(private loginService: LoginService,
                private eventManager: JhiEventManager,
                private languageService: JhiLanguageService,
                private languageHelper: JhiLanguageHelper,
                private principal: Principal,
                private loginModalService: LoginModalService,
                private profileService: ProfileService,
                private router: Router,
                private elRef: ElementRef,
                private location: Location) {
        this.version = VERSION ? 'v' + VERSION : '';
        this.isNavbarCollapsed = true;
        this.isReindexMenuCollapsed = true;
    }

    ngOnInit() {
        this.authenticateSubscription = this.principal.getAuthenticationState()
            .subscribe((currentUser) => {
                this.currentUser = currentUser;
                if (currentUser) {
                    this.hasCompletedRegistration = currentUser.registrationStatus === RegistrationStatus.REGISTERED;
                }
            });

        this.languageHelper.getAll().then((languages) => {
            this.languages = languages;
        });

        this.profileService.getProfileInfo()
            .subscribe((profileInfo) => {
                this.inProduction = profileInfo.inProduction;
                this.swaggerEnabled = profileInfo.swaggerEnabled;
                this.noEiam = profileInfo.noEiam;
            });
    }

    ngOnDestroy() {
        this.authenticateSubscription.unsubscribe();
    }

    @HostListener('document:click', ['$event.target'])
    onClick(targetElement: HTMLElement): void {
        if (!targetElement) {
            return;
        }

        if (!this.elRef.nativeElement.contains(targetElement)) {
            this.collapseNavbar();
        }
    }

    changeLanguage(languageKey: string) {
        this.languageService.changeLanguage(languageKey);
    }

    collapseNavbar() {
        this.isNavbarCollapsed = true;
        this.isReindexMenuCollapsed = true;
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login(isMobile: boolean) {
        if (isMobile) {
            this.collapseNavbar();
        }
        this.modalRef = this.loginModalService.open();
    }

    logoutEiam() {
        this.collapseNavbar();
        this.loginService.logout();
        document.location.href = 'authentication/logout';
    }

    logoutLocal() {
        this.collapseNavbar();
        this.loginService.logout();
        this.router.navigate(['']);
    }

    goToEiamProfile() {
        this.collapseNavbar();
        document.location.href = 'authentication/profile';
    }

    hasNotCompletedRegistration() {
        return !this.location.isCurrentPathEqualTo(this.REGISTRATION_QUESTIONNAIRE_PAGE) &&
            !this.location.isCurrentPathEqualTo(this.ACCESS_CODE_PAGE) &&
            !this.hasCompletedRegistration;
    }

    goToFinishRegistration() {
        this.collapseNavbar();
        const currentRegistrationStatus = this.currentUser.registrationStatus;
        if (currentRegistrationStatus === RegistrationStatus.UNREGISTERED) {
            this.router.navigate([this.REGISTRATION_QUESTIONNAIRE_PAGE]);
        } else if (currentRegistrationStatus === RegistrationStatus.VALIDATION_PAV
            || currentRegistrationStatus === RegistrationStatus.VALIDATION_EMP) {
            this.router.navigate([this.ACCESS_CODE_PAGE]);
        }
    }

    toggleNavbar() {
        this.isNavbarCollapsed = !this.isNavbarCollapsed;
    }

    isAdmin(): boolean {
        return this.principal.hasAnyAuthorityDirect(['ROLE_ADMIN', 'ROLE_SYSADMIN']);
    }

    getCurrentLanguage() {
        return this.languageService.currentLang;
    }
}
