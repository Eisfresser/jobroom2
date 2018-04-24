import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../app.constants';
import { SystemNotification } from './system.notification.model';
import { createRequestOption } from '../model/request-util';
import { ResponseWrapper } from '../';

@Injectable()
export class SystemNotificationService {
    private resourceUrl = SERVER_API_URL + 'api/systemnotifications';
    private searchUrl = SERVER_API_URL + 'api/_search/systemnotifications';
    systemNotifications = new Array(
        new SystemNotification(
            1,
            'Systemmitteilung 1',
            'Aufgrund technischer Probleme werden aktuell keine Stellen aktualisiert. Bis wir diese Probleme behoben haben, danken wir Ihnen für Ihr Verständnis',
            'SYSTEM',
            '01-01-2001 14:00',
            '01-01-2001 16:00',
            true
        ),
        new SystemNotification(
            2,
            'Systemmitteilung 2',
            'Aufgrund technischer Probleme werden aktuell keine Stellen aktualisiert. Bis wir diese Probleme behoben haben, danken wir Ihnen für Ihr Verständnis',
            'SYSTEM',
            '01-01-2001 14:00',
            '01-01-2001 16:00',
            false
        ),
        new SystemNotification(
            3,
            'Systemmitteilung 3',
            'Aufgrund technischer Probleme werden aktuell keine Stellen aktualisiert. Bis wir diese Probleme behoben haben, danken wir Ihnen für Ihr Verständnis',
            'SYSTEM',
            '01-01-2001 14:00',
            '01-01-2001 16:00',
            true
        ),
        new SystemNotification(
            4,
            'Systemmitteilung 4',
            'Aufgrund technischer Probleme werden aktuell keine Stellen aktualisiert. Bis wir diese Probleme behoben haben, danken wir Ihnen für Ihr Verständnis',
            'SYSTEM',
            '01-01-2001 14:00',
            '01-01-2001 16:00',
            false
        ),
        new SystemNotification(
            5,
            'Systemmitteilung 5',
            'Aufgrund technischer Probleme werden aktuell keine Stellen aktualisiert. Bis wir diese Probleme behoben haben, danken wir Ihnen für Ihr Verständnis',
            'SYSTEM',
            '01-01-2001 14:00',
            '01-01-2001 16:00',
            true
        ));

    constructor(private http: HttpClient) {
    }

    create(systemNotification: SystemNotification): Observable<HttpResponse<SystemNotification>> {
        return this.http.post<SystemNotification>(this.resourceUrl, systemNotification, { observe: 'response' });
    }

    update(systemNotification: SystemNotification): Observable<HttpResponse<SystemNotification>> {
        return this.http.put<SystemNotification>(this.resourceUrl, systemNotification, { observe: 'response' });
    }

    search(req: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get<SystemNotification[]>(this.searchUrl, { params: options, observe: 'response' })
            .map((resp) => this.convertResponse(resp));
    }

    getAllSystemNotifications(): SystemNotification[] {
        return this.systemNotifications;
    }

    private convertResponse(res: HttpResponse<any>): ResponseWrapper {
        return new ResponseWrapper(res.headers, res.body, res.status);
    }

}
