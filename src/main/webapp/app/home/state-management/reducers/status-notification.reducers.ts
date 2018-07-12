import {
    initialState,
    StatusNotificationState
} from '../state/status-notification.state';
import { Actions } from '../index';
import {
    HIDE_STATUS_NOTIFICATION_MESSAGE,
    SHOW_STATUS_NOTIFICATION_MESSAGE
} from '../actions/status-notification.actions';

export function statusNotificationReducer(state = initialState, action: Actions): StatusNotificationState {
    let newState;
    switch (action.type) {
        case SHOW_STATUS_NOTIFICATION_MESSAGE:
            newState = Object.assign({}, state, {
                messageKey: action.payload,
                showMessage: true
            });
            break;
        case HIDE_STATUS_NOTIFICATION_MESSAGE:
            newState = Object.assign({}, state, { showMessage: false });
            break;
        default:
            newState = state;
    }
    return newState;
}
