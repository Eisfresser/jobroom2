import { Injectable } from '@angular/core';
import {
    ActivatedRouteSnapshot,
    CanActivate,
    RouterStateSnapshot
} from '@angular/router';
import { Principal } from '..';
import { LandingPageService } from './landing-page.service';

@Injectable()
export class LandingPageGuard implements CanActivate {

    constructor(private principal: Principal, private landingPageService: LandingPageService) {
    }

    canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        this.principal.identity()
            .then((currentUser) => this.landingPageService.navigateToLandingPage(currentUser))
            .catch(() => this.landingPageService.navigateToLandingPage());
        return false;
    }
}
