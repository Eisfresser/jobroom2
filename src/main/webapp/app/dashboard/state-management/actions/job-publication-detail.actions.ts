import { Action } from '@ngrx/store';
import { CancellationData } from '../../dialogs/cancellation-data';
import { JobAdvertisement } from '../../../shared/job-advertisement/job-advertisement.model';

export const SUBMIT_CANCELLATION = 'JOB_PUBLICATION_DETAIL:SUBMIT_CANCELLATION';
export const CANCELLATION_SUCCEEDED = 'JOB_PUBLICATION_DETAIL:CANCELLATION_SUCCEEDED';
export const CANCELLATION_FAILED = 'JOB_PUBLICATION_DETAIL:CANCELLATION_FAILED';
export const HIDE_SUCCESS_MESSAGE = 'JOB_PUBLICATION_DETAIL:HIDE_SUCCESS_MESSAGE';
export const HIDE_ERROR_MESSAGE = 'JOB_PUBLICATION_DETAIL:HIDE_ERROR_MESSAGE';
export const LOAD_JOB_ADVERTISEMENT = 'JOB_PUBLICATION_DETAIL:LOAD_JOB_ADVERTISEMENT';
export const LOAD_JOB_ADVERTISEMENT_FAILED = 'JOB_PUBLICATION_DETAIL:LOAD_JOB_ADVERTISEMENT_FAILED';
export const JOB_ADVERTISEMENT_LOADED = 'JOB_PUBLICATION_DETAIL:JOB_ADVERTISEMENT_LOADED';

export class LoadJobAdvertisementAction implements Action {
    readonly type = LOAD_JOB_ADVERTISEMENT;

    constructor(public payload: { id: string, token?: string }) {
    }
}

export class JobAdvertisementLoadedAction implements Action {
    readonly type = JOB_ADVERTISEMENT_LOADED;

    constructor(public payload: JobAdvertisement) {
    }
}

export class LoadJobAdvertisementFailedAction implements Action {
    readonly type = LOAD_JOB_ADVERTISEMENT_FAILED;

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

export class CancellationFailedAction implements Action {
    readonly type = CANCELLATION_FAILED;

    constructor(public payload = {}) {
    }
}

export class HideSuccessMessageAction implements Action {
    readonly type = HIDE_SUCCESS_MESSAGE;

    constructor(public payload = {}) {
    }
}

export class HideErrorMessageAction implements Action {
    readonly type = HIDE_ERROR_MESSAGE;

    constructor(public payload = {}) {
    }
}

export type Actions = LoadJobAdvertisementAction
    | JobAdvertisementLoadedAction
    | LoadJobAdvertisementFailedAction
    | SubmitCancellationAction
    | CancellationSucceededAction
    | CancellationFailedAction
    | HideSuccessMessageAction
    | HideErrorMessageAction
    ;
