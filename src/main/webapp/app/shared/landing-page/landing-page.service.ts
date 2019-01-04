import { Injectable } from '@angular/core';
import { RegistrationStatus } from '../user-info/user-info.model';
import { CurrentUser } from '..';
import { Router } from '@angular/router';

const hasAnyAuthority = (user: CurrentUser, authorities: string[]) => {
    return user.authorities.some((value) => -1 !== authorities.indexOf(value));
};

const isRegistered = (user: CurrentUser) => {
    return user.registrationStatus === RegistrationStatus.REGISTERED || hasAnyAuthority(user, ['ROLE_ADMIN']);
};

@Injectable()
export class LandingPageService {

    constructor(
        private router: Router) {
    }

    navigateToLandingPage(user?: CurrentUser): Promise<boolean> {
        return this.router.navigate(this.getLandingPageUrl(user));
    }

    private getLandingPageUrl(user?: CurrentUser) {
        if (!user) {
            return ['/home'];
        }

        if (isRegistered(user)) {
            return ['/home'];
        }

        switch (user.registrationStatus) {
            case RegistrationStatus.UNREGISTERED :
                return ['/registrationQuestionnaire'];

            case RegistrationStatus.VALIDATION_PAV :
            case RegistrationStatus.VALIDATION_EMP :
                return ['/accessCode'];

            case RegistrationStatus.REGISTERED :
            default:
                return ['/home'];
        }

    }
}
