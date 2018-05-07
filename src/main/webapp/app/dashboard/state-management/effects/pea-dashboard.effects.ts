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
import { ITEMS_PER_PAGE, Principal } from '../../../shared';
import {
    getJobAdvertisementDashboardState, JobAdvertisementFilter,
    PEADashboardState
} from '../state/pea-dashboard.state';
import { JobPublicationSearchRequest } from '../../../shared/job-publication/job-publication-search-request';
import { createJobAdvertisementCancellationRequest } from '../util/cancellation-request.mapper';
import { JobAdvertisementService } from '../../../shared/job-advertisement/job-advertisement.service';
import { JobAdvertisement } from '../../../shared/job-advertisement/job-advertisement.model';
import { JobAdvertisementCancelRequest } from '../../../shared/job-advertisement/job-advertisement-cancel-request';

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
            this.principal.getAuthenticationState())
        .switchMap(([action, state, identity]: [LoadNextJobAdvertisementsDashboardPageAction, PEADashboardState, any]) =>
            this.jobAdvertisementService.search(
                this.createSearchRequest(state.jobAdvertisementFilter, action.payload.page, identity))
                .map((resp) => this.toJobAdvertisementsLoadedActionAction(resp, action.payload.page))
                .catch(() => Observable.of(new JobAdvertisementsLoadErrorAction()))
        );

    @Effect()
    filterJobAdvertisements$: Observable<Action> = this.actions$
        .ofType(FILTER_JOB_ADVERTISEMENTS_DASHBOARD)
        .withLatestFrom(
            this.store.select(getJobAdvertisementDashboardState),
            this.principal.getAuthenticationState())
        .switchMap(([action, state, identity]: [FilterJobAdvertisementsDashboardAction, PEADashboardState, any]) =>
            this.jobAdvertisementService.search(
                this.createSearchRequest(action.payload, state.page, identity))
                .map(this.toJobAdvertisementsLoadedActionAction)
                .catch(() => Observable.of(new JobAdvertisementsLoadErrorAction()))
        );

    constructor(private actions$: Actions,
                private store: Store<PEADashboardState>,
                private principal: Principal,
                private jobAdvertisementService: JobAdvertisementService) {
    }

    private createSearchRequest(filter: JobAdvertisementFilter, page: number, identity): JobPublicationSearchRequest {
        const { jobTitle, onlineSinceDays } = filter;
        return {
            email: identity.email,
            page,
            size: ITEMS_PER_PAGE,
            jobTitle,
            onlineSinceDays
        };
    }

    private toJobAdvertisementsLoadedActionAction(response, page = 0): JobAdvertisementsLoadedAction {
        const resp = response.json;
        return new JobAdvertisementsLoadedAction({
            jobAdvertisements: resp.content,
            totalCount: +resp.totalElements,
            page
        });
    }
}
