import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';

import { SERVER_API_URL } from '../../app.constants';
import { SystemNotification } from './system.notification.model';
import { catchError } from 'rxjs/operators';

@Injectable()
export class SystemNotificationService {
    private resourceUrl = SERVER_API_URL + 'api/systemNotification';

    constructor(private http: HttpClient) {
    }

    createSystemNotification(systemNotificationToCreate: SystemNotification): Observable<SystemNotification> {
        return this.http.post<SystemNotification>(this.resourceUrl, systemNotificationToCreate)
            .pipe(catchError((error: any) => Observable.throw(error.json())));
    }

    deleteSystemNotification(systemNotificationToDelete: SystemNotification): Observable<SystemNotification> {
        return this.http
            .delete<SystemNotification>(`${this.resourceUrl}/${systemNotificationToDelete.id}`)
            .pipe(catchError((error: any) => Observable.throw(error.json())));
    }

    updateSystemNotification(systemNotificationToUpdate: SystemNotification): Observable<SystemNotification> {
        return this.http.patch<SystemNotification>(this.resourceUrl, systemNotificationToUpdate)
            .pipe(catchError((error: any) => Observable.throw(error.json())));
    }

    getAllSystemNotifications(): Observable<SystemNotification[]> {
        return this.http.get<SystemNotification[]>(`${this.resourceUrl}`)
            .pipe(catchError((error: any) => Observable.throw(error.json())));
    }
}
