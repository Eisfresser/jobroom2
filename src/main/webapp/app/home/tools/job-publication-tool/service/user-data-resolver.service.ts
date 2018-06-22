import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { CurrentUser, Principal } from '../../../../shared';
import { Observable } from 'rxjs/Observable';
import { CompanyService } from '../../../../shared/company/company.service';
import { Company } from '../../../../shared/company/company.model';

export interface UserData extends CurrentUser {
    company?: Company;
}

@Injectable()
export class UserDataResolverService implements Resolve<UserData> {

    constructor(private principal: Principal,
                private companyService: CompanyService) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<UserData> {
        return this.principal.currentUser()
            .flatMap((currentUser) => {
                return this.principal.isCompanyOrAgent()
                    .flatMap((hasCompany) => hasCompany && currentUser.companyId ? this.companyService.findByExternalId(currentUser.companyId) : Observable.empty())
                    .map((company) => Object.assign({}, currentUser, { company: company }));
            });
    }

}
