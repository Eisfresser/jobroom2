import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { CookieService } from 'ngx-cookie';
import { SessionStorageService } from 'ngx-webstorage';

@Injectable()
export class AuthResolverService implements Resolve<String> {
    constructor(                private _cookieService: CookieService,
                                private $sessionStorage: SessionStorageService,
                                private router: Router) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<String> {
        const ck = this._cookieService.get('jwt');
        console.log(ck);  // TODO: remove; only for debugging during development
        this.$sessionStorage.store('authenticationToken', ck);
        // TODO: authenticate in application
        this.router.navigate(['/']);
        return Observable.of('hello');
    }

}
