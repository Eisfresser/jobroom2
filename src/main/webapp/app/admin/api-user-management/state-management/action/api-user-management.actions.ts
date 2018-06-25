import { Action } from '@ngrx/store';
import { ApiUser, ApiUserUpdatePasswordRequest } from '../../service/api-user.service';
import { ApiUserManagementFilter } from '../state/api-user-management.state';

export const FILTER_API_USERS = 'API_USERS_MANAGEMENT:FILTER_API_USERS';
export const LOAD_NEXT_API_USERS_PAGE = 'API_USERS_MANAGEMENT:LOAD_NEXT_API_USERS_PAGE';
export const API_USERS_LOADED = 'API_USERS_MANAGEMENT:API_USERS_LOADED';
export const UPDATE_API_USER = 'API_USERS_MANAGEMENT:UPDATE_API_USER';
export const API_USER_UPDATED = 'API_USERS_MANAGEMENT:API_USER_UPDATED';
export const CREATE_API_USER = 'API_USERS_MANAGEMENT:CREATE_API_USER';
export const TOGGLE_STATUS = 'API_USERS_MANAGEMENT:TOGGLE_STATUS';
export const UPDATE_PASSWORD = 'API_USER_MANAGEMENT:UPDATE_PASSWORD';

export class FilterApiUsersAction implements Action {
    readonly type = FILTER_API_USERS;

    constructor(public payload: ApiUserManagementFilter) {
    }
}

export class LoadNextApiUsersPageAction implements Action {
    readonly type = LOAD_NEXT_API_USERS_PAGE;

    constructor(public payload: { page: number }) {
    }
}

export class ApiUsersLoadedAction implements Action {
    readonly type = API_USERS_LOADED;

    constructor(public payload: { apiUsers: ApiUser[], totalCount: number, page: number }) {
    }
}

export class UpdateApiUserAction implements Action {
    readonly type = UPDATE_API_USER;

    constructor(public payload: ApiUser) {
    }
}

export class ApiUserUpdatedAction implements Action {
    readonly type = API_USER_UPDATED;

    constructor(public payload: ApiUser) {
    }
}

export class CreateApiUserAction implements Action {
    readonly type = CREATE_API_USER;

    constructor(public payload: ApiUser) {
    }
}

export class ToggleStatusAction implements Action {
    readonly type = TOGGLE_STATUS;

    constructor(public payload: ApiUser) {
    }
}

export class UpdatePasswordAction implements Action {
    readonly type = UPDATE_PASSWORD;

    constructor(public payload: { id: string, password: ApiUserUpdatePasswordRequest }) {
    }
}

export type Actions =
    | FilterApiUsersAction
    | LoadNextApiUsersPageAction
    | ApiUsersLoadedAction
    | UpdateApiUserAction
    | ApiUserUpdatedAction
    | CreateApiUserAction;
