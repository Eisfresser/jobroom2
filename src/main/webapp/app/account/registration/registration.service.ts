import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../app.constants';
import { JobSeekerDetails } from './jobseeker/jobseeker-details.model';
import { LoginData } from './existing-pav/login-data.model';

@Injectable()
export class RegistrationService {
    private registerJobseekerUrl = SERVER_API_URL + 'api/registerJobseeker';
    private requestEmployerAccessCodeUrl = SERVER_API_URL + 'api/requestEmployerAccessCode';
    private getCompanyByUidUrl = SERVER_API_URL + 'api/getCompanyByUid';
    private requestAgentAccessCodeUrl = SERVER_API_URL + 'api/requestAgentAccessCode';
    private registerEmployerOrAgentUrl = SERVER_API_URL + 'api/registerEmployerOrAgent';
    private registerExistingAgentUrl = SERVER_API_URL + 'api/registerExistingAgent';

    constructor(private http: HttpClient) {
    }

    registerJobSeeker(jobSeekerDetails: JobSeekerDetails): Observable<any> {
        return this.http.post(this.registerJobseekerUrl, jobSeekerDetails, { observe: 'response' });
    }

    requestEmployerAccessCode(uid: number): Observable<any> {
        return this.http.post(this.requestEmployerAccessCodeUrl, uid, { observe: 'response' });
    }

    getCompanyByUid(uid: number): Observable<any> {
        return this.http.post(this.getCompanyByUidUrl, uid, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        });
    }

    requestAgentAccessCode(avgId: string): Observable<any> {
        return this.http.post(this.requestAgentAccessCodeUrl, avgId, { observe: 'response' });
    }

    registerEmployerOrAgent(accessCode: string): Observable<any> {
        return this.http.post(this.registerEmployerOrAgentUrl, accessCode, { observe: 'response' });
    }

    registerExistingAgent(loginData: LoginData): Observable<any> {
        return this.http.post(this.registerExistingAgentUrl, loginData, { observe: 'response' });
    }

}
