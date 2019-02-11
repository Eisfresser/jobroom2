import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { TranslateService } from '@ngx-translate/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { SERVER_API_URL } from '../../app.constants';

const REFERENCESERVICE_URL =  SERVER_API_URL + 'referenceservice/api/';
const JOB_CENTER_URL = REFERENCESERVICE_URL + 'job-centers';

export class Address {
    constructor(public name: string,
                public street: string,
                public houseNumber: string,
                public zipCode: string,
                public city: string) {
    }
}

export class JobCenter {
    constructor(public id: string,
                public code: string,
                public email: string,
                public phone: string,
                public fax: string,
                public address: Address) {
    }
}

@Injectable()
export class ReferenceService {

    constructor(private http: HttpClient) {
    }

    resolveJobCenter(code: string, lang: string): Observable<JobCenter> {
        const params = new HttpParams()
            .set('code', code)
            .set('language', lang);

        return this.http.get<JobCenter>(JOB_CENTER_URL, { params });
    }
}
