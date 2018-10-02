import { initialState } from '../state/core.state';
import {
    Actions,
    HIDE_ALERT,
    INIT_LANGUAGE,
    LANGUAGE_CHANGED,
    SHOW_ALERT,
    USER_LOGIN
} from '../actions/core.actions';
import { ActionReducerMap } from '@ngrx/store';
import { routerReducer, RouterReducerState } from '@ngrx/router-store';
import { RouterStateUrl } from '../../custom-router-state-serializer/custom-router-state-serializer';

export function coreReducer(state = initialState, action: Actions) {
    let newState;

    switch (action.type) {
        case INIT_LANGUAGE:
            newState = Object.assign({}, state, { language: action.payload });
            break;
        case LANGUAGE_CHANGED:
            newState = Object.assign({}, state, { language: action.payload });
            break;
        case USER_LOGIN:
            newState = Object.assign({}, state, { currentUser: action.payload });
            break;
        case SHOW_ALERT:
            newState = state.alerts.findIndex((alert) => alert.equals(action.payload)) < 0
                ? Object.assign({}, state, { alerts: [...state.alerts, action.payload] })
                : state;
            break;
        case HIDE_ALERT:
            const alerts = state.alerts.filter((alert) => !alert.equals(action.payload));
            newState = Object.assign({}, state, { alerts });
            break;

        default:
            newState = state;
    }

    return newState;
}

export interface CoreState {
    routerReducer: RouterReducerState<RouterStateUrl>;
    coreReducer;
}

export const reducers: ActionReducerMap<CoreState> = {
    routerReducer,
    coreReducer
};
