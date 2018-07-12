import { ActionReducerMap } from '@ngrx/store';
import { HomeState } from '../state/home.state';
import { jobSearchToolReducer } from './job-search-tool.reducers';
import { candidateSearchToolReducer } from './candidate-search-tool.reducers';
import { homeLayoutReducer } from './layout.reducers';
import { systemNotificationReducer } from './system-notification-reducers';
import { statusNotificationReducer } from './status-notification.reducers';

export const homeReducers: ActionReducerMap<HomeState> = {
    jobSearchTool: jobSearchToolReducer,
    candidateSearchTool: candidateSearchToolReducer,
    layoutState: homeLayoutReducer,
    systemNotification: systemNotificationReducer,
    statusNotification: statusNotificationReducer
};
