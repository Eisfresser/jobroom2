import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../app.constants';
import { Accountability, CompanyContactTemplate } from './user-info.model';

@Injectable()
export class UserInfoService {

    public static USER_INFO_URL = SERVER_API_URL + 'api/user-info/';

    public static USER_INFO_ROLE_POSTFIX = SERVER_API_URL + '/roles';

    constructor(private http: HttpClient) {
    }

    public loadUserInfoByMail(email): Observable<any> {
        const params = new HttpParams().set('eMail', email);
        return this.http.get(UserInfoService.USER_INFO_URL, {
            params: params,
            observe: 'response'
        });
    }

    public loadUserRoles(userInfoId: string): Observable<any> {
        return this.http.get(UserInfoService.USER_INFO_URL + userInfoId + UserInfoService.USER_INFO_ROLE_POSTFIX, { observe: 'response' });
    }

    public unregisterUser(email, deleteParams): Observable<any> {
        const params = new HttpParams()
            .set('email', email)
            .set('deleteParams', deleteParams);
        return this.http.delete(UserInfoService.USER_INFO_URL, {
            params: params,
            observe: 'response'
        })
    }

    public findAccountabilities(userId: string): Observable<Array<Accountability>> {
        return this.http.get<Array<Accountability>>(`${UserInfoService.USER_INFO_URL}${userId}/accountabilities`);
    }

    public findCompanyContactTemplate(userId: string, companyId: string): Observable<CompanyContactTemplate> {
        return this.http.get<CompanyContactTemplate>(`${UserInfoService.USER_INFO_URL}${userId}/company-contact-template/${companyId}`);
    }

    public createCompanyContactTemplate(userId: string, companyContactTemplate: CompanyContactTemplate): Observable<void> {
        return this.http.post<void>(`${UserInfoService.USER_INFO_URL}${userId}/company-contact-templates`, companyContactTemplate);
    }
}
