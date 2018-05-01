import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';

import { SERVER_API_URL } from '../../app.constants';
import { SystemNotification } from './system.notification.model';

@Injectable()
export class SystemNotificationService {
    private resourceUrl = SERVER_API_URL + 'api/systemNotification';

    constructor(private http: HttpClient) {
    }

    create(
        systemNotification: SystemNotification
    ): Observable<SystemNotification> {
        return this.http.post<SystemNotification>(
            this.resourceUrl,
            systemNotification
        );
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete(`${this.resourceUrl}/${id}`, {
            observe: 'response'
        });
    }

    update(
        systemNotification: SystemNotification
    ): Observable<HttpResponse<SystemNotification>> {
        return this.http.put<SystemNotification>(
            this.resourceUrl,
            systemNotification,
            { observe: 'response' }
        );
    }

    getAllSystemNotifications(): Observable<SystemNotification[]> {
        return this.http.get<SystemNotification[]>(`${this.resourceUrl}`);
    }
}
