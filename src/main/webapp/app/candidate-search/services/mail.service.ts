import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { CompanyContactTemplateModel } from '../../shared/company/company-contact-template.model';

export class EmailContent {
    candidateId: string;
    subject: string;
    personalMessage: string;
    companyName: string;
    phone?: string;
    email?: string;
    company?: Company;

    constructor(candidateId: string, subject: string, companyContactTemplateModel: CompanyContactTemplateModel) {
        this.candidateId = candidateId;
        this.subject = subject;
        this.personalMessage = null;
        this.companyName = companyContactTemplateModel.companyName;
        this.phone = companyContactTemplateModel.phone;
        this.email = companyContactTemplateModel.email;
        this.company = {
            name: companyContactTemplateModel.companyName,
            contactPerson: companyContactTemplateModel.firstName + ' ' + companyContactTemplateModel.lastName,
            street: companyContactTemplateModel.companyStreet,
            houseNumber: companyContactTemplateModel.companyHouseNr,
            zipCode: companyContactTemplateModel.companyZipCode,
            city: companyContactTemplateModel.companyCity,
            country: null
        }
    }
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
