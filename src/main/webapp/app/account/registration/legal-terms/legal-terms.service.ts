import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { TranslateService } from '@ngx-translate/core';
import { SERVER_API_URL } from '../../../app.constants';

@Injectable()
export class LegalTermsService {

    constructor(private http: HttpClient,
                private translateService: TranslateService) {
    }

    getCurrentLegalTermsUrl(): Observable<string> {
        const languageToLinkMap = {
            en: 'linkEn',
            de: 'linkDe',
            fr: 'linkFr',
            it: 'linkIr',
        };

        const currentLanguage$ = this.translateService.onLangChange
            .map((langChange) => langChange.lang.toLowerCase())
            .startWith(this.translateService.currentLang);

        return this.http.get<LegalTermsDto>(SERVER_API_URL + 'api/legal-terms/current')
            .combineLatest(currentLanguage$)
            .map(([legalTerms, currentLang]) => legalTerms[languageToLinkMap[currentLang]]);
    }
}

interface LegalTermsDto {
    id: string;
    effectiveAt: Date;
    linkEn: string;
    linkDe: string;
    linkFr: string;
    linkIt: string;
}
