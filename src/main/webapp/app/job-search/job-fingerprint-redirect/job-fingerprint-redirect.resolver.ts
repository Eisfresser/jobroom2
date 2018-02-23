import {
    ActivatedRouteSnapshot,
    Resolve,
    Router,
    RouterStateSnapshot
} from '@angular/router';
import { Injectable } from '@angular/core';
import { JobService } from '../services';

@Injectable()
export class JobFingerprintRedirectResolver implements Resolve<void> {

    constructor(private jobService: JobService, private router: Router) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): void {
        this.jobService.findByExternalId(route.queryParams['externalId'])
            .subscribe((job) => {
                if (job !== null) {
                    this.router.navigate(['/job-detail/', job.id]);
                }
            });
    }
}
