import { createFeatureSelector, createSelector } from '@ngrx/store';
import { JobAdvertisement } from '../../../shared/job-advertisement/job-advertisement.model';

export enum LoadingStatus {
    INITIAL, OK, FAILED
}

export interface JobPublicationDetailState {
    showCancellationSuccess: boolean;
    showCancellationError: boolean;
    jobAdvertisement: JobAdvertisement;
    loadingStatus: LoadingStatus;
}

export const initialState: JobPublicationDetailState = {
    showCancellationSuccess: false,
    showCancellationError: false,
    jobAdvertisement: null,
    loadingStatus: LoadingStatus.INITIAL
};

export const getJobAdvertisementDetailState = createFeatureSelector<JobPublicationDetailState>('jobPublicationDetail');
export const getShowCancellationSuccess = createSelector(getJobAdvertisementDetailState,
    (state: JobPublicationDetailState) => state.showCancellationSuccess);
export const getShowCancellationError = createSelector(getJobAdvertisementDetailState,
    (state: JobPublicationDetailState) => state.showCancellationError);
export const getJobAdvertisement = createSelector(getJobAdvertisementDetailState,
    (state: JobPublicationDetailState) => state.jobAdvertisement);
export const getLoadingStatus = createSelector(getJobAdvertisementDetailState,
    (state: JobPublicationDetailState) => state.loadingStatus);
