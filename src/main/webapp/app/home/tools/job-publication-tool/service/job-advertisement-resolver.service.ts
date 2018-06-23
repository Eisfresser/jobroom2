import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { JobAdvertisementService } from '../../../../shared/job-advertisement/job-advertisement.service';
import { JobAdvertisement } from '../../../../shared/job-advertisement/job-advertisement.model';
import { OccupationPresentationService } from '../../../../shared/reference-service';
import { TranslateService } from '@ngx-translate/core';
import { Principal } from '../../../../shared';

@Injectable()
export class JobAdvertisementResolverService implements Resolve<JobAdvertisement> {
    constructor(private jobAdvertisementService: JobAdvertisementService,
                private occupationPresentationService: OccupationPresentationService,
                private translateService: TranslateService,
                private principal: Principal) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<JobAdvertisement> {
        const jobPublicationId = route.paramMap.get('jobPublicationId');
        const token = route.paramMap.get('token');
        return this.principal.currentUser()
            .flatMap(() => this.principal.isCompanyOrAgent())
            .flatMap((canActivate) => {
                if (!canActivate && !token) {
                    return Observable.of(null);
                } else if (!jobPublicationId) {
                    return Observable.of(null);
                }
                const job$ = token ? this.jobAdvertisementService.findByToken(token)
                    : this.jobAdvertisementService.findById(jobPublicationId);

                return job$
                    .flatMap(this.addOccupationLabel.bind(this));
            });
    }

    private addOccupationLabel(jobAd: JobAdvertisement): Observable<JobAdvertisement> {
        const occupations = jobAd.jobContent.occupations;
        if (occupations && occupations.length) {
            const avamOccupationCode = occupations[0].avamOccupationCode;
            const occupation$ = this.occupationPresentationService.findOccupationLabelsByAvamCode('avam:' + avamOccupationCode,
                this.translateService.currentLang);
            return occupation$.map((occupation) => {
                occupations[0].occupationLabel = occupation.default;
                return jobAd;
            });
        }

        return Observable.of(jobAd);
    }
}
