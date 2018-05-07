import { Action } from '@ngrx/store';
import { JobAdvertisementFilter } from '../state/pea-dashboard.state';
import { CancellationData } from '../../dialogs/cancellation-data';
import { JobAdvertisement } from '../../../shared/job-advertisement/job-advertisement.model';

export const FILTER_JOB_ADVERTISEMENTS_DASHBOARD = 'DASHBOARD:FILTER_JOB_ADVERTISEMENTS_DASHBOARD';
export const LOAD_NEXT_JOB_ADVERTISEMENTS_DASHBOARD_PAGE = 'DASHBOARD:LOAD_NEXT_JOB_ADVERTISEMENTS_DASHBOARD_PAGE';
export const JOB_ADVERTISEMENTS_LOADED = 'DASHBOARD:JOB_ADVERTISEMENTS_LOADED';
export const JOB_ADVERTISEMENTS_LOAD_ERROR = 'DASHBOARD:JOB_ADVERTISEMENTS_LOAD_ERROR';
export const SUBMIT_CANCELLATION = 'DASHBOARD:SUBMIT_CANCELLATION';
export const CANCELLATION_SUCCEEDED = 'DASHBOARD:CANCELLATION_SUCCEEDED';

export class FilterJobAdvertisementsDashboardAction implements Action {
    readonly type = FILTER_JOB_ADVERTISEMENTS_DASHBOARD;

    constructor(public payload: JobAdvertisementFilter) {
    }
}

export class LoadNextJobAdvertisementsDashboardPageAction implements Action {
    readonly type = LOAD_NEXT_JOB_ADVERTISEMENTS_DASHBOARD_PAGE;

    constructor(public payload: { page: number }) {
    }
}

export class JobAdvertisementsLoadedAction implements Action {
    readonly type = JOB_ADVERTISEMENTS_LOADED;

    constructor(public payload: { jobAdvertisements: JobAdvertisement[], totalCount: number, page: number }) {
    }
}

export class JobAdvertisementsLoadErrorAction implements Action {
    readonly type = JOB_ADVERTISEMENTS_LOAD_ERROR;

    constructor(public payload = {}) {
    }
}

export class SubmitCancellationAction implements Action {
    readonly type = SUBMIT_CANCELLATION;

    constructor(public payload: CancellationData) {
    }
}

export class CancellationSucceededAction implements Action {
    readonly type = CANCELLATION_SUCCEEDED;

    constructor(public payload: JobAdvertisement) {
    }
}

export type Actions =
    | FilterJobAdvertisementsDashboardAction
    | JobAdvertisementsLoadErrorAction
    | LoadNextJobAdvertisementsDashboardPageAction
    | SubmitCancellationAction
    | CancellationSucceededAction
    | JobAdvertisementsLoadedAction;
