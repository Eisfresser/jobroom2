import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs/Observable';
import { Principal, ResponseWrapper } from '../';
import { createPageableURLSearchParams } from '../model/request-util';
import { JobCancelRequest } from './job-publication-cancel-request';
import { JobPublicationSearchRequest } from './job-publication-search-request';
import { CancellationReason, JobPublication, Status } from './job-publication.model';

@Injectable()
export class JobPublicationService {

    private readonly resourceUrl = 'jobpublicationservice/api/jobPublications';
    private readonly searchUrl = 'jobpublicationservice/api/_search/jobPublications';

    private static createCancelJobPublicationParams(jobCancelRequest: JobCancelRequest) {
        return new HttpParams()
            .set('accessToken', jobCancelRequest.accessToken)
            .set('cancellationReason', CancellationReason[jobCancelRequest.cancellationReason]);
    }

    constructor(private http: HttpClient,
                private principal: Principal,
                private translateService: TranslateService) {
    }

    private getJobPublicationLocale(): Observable<string> {
        return this.principal.isAuthenticated()
            ? Observable.fromPromise(this.principal.identity())
                .map((identity) => identity.langKey)
            : Observable.of(this.translateService.currentLang);
    }

    save(jobPublication: JobPublication): Observable<ResponseWrapper> {
        return this.getJobPublicationLocale()
            .map((locale) => Object.assign(jobPublication, { locale }))
            .flatMap((body) => this.http.post(this.resourceUrl, body, { observe: 'response' })
                .map((resp: HttpResponse<any>) => new ResponseWrapper(resp.headers, resp.body, resp.status)));
    }

    search(request: JobPublicationSearchRequest): Observable<ResponseWrapper> {
        const params = createPageableURLSearchParams(request);

        return this.http.post(this.searchUrl, request, { params, observe: 'response' })
            .map((resp) => new ResponseWrapper(resp.headers, resp.body, resp.status));
    }

    findByIdAndAccessToken(id: string, accessToken: string): Observable<JobPublication> {
        const params = new HttpParams()
            .set('accessToken', accessToken);

        return this.http.get<JobPublication>(`${this.resourceUrl}/${id}`, { params });
    }

    cancelJobPublication(jobCancelRequest: JobCancelRequest): Observable<number> {
        const params = JobPublicationService.createCancelJobPublicationParams(jobCancelRequest);
        return this.http.post(`${this.resourceUrl}/${jobCancelRequest.id}/cancel`, {}, { params, observe: 'response' })
            .map((result) => result.status);
    }

    isJobPublicationCancellable(status: string | Status): boolean {
        const statusEnum = typeof status === 'string' ? Status[status] : status;
        return statusEnum !== Status.DISMISSED && statusEnum !== Status.UNSUBSCRIBED;
    }
}
