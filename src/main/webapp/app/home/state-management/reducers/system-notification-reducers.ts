import { initialState, SystemNotificationState } from '../state/system-notification-state';
import { GET_ACTIVE_SYSTEMNOTIFICATIONS_FAILED, GET_ACTIVE_SYSTEMNOTIFICATIONS_SUCCESS, HIDE_SYSTEMNOTIFICATION } from '../actions/system-notification-actions';
import { SystemNotification } from '../../system-notification/system.notification.model';
import { Actions } from '../index';

export function systemNotificationReducer(state = initialState, action: Actions): SystemNotificationState {
    let newState;
    switch (action.type) {
        case GET_ACTIVE_SYSTEMNOTIFICATIONS_SUCCESS: {
            const systemNotifications = action.payload;
            const entities = systemNotifications.reduce(
                (
                    entitiesReduced: { [id: number]: SystemNotification },
                    systemNotification: SystemNotification
                ) => {
                    return {
                        ...entitiesReduced,
                        [systemNotification.id]: systemNotification
                    };
                },
                {
                    ...state.entities
                }
            );
            newState = {
                ...state,
                loading: false,
                loaded: true,
                entities
            };
            break
        }

        case GET_ACTIVE_SYSTEMNOTIFICATIONS_FAILED: {
            newState = {
                ...state,
                loading: false,
                loaded: false
            };
            break
        }

        case HIDE_SYSTEMNOTIFICATION: {
            const entities = Object.assign({}, state.entities);
            delete entities[action.payload];
            newState = Object.assign({}, state, { entities });
            break;
        }

        default:
            newState = state;
    }
    return newState;
}
