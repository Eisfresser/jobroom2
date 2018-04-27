import { Injectable } from '@angular/core';
import { ResponseWrapper } from '../index';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { CreateJobAdvertisement, JobAdvertisement, JobAdvertisementStatus } from './job-advertisement.model';
import { JobPublicationSearchRequest } from '../job-publication/job-publication-search-request';
import { createPageableURLSearchParams } from '../model/request-util';
import { JobCancelRequest } from '../job-publication/job-publication-cancel-request';

@Injectable()
export class JobAdvertisementService {
    private readonly resourceUrl = 'jobadservice/api/jobAdvertisement';

    constructor(
        private http: HttpClient
    ) {
    }

    save(jobPublication: CreateJobAdvertisement): Observable<ResponseWrapper> {
        return this.http.post(this.resourceUrl, jobPublication, { observe: 'response' })
            .map((resp: HttpResponse<JobAdvertisement>) => new ResponseWrapper(resp.headers, resp.body, resp.status));
    }

    // TODO: add search params
    search(request: JobPublicationSearchRequest): Observable<ResponseWrapper> {
        const params = createPageableURLSearchParams(request);

        return this.http.get(this.resourceUrl, { params, observe: 'response' })
            .map((resp) => new ResponseWrapper(resp.headers, resp.body, resp.status));
    }

    findById(id: string): Observable<JobAdvertisement> {
        return this.http.get<JobAdvertisement>(`${this.resourceUrl}/${id}`);
    }

    // TODO: update
    cancelJobPublication(jobCancelRequest: JobCancelRequest): Observable<number> {
        return this.http.patch(`${this.resourceUrl}/${jobCancelRequest.id}/cancel`, {}, { observe: 'response' })
            .map((result) => result.status);
    }

    isJobAdvertisementCancellable(status: string | JobAdvertisementStatus): boolean {
        const statusEnum = typeof status === 'string' ? JobAdvertisementStatus[status] : status;
        return statusEnum !== JobAdvertisementStatus.REJECTED
            && statusEnum !== JobAdvertisementStatus.CANCELLED
            && statusEnum !== JobAdvertisementStatus.ARCHIVE;
    }
}
