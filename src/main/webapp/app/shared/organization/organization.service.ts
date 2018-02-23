import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Rx';
import { SERVER_API_URL } from '../../app.constants';

import { Organization, OrganizationAutocomplete } from './organization.model';
import { createRequestOption, ResponseWrapper } from '../';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';

@Injectable()
export class OrganizationService {

    private resourceUrl = SERVER_API_URL + 'api/organizations';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/organizations';

    constructor(private http: HttpClient) {
    }

    create(organization: Organization): Observable<Organization> {
        const copy = this.convert(organization);
        return this.http.post<Organization>(this.resourceUrl, copy);
    }

    update(organization: Organization): Observable<Organization> {
        const copy = this.convert(organization);
        return this.http.put<Organization>(this.resourceUrl, copy);
    }

    find(id: number): Observable<Organization> {
        return this.http.get<Organization>(`${this.resourceUrl}/${id}`);
    }

    findByExternalId(id: string): Observable<Organization> {
        return this.http.get<Organization>(`${this.resourceUrl}/externalId/${id}`);
    }

    query(req?: any): Observable<ResponseWrapper> {
        const params = createRequestOption(req);
        return this.http.get<Organization[]>(this.resourceUrl, { params, observe: 'response' })
            .map((res: HttpResponse<Organization[]>) => this.convertResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<ResponseWrapper> {
        const params = createRequestOption(req);
        return this.http.get(this.resourceSearchUrl, { params })
            .map((res: any) => this.convertResponse(res));
    }

    suggest(prefix: string, resultSize: number): Observable<OrganizationAutocomplete> {
        const params = new HttpParams()
            .set('prefix', prefix)
            .set('resultSize', resultSize.toString());

        return this.http.get<OrganizationAutocomplete>(`${this.resourceSearchUrl}/suggest`, { params });
    }

    private convertResponse(res: HttpResponse<Organization>): ResponseWrapper {
        return new ResponseWrapper(res.headers, res.body, res.status);
    }

    /**
     * Convert a Organization to a JSON which can be sent to the server.
     */
    private convert(organization: Organization): Organization {
        return Object.assign({}, organization);
    }
}
