import { ChangeDetectionStrategy, Component } from '@angular/core';
import {
    PEADashboardState,
    getJobAdvertisementFilter,
    getJobAdvertisements,
    getJobAdvertisementsPage,
    getJobAdvertisementsTotalCount,
    JobAdvertisementFilter
} from './state-management/state/pea-dashboard.state';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import {
    FilterJobAdvertisementsDashboardAction,
    LoadNextJobAdvertisementsDashboardPageAction
} from './state-management/actions/pea-dashboard.actions';
import { JobAdvertisement } from '../shared/job-advertisement/job-advertisement.model';

@Component({
    selector: 'jr2-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: [],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent {
    jobAdvertisementFilter$: Observable<JobAdvertisementFilter>;
    jobAdvertisements$: Observable<JobAdvertisement[]>;
    jobAdvertisementsTotalCount$: Observable<number>;
    jobAdvertisementsPage$: Observable<number>;

    constructor(private store: Store<PEADashboardState>) {
        this.jobAdvertisementFilter$ = store.select(getJobAdvertisementFilter);
        this.jobAdvertisements$ = store.select(getJobAdvertisements);
        this.jobAdvertisementsTotalCount$ = store.select(getJobAdvertisementsTotalCount);
        this.jobAdvertisementsPage$ = store.select(getJobAdvertisementsPage);
    }

    filterJobAdvertisements(jobAdvertisementFilter: JobAdvertisementFilter): void {
        this.store.dispatch(new FilterJobAdvertisementsDashboardAction(jobAdvertisementFilter));
    }

    changeJobAdvertisementsPage(page): void {
        this.store.dispatch(new LoadNextJobAdvertisementsDashboardPageAction({ page: page - 1 }));
    }
}
