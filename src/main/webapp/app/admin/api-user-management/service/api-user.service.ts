import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { createPageableURLSearchParams, ResponseWrapper } from '../../../shared';
import { HttpClient } from '@angular/common/http';
import { SERVER_API_URL } from '../../../app.constants';

export interface ApiUser {
    id?: string;
    username: string;
    password?: string;
    companyEmail: string;
    companyName: string;
    technicalContactName: string;
    technicalContactEmail: string;
    active?: boolean;
    lastAccessDate?: Date;
}

export interface ApiUserSearchRequest {
    page: number;
    size: number;
    query: string;
    sort: string
}

export interface ApiUserUpdatePasswordRequest {
    password: string
}

@Injectable()
export class ApiUserService {

    private resourceUrl = SERVER_API_URL + '/jobadservice/api/apiUsers';

    constructor(private http: HttpClient) {
    }

    search(searchRequest: ApiUserSearchRequest): Observable<ResponseWrapper> {
        const params = createPageableURLSearchParams(searchRequest);
        const body = { query: searchRequest.query };

        return this.http.post<ResponseWrapper>(`${this.resourceUrl}/_search`, body, {
            params,
            observe: 'response'
        }).map((resp) => new ResponseWrapper(resp.headers, resp.body, resp.status));
    }

    save(apiUser: ApiUser): Observable<ApiUser> {
        return this.http.post<ApiUser>(this.resourceUrl, apiUser);
    }

    update(apiUser: ApiUser): Observable<ApiUser> {
        const apiUserId = apiUser.id;
        apiUser = Object.assign({}, apiUser, { id: undefined });
        return this.http.put<ApiUser>(`${this.resourceUrl}/${apiUserId}`, apiUser);
    }

    toggleStatus(apiUser: ApiUser): Observable<void> {
        const body = { active: apiUser.active };
        return this.http.put<void>(`${this.resourceUrl}/${apiUser.id}/active`, body)
    }

    updatePassword(id: string, updatePasswordRequest: ApiUserUpdatePasswordRequest): Observable<void> {
        return this.http.put<void>(`${this.resourceUrl}/${id}/password`, updatePasswordRequest)
    }

    generatePassword(): string {
        return Math.random().toString(36).slice(-10);
    }
}
