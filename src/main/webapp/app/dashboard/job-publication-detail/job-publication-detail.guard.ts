import { Injectable } from '@angular/core';
import {
    ActivatedRouteSnapshot,
    CanActivate,
    Router,
    RouterStateSnapshot
} from '@angular/router';
import { Observable } from 'rxjs/Observable';
import {
    getLoadingStatus,
    JobPublicationDetailState,
    LoadingStatus
} from '../state-management/state/job-publication-detail.state';
import { Store } from '@ngrx/store';
import { LoadJobAdvertisementAction } from '../state-management/actions/job-publication-detail.actions';
import { Principal } from '../../shared';

@Injectable()
export class JobPublicationDetailGuard implements CanActivate {

    constructor(private store: Store<JobPublicationDetailState>,
                private router: Router,
                private principal: Principal) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
        const id = route.paramMap.get('id');
        const token = route.queryParamMap.get('token');

        return this.principal.currentUser()
            .flatMap(() => this.principal.isCompanyOrAgent())
            .flatMap((canActivate) => {
                if (!canActivate && !token) {
                    this.router.navigate(['/error']);

                    return Observable.of(false);
                }

                this.store.dispatch(new LoadJobAdvertisementAction({ id, token }));

                return this.store.select(getLoadingStatus)
                    .filter((status: LoadingStatus) => status !== LoadingStatus.INITIAL)
                    .take(1)
                    .switchMap((status: LoadingStatus) => {
                        if (status === LoadingStatus.OK) {
                            return Observable.of(true);
                        } else {
                            this.router.navigate(['/error']);

                            return Observable.of(false);
                        }
                    });
            });
    }
}
