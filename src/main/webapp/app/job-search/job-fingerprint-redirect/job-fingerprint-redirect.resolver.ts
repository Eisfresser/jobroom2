import {
    ActivatedRouteSnapshot,
    Resolve,
    Router,
    RouterStateSnapshot
} from '@angular/router';
import { Injectable } from '@angular/core';
import { JobAdvertisementService } from '../../shared/job-advertisement/job-advertisement.service';

@Injectable()
export class JobFingerprintRedirectResolver implements Resolve<void> {

    constructor(private jobAdvertisementService: JobAdvertisementService, private router: Router) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): void {
        this.jobAdvertisementService.findByFingerprint(route.queryParams['externalId'])
            .subscribe((job) => {
                if (job !== null) {
                    this.router.navigate(['/job-detail/', job.id]);
                }
            });
    }
}
