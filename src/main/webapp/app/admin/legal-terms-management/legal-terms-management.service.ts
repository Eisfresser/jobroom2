import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LegalTerms } from './legal-terms.model';
import { SERVER_API_URL } from '../../app.constants';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class LegalTermsManagementService {

    private resourceUrl = SERVER_API_URL + '/api/legal-terms';

    constructor(private http: HttpClient) {
    }

    getAllLegalTermsEntries(): Observable<LegalTerms[]> {
        return this.http.get<LegalTerms[]>(this.resourceUrl);
    }

    addLegalTermsEntry(legalTerms: LegalTerms): Observable<void> {
        return this.http.post<void>(this.resourceUrl, JSON.stringify(legalTerms), {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })});
    }

    update(legalTerms: LegalTerms): Observable<void> {
        return this.http.put<void>(this.resourceUrl + `/${legalTerms.id}`, legalTerms, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        });
    }

    delete(id: String): Observable<HttpResponse<Object>> {
        return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
}
