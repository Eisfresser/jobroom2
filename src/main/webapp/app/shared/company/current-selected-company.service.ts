import { Injectable } from '@angular/core';
import { CurrentUser, Principal } from '..';
import { BehaviorSubject, Observable } from 'rxjs';
import { CompanyService } from './company.service';
import { Company } from './company.model';
import { UserInfoService } from '../user-info/user-info.service';
import { Accountability, CompanyContactTemplate } from '../user-info/user-info.model';
import { CompanyContactTemplateModel } from './company-contact-template.model';

@Injectable()
export class CurrentSelectedCompanyService {

    private accountabilitiesSubject = new BehaviorSubject<Array<Accountability>>(null);

    private selectedCompanyContactTemplateSubject = new BehaviorSubject<CompanyContactTemplateModel>(null);

    private selectedAccountabilitySubject = new BehaviorSubject<Accountability>(null);

    constructor(
        private principal: Principal,
        private companyService: CompanyService,
        private userInfoService: UserInfoService) {
        this.principal.getAuthenticationState()
            .do((currentUser: CurrentUser) => {
                if (currentUser == null) {
                    this.reset();
                }
            })
            .filter((currentUser: CurrentUser) => currentUser != null)
            .flatMap((currentUser: CurrentUser) => {
                return this.fetchAccountabilities(currentUser)
                    .flatMap((accountabilities) => {
                        const accountability = accountabilities[0];
                        if (!accountability) {
                            return Observable.empty();
                        }
                        return this.selectAccountability(accountability);
                    });
            })
            .subscribe();
    }

    public getSelectedAccountability(): Observable<Accountability> {
        return this.selectedAccountabilitySubject;
    }

    public getAccountabilities(): Observable<Array<Accountability>> {
        return this.accountabilitiesSubject;
    }

    public getSelectedCompanyContactTemplate(): Observable<CompanyContactTemplateModel> {
        return this.selectedCompanyContactTemplateSubject;
    }

    public selectAccountability(selectedAccountability: Accountability): Observable<CompanyContactTemplateModel> {
        if (selectedAccountability == null) {
            throw Error('Accountability must not be null');
        }
        return this.principal.currentUser()
            .flatMap((currentUser) => {
                return this.userInfoService.findCompanyContactTemplate(currentUser.id, selectedAccountability.companyId)
                    .catch(() => {
                        return this.loadDefaultTemplate(currentUser, selectedAccountability.companyExternalId);
                    })
                    .map((companyContactTemplate: CompanyContactTemplate) => {
                        return new CompanyContactTemplateModel(companyContactTemplate, currentUser);
                    })
                    .do((model: CompanyContactTemplateModel) => {
                        this.selectedCompanyContactTemplateSubject.next(model);
                        this.selectedAccountabilitySubject.next(selectedAccountability)
                    });
            });
    }

    public reset() {
        this.selectedCompanyContactTemplateSubject.next(null);
        this.selectedAccountabilitySubject.next(null);
        this.accountabilitiesSubject.next(null);
    }

    public reloadCurrentSelection(): Observable<CompanyContactTemplateModel> {
        return this.selectAccountability(this.selectedAccountabilitySubject.getValue());
    }

    private fetchAccountabilities(currentUser: CurrentUser) {
        return this.userInfoService.findAccountabilities(currentUser.id)
            .do((accountabilities) => {
                this.accountabilitiesSubject.next(accountabilities);
            });
    }

    private loadDefaultTemplate(currentUser: CurrentUser, companyExternalId: string): Observable<CompanyContactTemplate> {
        return this.companyService.findByExternalId(companyExternalId)
            .map((company: Company) => {
                return {
                    companyId: company.id,
                    companyName: company.name,
                    companyStreet: company.street,
                    companyZipCode: company.zipCode,
                    companyCity: company.city,
                    email: currentUser.email
                };
            });
    }

}
