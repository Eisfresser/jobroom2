import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { AuthServerProvider } from './auth-jwt.service';
import { SERVER_API_URL } from '../../app.constants';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class Principal {

    private userIdentity: CurrentUser;

    private authenticated = false;

    private authenticationState = new Subject<any>();

    constructor(
        private  authServerProvider: AuthServerProvider,
        private http: HttpClient
    ) {
    }

    authenticate(identity) {
        this.userIdentity = identity;
        this.authenticated = identity !== null;
        this.authenticationState.next(this.userIdentity);
    }

    isAgent(): Observable<boolean> {
        return Observable.fromPromise(this.hasAnyAuthority(['ROLE_PRIVATE_EMPLOYMENT_AGENT']));
    }

    isCompany(): Observable<boolean> {
        return Observable.fromPromise(this.hasAnyAuthority(['ROLE_COMPANY']));
    }

    isCompanyOrAgent(): Observable<boolean> {
        return Observable.fromPromise(this.hasAnyAuthority(['ROLE_COMPANY', 'ROLE_PRIVATE_EMPLOYMENT_AGENT']));
    }

    hasAnyAuthority(authorities: string[]): Promise<boolean> {
        return Promise.resolve(this.hasAnyAuthorityDirect(authorities));
    }

    hasAnyAuthorityDirect(authorities: string[]): boolean {
        if (!this.authenticated || !this.userIdentity || !this.userIdentity.authorities) {
            return false;
        }

        for (let i = 0; i < authorities.length; i++) {
            if (this.userIdentity.authorities.includes(authorities[i])) {
                return true;
            }
        }

        return false;
    }

    currentUser(force?: boolean): Observable<CurrentUser> {
        return Observable.fromPromise(this.identity(force));
    }

    identity(force?: boolean): Promise<CurrentUser> {
        if (force === true) {
            this.userIdentity = undefined;
        }

        // check and see if we have retrieved the userIdentity data from the server.
        // if we have, reuse it by immediately resolving
        if (this.userIdentity) {
            return Promise.resolve(this.userIdentity);
        }

        // retrieve the userIdentity data from the server, update the identity object, and then resolve.
        return this.http.get<CurrentUser>(SERVER_API_URL + 'api/current-user', { observe: 'response' })
            .toPromise()
            .then((response) => {
                this.extractAndStoreJwt(response.headers.get('Authorization'));
                const currentUser = response.body;
                if (currentUser) {
                    this.userIdentity = currentUser;
                    this.authenticated = true;
                } else {
                    this.userIdentity = null;
                    this.authenticated = false;
                }
                this.authenticationState.next(this.userIdentity);
                return this.userIdentity;
            }).catch((err) => {
                this.userIdentity = null;
                this.authenticated = false;
                this.authenticationState.next(this.userIdentity);
                return null;
            });
    }

    isAuthenticated(): boolean {
        return this.authenticated;
    }

    getAuthenticationState(): Observable<any> {
        return this.authenticationState.asObservable();
    }

    private extractAndStoreJwt(bearerToken: String) {
        if (bearerToken && bearerToken.slice(0, 7) === 'Bearer ') {
            const jwt = bearerToken.slice(7, bearerToken.length);
            this.authServerProvider.storeAuthenticationToken(jwt, false);
        }
    }

}

export interface CurrentUser {

    id: string

    login: string

    firstName: string

    lastName: string

    email: string

    langKey: string

    authorities: Array<string>

    companyId: string

    companyName: string

}
