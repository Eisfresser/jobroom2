import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';

import { SERVER_API_URL } from '../../app.constants';
import { BlacklistedAgent, BlacklistedAgentStatus } from './blacklisted.agent.model';

@Injectable()
export class BlacklistedAgentService {

    private resourceUrl = SERVER_API_URL + '/api/blacklisted-agent/';

    constructor(private http: HttpClient) {
    }

    getAllBlacklistedAgents(): Observable<BlacklistedAgent[]> {
        return this.http.get<BlacklistedAgent[]>(this.resourceUrl);
    }

    changeStatus(agent: BlacklistedAgent, status: BlacklistedAgentStatus) {
        return this.http.put<void>(this.resourceUrl + `${agent.id}/status`, JSON.stringify(status), {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })});
    }
}
