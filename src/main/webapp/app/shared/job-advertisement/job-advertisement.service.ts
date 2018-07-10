import { Injectable } from '@angular/core';
import { ResponseWrapper } from '../index';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import {
    CreateJobAdvertisement,
    JobAdvertisement,
    JobAdvertisementStatus
} from './job-advertisement.model';
import { createPageableURLSearchParams } from '../model/request-util';
import { JobAdvertisementCancelRequest } from './job-advertisement-cancel-request';
import {
    JobAdvertisementSearchRequest,
    JobAdvertisementSearchRequestBody
} from './job-advertisement-search-request';
import { PEAJobAdsSearchRequest } from './pea-job-ads-search-request';

@Injectable()
export class JobAdvertisementService {
    private readonly resourceUrl = 'jobadservice/api/jobAdvertisements';
    private readonly searchUrl = `${this.resourceUrl}/_search`;
    private readonly countUrl = `${this.resourceUrl}/_count`;

    constructor(
        private http: HttpClient
    ) {
    }

    save(jobPublication: CreateJobAdvertisement): Observable<ResponseWrapper> {
        return this.http.post(this.resourceUrl, jobPublication, { observe: 'response' })
            .map((resp: HttpResponse<JobAdvertisement>) => new ResponseWrapper(resp.headers, resp.body, resp.status));
    }

    searchPEAJobAds(request: PEAJobAdsSearchRequest): Observable<ResponseWrapper> {
        const params = createPageableURLSearchParams(request);

        return this.http.post(`${this.searchUrl}/pea`, request.body, { params, observe: 'response' })
            .map((resp) => new ResponseWrapper(resp.headers, resp.body, resp.status));
    }

    search(request: JobAdvertisementSearchRequest): Observable<ResponseWrapper> {
        const params = createPageableURLSearchParams(request);

        return this.http.post(this.searchUrl, request.body, { params, observe: 'response' })
            .map((resp) => new ResponseWrapper(resp.headers, resp.body, resp.status));
    }

    count(request: JobAdvertisementSearchRequestBody): Observable<number> {
        return this.http.post(this.countUrl, request, { observe: 'response' })
            .map((resp) => (<any>resp.body).totalCount);
    }

    findById(id: string): Observable<JobAdvertisement> {
        return this.http.get<JobAdvertisement>(`${this.resourceUrl}/${id}`);
    }

    findByToken(token: string): Observable<JobAdvertisement> {
        return this.http.get<JobAdvertisement>(`${this.resourceUrl}/token/${token}`);
    }

    findByFingerprint(fingerprint: string): Observable<JobAdvertisement> {
        return this.http.get<JobAdvertisement>(`${this.resourceUrl}/byFingerprint/${fingerprint}`)
    }

    cancel(jobAdCancelRequest: JobAdvertisementCancelRequest): Observable<number> {
        const { code } = jobAdCancelRequest;
        let params = new HttpParams();
        if (jobAdCancelRequest.token) {
            params = params.set('token', jobAdCancelRequest.token);
        }
        return this.http.patch(`${this.resourceUrl}/${jobAdCancelRequest.id}/cancel`,
            { code }, { params, observe: 'response' })
            .map((result) => result.status);
    }

    isJobAdvertisementCancellable(status: string | JobAdvertisementStatus): boolean {
        const statusEnum = typeof status === 'string' ? JobAdvertisementStatus[status] : status;
        return statusEnum !== JobAdvertisementStatus.INSPECTING
            && statusEnum !== JobAdvertisementStatus.REJECTED
            && statusEnum !== JobAdvertisementStatus.CANCELLED
            && statusEnum !== JobAdvertisementStatus.ARCHIVE;
    }
}
