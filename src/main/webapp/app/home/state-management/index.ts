import {
    JobSearchToolCountAction, JobSearchToolCountedAction, JobSearchToolSubmittedAction,
    ResetJobSearchToolCountAction
} from './actions/job-search-tool.actions';
import {
    CandidateSearchToolCountAction,
    CandidateSearchToolCountedAction,
    CandidateSearchToolSubmittedAction,
    ResetCandidateSearchToolCountAction, UpdateOccupationTranslationAction
} from './actions/candidate-search-tool.actions';
import {
    SelectAgencyTabAction,
    SelectCompanyTabAction,
    SelectToolbarItemAction
} from './actions/layout.actions';
import { GetActiveSystemNotificationsAction, GetActiveSystemNotificationsFailedAction, GetActiveSystemNotificationsSuccessAction } from './actions/system-notification-actions';
export { SystemNotificationState } from './state/system-notification-state';

export * from './actions/job-search-tool.actions';
export * from './actions/candidate-search-tool.actions';
export * from './actions/layout.actions';
export * from './actions/system-notification-actions';
export * from './reducers/home.reducers';
export * from './reducers/job-search-tool.reducers';
export * from './reducers/layout.reducers';
export * from './reducers/system-notification-reducers'
export { JobSearchToolState } from './state/job-search-tool.state';
export * from './effects/home.effects';
export * from './state/home.state';
export { CandidateSearchToolState } from './state/candidate-search-tool.state';

export type Actions =
    | JobSearchToolSubmittedAction
    | JobSearchToolCountAction
    | JobSearchToolCountedAction
    | ResetJobSearchToolCountAction
    | CandidateSearchToolSubmittedAction
    | CandidateSearchToolCountAction
    | CandidateSearchToolCountedAction
    | UpdateOccupationTranslationAction
    | ResetCandidateSearchToolCountAction
    | SelectToolbarItemAction
    | SelectCompanyTabAction
    | SelectAgencyTabAction
    | GetActiveSystemNotificationsAction
    | GetActiveSystemNotificationsSuccessAction
    | GetActiveSystemNotificationsFailedAction;
