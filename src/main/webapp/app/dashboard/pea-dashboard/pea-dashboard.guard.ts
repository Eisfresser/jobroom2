import { CanActivate } from '@angular/router';
import { Injectable } from '@angular/core';
import { Principal } from '../../shared';

@Injectable()
export class PeaDashboardGuard implements CanActivate {
    constructor(private principal: Principal) {
    }

    canActivate() {
        return this.principal.currentUser()
            .flatMap(() => this.principal.isCompanyOrAgent())
    }
}
