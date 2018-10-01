import { Component, ElementRef, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
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

@Component({
    selector: 'jhi-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: [
        'navbar.scss'
    ]
})
export class NavbarComponent implements OnInit {
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

    constructor(private loginService: LoginService,
                private eventManager: JhiEventManager,
                private languageService: JhiLanguageService,
                private languageHelper: JhiLanguageHelper,
                private principal: Principal,
                private loginModalService: LoginModalService,
                private profileService: ProfileService,
                private router: Router,
                private elRef: ElementRef) {
        this.version = VERSION ? 'v' + VERSION : '';
        this.isNavbarCollapsed = true;
        this.isReindexMenuCollapsed = true;
    }

    ngOnInit() {
        this.principal.getAuthenticationState()
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
        document.location.href = 'api/redirect/logout';
    }

    logoutLocal() {
        this.collapseNavbar();
        this.loginService.logout();
        this.router.navigate(['']);
    }

    goToEiamProfile() {
        this.collapseNavbar();
        document.location.href = 'api/redirect/profile';
    }

    hasNotCompletedRegistration() {
        return !this.hasCompletedRegistration;
    }

    goToFinishRegistration() {
        this.collapseNavbar();
        const currentRegistrationStatus = this.currentUser.registrationStatus;
        if (currentRegistrationStatus === RegistrationStatus.UNREGISTERED) {
            this.router.navigate(['/registrationQuestionnaire']);
        } else if (currentRegistrationStatus === RegistrationStatus.VALIDATION_PAV
            || currentRegistrationStatus === RegistrationStatus.VALIDATION_EMP) {
            this.router.navigate(['/accessCode']);
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
