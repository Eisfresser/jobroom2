import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Rx';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class ElasticsearchReindexService {

    private readonly documentUrls = {
        'users': '',
        'jobs': 'jobservice/',
        'candidates': 'candidateservice/',
        'reference-data': 'referenceservice/',
    };

    constructor(
      private http: HttpClient
    ) { }

    reindex(document: string): Observable<void> {
        let urls = [this.documentUrls[document]];
        if (document === 'all') {
            urls = Object.keys(this.documentUrls)
                .map((key) => this.documentUrls[key]);
        }

        return Observable.from(urls)
            .map((url) => this.http.post(`${url}api/elasticsearch/index`, {}))
            .zipAll();
    }
}
