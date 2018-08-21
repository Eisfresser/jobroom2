import { Injectable } from '@angular/core';
import { Actions, Effect } from '@ngrx/effects';
import { Observable } from 'rxjs/Observable';
import { Action, Store } from '@ngrx/store';
import {
    CancellationSucceededAction,
    FILTER_JOB_ADVERTISEMENTS_DASHBOARD,
    FilterJobAdvertisementsDashboardAction,
    JobAdvertisementsLoadedAction,
    JobAdvertisementsLoadErrorAction,
    LOAD_NEXT_JOB_ADVERTISEMENTS_DASHBOARD_PAGE,
    LoadNextJobAdvertisementsDashboardPageAction,
    SUBMIT_CANCELLATION,
    SubmitCancellationAction
} from '../actions/pea-dashboard.actions';
import { ITEMS_PER_PAGE } from '../../../shared';
import {
    getJobAdvertisementDashboardState,
    JobAdvertisementFilter,
    PEADashboardState
} from '../state/pea-dashboard.state';
import {
    PEA_JOB_AD_DEFAULT_SORT,
    PEAJobAdsSearchRequest
} from '../../../shared/job-advertisement/pea-job-ads-search-request';
import { createJobAdvertisementCancellationRequest } from '../util/cancellation-request.mapper';
import { JobAdvertisementService } from '../../../shared/job-advertisement/job-advertisement.service';
import { JobAdvertisement } from '../../../shared/job-advertisement/job-advertisement.model';
import { JobAdvertisementCancelRequest } from '../../../shared/job-advertisement/job-advertisement-cancel-request';
import { CurrentSelectedCompanyService } from '../../../shared/company/current-selected-company.service';
import { Accountability } from '../../../shared/user-info/user-info.model';

@Injectable()
export class PEADashboardEffects {

    @Effect()
    cancelJobAdvertisement$: Observable<Action> = this.actions$
        .ofType(SUBMIT_CANCELLATION)
        .map((action: SubmitCancellationAction) => createJobAdvertisementCancellationRequest(action.payload))
        .switchMap((jobCancelRequest: JobAdvertisementCancelRequest) =>
            this.jobAdvertisementService.cancel(jobCancelRequest)
                .flatMap((code) => this.jobAdvertisementService.findById(jobCancelRequest.id))
                .map((jobAdvertisement: JobAdvertisement) => new CancellationSucceededAction(jobAdvertisement))
                .catch(() => Observable.of(new JobAdvertisementsLoadErrorAction()))
        );

    @Effect()
    loadNextJobAdvertisementsPage$: Observable<Action> = this.actions$
        .ofType(LOAD_NEXT_JOB_ADVERTISEMENTS_DASHBOARD_PAGE)
        .withLatestFrom(
            this.store.select(getJobAdvertisementDashboardState),
            this.currentSelectedCompanyService.getSelectedAccountability(),
        )
        .switchMap(([action, state, accountability]: [LoadNextJobAdvertisementsDashboardPageAction, PEADashboardState, Accountability]) =>
            this.jobAdvertisementService.searchPEAJobAds(
                this.createSearchRequest(state.jobAdvertisementFilter, action.payload.page, accountability))
                .map((resp) => this.toJobAdvertisementsLoadedActionAction(resp, action.payload.page))
                .catch(() => Observable.of(new JobAdvertisementsLoadErrorAction()))
        );

    @Effect()
    filterJobAdvertisements$: Observable<Action> = this.actions$
        .ofType(FILTER_JOB_ADVERTISEMENTS_DASHBOARD)
        .withLatestFrom(
            this.store.select(getJobAdvertisementDashboardState),
            this.currentSelectedCompanyService.getSelectedAccountability())
        .switchMap(([action, state, accountability]: [FilterJobAdvertisementsDashboardAction, PEADashboardState, Accountability]) =>
            this.jobAdvertisementService.searchPEAJobAds(
                this.createSearchRequest(action.payload, state.page, accountability))
                .map(this.toJobAdvertisementsLoadedActionAction)
                .catch(() => Observable.of(new JobAdvertisementsLoadErrorAction()))
        );

    constructor(private actions$: Actions,
                private store: Store<PEADashboardState>,
                private currentSelectedCompanyService: CurrentSelectedCompanyService,
                private jobAdvertisementService: JobAdvertisementService) {
    }

    private createSearchRequest(filter: JobAdvertisementFilter, page: number, accountability: Accountability): PEAJobAdsSearchRequest {
        const { jobTitle, onlineSinceDays } = filter;
        return {
            body: {
                companyId: accountability.companyExternalId,
                jobTitle,
                onlineSinceDays
            },
            page,
            size: ITEMS_PER_PAGE,
            sort: PEA_JOB_AD_DEFAULT_SORT
        };
    }

    private toJobAdvertisementsLoadedActionAction(response, page = 0): JobAdvertisementsLoadedAction {
        return new JobAdvertisementsLoadedAction({
            jobAdvertisements: response.json,
            totalCount: +response.headers.get('X-Total-Count'),
            page
        });
    }
}
