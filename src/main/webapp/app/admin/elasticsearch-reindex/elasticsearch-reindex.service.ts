import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Rx';
import { HttpClient } from '@angular/common/http';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class ElasticsearchReindexService {

    private readonly documentUrls = {
        'users': SERVER_API_URL + 'api/elasticsearch/index',
        'jobs': SERVER_API_URL + 'jobadservice/api/elasticsearch/index',
        'candidates': SERVER_API_URL + 'candidateservice/api/elasticsearch/index',
        'reference-data': SERVER_API_URL + 'referenceservice/api/elasticsearch/index',
    };

    constructor(
        private http: HttpClient
    ) {
    }

    reindex(document: string): Observable<void> {
        let urls = [this.documentUrls[document]];
        if (document === 'all') {
            urls = Object.keys(this.documentUrls)
                .map((key) => this.documentUrls[key]);
        }

        return Observable.from(urls)
            .map((url) => this.http.post(url, {}))
            .zipAll();
    }
}
