import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../app.constants';
import { UserInfoComponent } from './user-info.component';

@Injectable()
export class UserInfoService {
    public static USER_INFO_URL = SERVER_API_URL + 'api/user-info/';
    public static USER_INFO_ROLE_POSTFIX = SERVER_API_URL + '/roles';

    constructor(private http: HttpClient) {
    }
    public loadUserInfoByMail(email): Observable<any> {
        return this.http.get(UserInfoService.USER_INFO_URL + email, { observe: 'response' });
    }

    public loadUserRolesByMail(email): Observable<any> {
        return this.http.get(UserInfoService.USER_INFO_URL + email + UserInfoService.USER_INFO_ROLE_POSTFIX, { observe: 'response' });
    }

    public unregisterUser(email, deleteParams): Observable<any> {
        return this.http.delete(UserInfoService.USER_INFO_URL + email, { params: deleteParams, observe: 'response' })
    }
}
