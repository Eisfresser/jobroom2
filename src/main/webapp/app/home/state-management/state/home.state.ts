import { JobSearchToolState } from './job-search-tool.state';
import { createFeatureSelector, createSelector } from '@ngrx/store';
import { CandidateSearchToolState } from './candidate-search-tool.state';
import { HomeLayoutState } from './layout.state';
import { SystemNotificationState } from './system-notification-state';

export interface HomeState {
    layoutState: HomeLayoutState;
    jobSearchTool: JobSearchToolState;
    candidateSearchTool: CandidateSearchToolState;
    systemNotification: SystemNotificationState
}

export const getHomeState = createFeatureSelector<HomeState>('home');
export const getJobSearchToolState = createSelector(getHomeState, (state: HomeState) => state.jobSearchTool);
export const getCandidateSearchToolState = createSelector(getHomeState, (state: HomeState) => state.candidateSearchTool);
export const getLayoutState = createSelector(getHomeState, (state: HomeState) => state.layoutState);
export const getActiveToolbarItem = createSelector(getLayoutState, (state: HomeLayoutState) => state.activeToolbarItem);
export const getActiveCompanyTabId = createSelector(getLayoutState, (state: HomeLayoutState) => state.activeCompanyTabId);
export const getActiveAgencyTabId = createSelector(getLayoutState, (state: HomeLayoutState) => state.activeAgencyTabId);
export const getSystemNotifications = createSelector(getHomeState, (state: HomeState) => state.systemNotification);
export const getSystemNotificationsEntities = createSelector(getSystemNotifications, (state: SystemNotificationState) => state.entities);
export const getActiveSystemNotifications = createSelector(
    getSystemNotificationsEntities,
    (entities) => {
        return Object.keys(entities).map((id) => entities[id]);
    }
)
