import { ApiUser } from '../../service/api-user.service';
import { createFeatureSelector, createSelector } from '@ngrx/store';

export interface ApiUserManagementFilter {
    query: string;
    sort: string;
}

export interface ApiUserManagementState {
    filter: ApiUserManagementFilter;
    apiUsers: ApiUser[];
    totalCount: number;
    page: number;
    error: boolean;
}

export const initialState: ApiUserManagementState = {
    filter: {
        query: null,
        sort: 'apiUser.username.keyword,asc'
    },
    apiUsers: [],
    totalCount: 0,
    page: 0,
    error: false
};

export const getApiUserManagementState = createFeatureSelector<ApiUserManagementState>('apiUserManagement');
export const getApiUserManagementFilter = createSelector(getApiUserManagementState, (state: ApiUserManagementState) => state.filter);
export const getApiUsers = createSelector(getApiUserManagementState, (state: ApiUserManagementState) => state.apiUsers);
export const getApiUserManagementTotalCount = createSelector(getApiUserManagementState, (state: ApiUserManagementState) => state.totalCount);
export const getApiUserManagementPage = createSelector(getApiUserManagementState, (state: ApiUserManagementState) => state.page);
