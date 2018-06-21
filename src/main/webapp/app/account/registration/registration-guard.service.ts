import { Injectable } from '@angular/core';
import { Principal } from '../../shared';
import {
    ActivatedRouteSnapshot,
    CanActivate,
    RouterStateSnapshot
} from '@angular/router';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class RegistrationGuardService implements CanActivate {

    constructor(private principal: Principal) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
        return this.principal.identity()
            .then((account) => {
                if (!account) {
                    return false;
                }
                return this.principal.hasAnyAuthority([
                    'ROLE_JOBSEEKER_CLIENT',
                    'ROLE_COMPANY',
                    'ROLE_PRIVATE_EMPLOYMENT_AGENT'
                ]).then((hasAuthority) => {
                    return !hasAuthority;
                });

            });
    }

}
