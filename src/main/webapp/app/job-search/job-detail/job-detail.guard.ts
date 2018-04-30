import {
    ActivatedRouteSnapshot,
    CanActivate,
    Router,
    RouterStateSnapshot
} from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { JobSearchState } from '../state-management/state/job-search.state';
import { Store } from '@ngrx/store';
import { JobDetailLoadedAction } from '../state-management/actions/job-search.actions';
import { Injectable } from '@angular/core';
import { JobAdvertisementService } from '../../shared/job-advertisement/job-advertisement.service';

@Injectable()
export class JobDetailGuard implements CanActivate {

    constructor(private store: Store<JobSearchState>,
                private router: Router,
                private jobAdvertisementService: JobAdvertisementService) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
        const id = route.params['id'];

        return this.jobAdvertisementService.findById(id)
            .catch((error) => {
                this.router.navigate(['/job-search']);
                return Observable.of(null);
            })
            .do((job) => this.store.dispatch(new JobDetailLoadedAction(job)))
            .map((job) => !!job);
    }
}
