import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { Principal } from '../../shared/auth/principal.service';
import { JhiLanguageService } from 'ng-jhipster';
import { AuthServerProvider } from '../../shared/auth/auth-jwt.service';

@Injectable()
export class AuthResolverService implements Resolve<String> {
    constructor(                private principal: Principal,
                                private languageService: JhiLanguageService,
                                private authServerProvider: AuthServerProvider,
                                private router: Router) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<String> {
        this.authServerProvider.retrieveToken().subscribe((data) => {
            console.log('jwt: ' + data);
            this.principal.identity(true).then((account) => {
                // After the login the language will be changed to
                // the language selected by the user during his registration
                if (account !== null) {
                    this.languageService.changeLanguage(account.langKey);
                }
            });
        }, (err) => {
            // TODO: proper exception handling
            // this.logout();
            console.log('error with jwt');
        });
        // TODO: currently we navigate to the home page; this should go to the last visited page
        this.router.navigate(['/']);
        return Observable.of(null);
    }

}
