import { Injectable } from '@angular/core';
import { ResponseWrapper } from '../index';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { CreateJobAdvertisement, JobAdvertisement } from './job-advertisement.model';

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
}
