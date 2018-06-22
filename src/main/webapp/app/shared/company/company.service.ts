import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Rx';
import { SERVER_API_URL } from '../../app.constants';

import { HttpClient, HttpParams } from '@angular/common/http';
import { Company } from './company.model';

@Injectable()
export class CompanyService {

    private resourceUrl = SERVER_API_URL + 'api/company';

    constructor(private http: HttpClient) {
    }

    findByExternalId(id: string): Observable<Company> {
        return this.http.get<Company>(`${this.resourceUrl}/find/by-external-id`,
            { params: new HttpParams().set('id', id) });
    }

}
