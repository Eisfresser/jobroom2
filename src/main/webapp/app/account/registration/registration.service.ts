import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../app.constants';
import { JobSeekerDetails } from './jobseeker/jobseeker-details.model';

const REGISTER_JOB_SEEKER_URL = SERVER_API_URL + 'api/registerJobseeker';
const REQUEST_COMPANY_ACCESS_CODE_URL = SERVER_API_URL + 'api/requestEmployerAccessCode';
const REQUEST_AGENT_ACCESS_CODE_URL = SERVER_API_URL + 'api/requestAgentAccessCode';
const COMPANY_BY_UID_URL = SERVER_API_URL + 'api/getCompanyByUid';
const REGISTER_BY_ACCESS_CODE = SERVER_API_URL + 'api/registerEmployerOrAgent';

@Injectable()
export class RegistrationService {

    constructor(private http: HttpClient) {
    }

    registerJobSeeker(jobSeekerDetails: JobSeekerDetails): Observable<any> {
        return this.http.post(REGISTER_JOB_SEEKER_URL, jobSeekerDetails, { observe: 'response' });
    }

    requestEmployerAccessCode(uid: number): Observable<any> {
        return this.http.post(REQUEST_COMPANY_ACCESS_CODE_URL, uid, { observe: 'response' });
    }

    getCompanyByUid(uid: number): Observable<any> {
        return this.http.post(COMPANY_BY_UID_URL, uid, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        });
    }

    requestAgentAccessCode(avgId: string): Observable<any> {
        return this.http.post(REQUEST_AGENT_ACCESS_CODE_URL, avgId, { observe: 'response' });
    }

    registerEmployerOrAgent(accessCode: string): Observable<any> {
        return this.http.post(REGISTER_BY_ACCESS_CODE, accessCode, { observe: 'response' });
    }

}
