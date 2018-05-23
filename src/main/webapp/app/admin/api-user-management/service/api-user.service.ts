import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { createPageableURLSearchParams, ResponseWrapper } from '../../../shared';
import { HttpClient, HttpParams } from '@angular/common/http';
import { SERVER_API_URL } from '../../../app.constants';

export interface ApiUser {
    id?: string;
    password: string;
    active: boolean;
    username: string;
    email: string;
    companyName: string
    contactName: string;
    contactEmail: string;
    lastAccessDate?: Date;
}

export interface ApiUserSearchRequest {
    page: number;
    size: number;
    filter: string;
}

@Injectable()
export class ApiUserService {

    private resourceUrl = SERVER_API_URL + '/jobadservice/api/apiUsers';

    constructor(private http: HttpClient) {
    }

    search(searchRequest: ApiUserSearchRequest): Observable<ResponseWrapper> {
        const params = createPageableURLSearchParams(searchRequest);
        const body = { query: searchRequest.filter };

        return this.http.post<ResponseWrapper>(`${this.resourceUrl}/_search`, body, {
            params,
            observe: 'response'
        }).map((resp) => new ResponseWrapper(resp.headers, resp.body, resp.status));
    }

    save(apiUser: ApiUser): Observable<ApiUser> {
        return this.http.post<ApiUser>(this.resourceUrl, apiUser);
    }

    update(apiUser: ApiUser): Observable<ApiUser> {
        return this.http.put<ApiUser>(this.resourceUrl, apiUser);
    }

    toggleStatus(apiUser: ApiUser): Observable<void> {
        const body = { active: apiUser.active };
        return this.http.put<void>(`${this.resourceUrl}/${apiUser.id}/active`, body)
    }
}
