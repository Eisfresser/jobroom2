import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { CookieService } from 'ngx-cookie';
import { SessionStorageService } from 'ngx-webstorage';
import { Principal } from '../../shared/auth/principal.service';
import { JhiLanguageService } from 'ng-jhipster';

@Injectable()
export class AuthResolverService implements Resolve<String> {
    constructor(                private _cookieService: CookieService,
                                private $sessionStorage: SessionStorageService,
                                private principal: Principal,
                                private languageService: JhiLanguageService,
                                private router: Router) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<String> {
        const ck = this._cookieService.get('jwt');
        console.log(ck);  // TODO: remove; only for debugging during development
        this.$sessionStorage.store('authenticationToken', ck);
        this.principal.identity(true).then((account) => {
            // After the login the language will be changed to
            // the language selected by the user during his registration
            if (account !== null) {
                this.languageService.changeLanguage(account.langKey);
            }
        });
        this.router.navigate(['/']);
        return Observable.of('hello');
    }

}
