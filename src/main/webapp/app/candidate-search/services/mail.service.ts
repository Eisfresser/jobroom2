import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface EmailContent {
    to: string;
    subject: string;
    body: string;
    phone?: string;
    email?: string;
    company?: Company
}

export interface Company {
    name: string;
    contactPerson: string;
    street: string;
    houseNumber: string;
    zipCode: string;
    city: string;
    country: string;
}

@Injectable()
export class MailService {

    constructor(private http: HttpClient) {
    }

    senAnonymousContactMessage(emailContent: EmailContent) {
        return this.http.post('/api/messages/send-anonymous-message', emailContent);
    }
}
