import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../app.constants';
import { User } from './user.model';
import { createRequestOption } from '../model/request-util';
import { ResponseWrapper } from '../';

@Injectable()
export class UserService {
    private resourceUrl = SERVER_API_URL + 'api/users';
    private searchUrl = SERVER_API_URL + 'api/_search/users';

    constructor(private http: HttpClient) { }

    create(user: User): Observable<HttpResponse<User>> {
        return this.http.post<User>(this.resourceUrl, user, { observe: 'response' });
    }

    update(user: User): Observable<HttpResponse<User>> {
        return this.http.put<User>(this.resourceUrl, user, { observe: 'response' });
    }

    find(login: string): Observable<HttpResponse<User>> {
        return this.http.get<User>(`${this.resourceUrl}/${login}`, { observe: 'response' });
    }

    query(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get<User[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((resp) => this.convertResponse(resp));
    }

    search(req: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get<User[]>(this.searchUrl, { params: options, observe: 'response' })
            .map((resp) => this.convertResponse(resp));
    }

    delete(login: string): Observable<HttpResponse<any>> {
        return this.http.delete(`${this.resourceUrl}/${login}`, { observe: 'response' });
    }

    authorities(): Observable<string[]> {
        return this.http.get<string[]>(SERVER_API_URL + 'api/users/authorities');
    }

    private convertResponse(res: HttpResponse<any>): ResponseWrapper {
        return new ResponseWrapper(res.headers, res.body, res.status);
    }
}
