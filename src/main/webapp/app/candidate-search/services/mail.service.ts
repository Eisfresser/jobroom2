import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

export interface EmailContent {
    candidateId: string;
    subject: string;
    body: string;
    companyName: string;
    phone?: string;
    email?: string;
    company?: Company
}

export interface Company {
    name?: string;
    contactPerson?: string;
    street?: string;
    houseNumber?: string;
    zipCode?: string;
    city?: string;
    country?: string;
}

@Injectable()
export class MailService {

    constructor(private http: HttpClient) {
    }

    sendAnonymousContactMessage(emailContent: EmailContent): Observable<void> {
        return this.http.post<void>('/api/messages/send-anonymous-message', emailContent)
    }
}
