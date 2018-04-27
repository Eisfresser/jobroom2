import { createFeatureSelector, createSelector } from '@ngrx/store';
import { JobAdvertisement } from '../../../shared/job-advertisement/job-advertisement.model';

export interface PEADashboardState {
    jobAdvertisementFilter: JobAdvertisementFilter;
    jobAdvertisements: JobAdvertisement[];
    totalCount: number;
    page: number;
}

export interface JobAdvertisementFilter {
    jobTitle: string;
    onlineSinceDays: number;
}

export const initialState: PEADashboardState = {
    jobAdvertisementFilter: {
        jobTitle: '',
        onlineSinceDays: 90
    },
    jobAdvertisements: [],
    totalCount: 0,
    page: 0
};

export const getJobAdvertisementDashboardState = createFeatureSelector<PEADashboardState>('peaDashboard');
export const getJobAdvertisementFilter = createSelector(getJobAdvertisementDashboardState, (state: PEADashboardState) => state.jobAdvertisementFilter);
export const getJobAdvertisements = createSelector(getJobAdvertisementDashboardState, (state: PEADashboardState) => state.jobAdvertisements);
export const getJobAdvertisementsTotalCount = createSelector(getJobAdvertisementDashboardState, (state: PEADashboardState) => state.totalCount);
export const getJobAdvertisementsPage = createSelector(getJobAdvertisementDashboardState, (state: PEADashboardState) => state.page);
