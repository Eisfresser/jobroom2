import { initialState } from '../state/api-user-management.state';
import {
    Actions,
    API_USER_UPDATED,
    API_USERS_LOADED,
    FILTER_API_USERS
} from '../action/api-user-management.actions';

export function apiUserManagementReducer(state = initialState, action: Actions) {
    let newState;
    switch (action.type) {
        case FILTER_API_USERS:
            newState = Object.assign({}, state, {
                filter: action.payload,
                totalCount: 0,
                page: 0
            });
            break;
        case API_USERS_LOADED:
            newState = Object.assign({}, state, {
                apiUsers: action.payload.apiUsers,
                totalCount: action.payload.totalCount,
                page: action.payload.page
            });
            break;
        case API_USER_UPDATED:
            const updatedApiUser = action.payload;
            const patchedApiUsers = state.apiUsers.map((apiUser) =>
                apiUser.id === updatedApiUser.id
                    ? updatedApiUser
                    : apiUser);
            newState = Object.assign({}, state, {
                apiUsers: patchedApiUsers
            });
            break;
        default:
            newState = state;
    }
    return newState;
}
