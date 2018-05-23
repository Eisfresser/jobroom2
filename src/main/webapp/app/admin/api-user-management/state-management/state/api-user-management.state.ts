import { ApiUser } from '../../service/api-user.service';
import { createFeatureSelector, createSelector } from '@ngrx/store';

export interface ApiUserManagementState {
    filter: string;
    apiUsers: ApiUser[];
    totalCount: number;
    page: number;
    error: boolean;
}

export const initialState: ApiUserManagementState = {
    filter: null,
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
