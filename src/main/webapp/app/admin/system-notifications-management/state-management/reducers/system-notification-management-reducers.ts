import {
    initialState,
    SystemNotificationState
} from '../state/system-notification-management.state';
import {
    CREATE_SYSTEMNOTIFICATION_SUCCESS,
    DELETE_SYSTEMNOTIFICATION_SUCCESS,
    GET_ALL_SYSTEMNOTIFICATIONS,
    GET_ALL_SYSTEMNOTIFICATIONS_FAILED,
    GET_ALL_SYSTEMNOTIFICATIONS_SUCCESS,
    SystemNotificationActions,
    UPDATE_SYSTEMNOTIFICATION_SUCCESS
} from '../actions/system-notification-management.actions';
import { SystemNotification } from '../../../../home/system-notification/system.notification.model';

export function systemNotificationReducer(
    state = initialState,
    action: SystemNotificationActions
): SystemNotificationState {
    switch (action.type) {
        case GET_ALL_SYSTEMNOTIFICATIONS: {
            return {
                ...state,
                loading: true
            };
        }
        case GET_ALL_SYSTEMNOTIFICATIONS_SUCCESS: {
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
            return {
                ...state,
                loading: false,
                loaded: true,
                entities
            };
        }

        case GET_ALL_SYSTEMNOTIFICATIONS_FAILED: {
            return {
                ...state,
                loading: false,
                loaded: false
            };
        }
        case DELETE_SYSTEMNOTIFICATION_SUCCESS: {
            const systemNotification = action.payload;
            const {
                [systemNotification.id]: removed,
                ...entities
            } = state.entities;
            return {
                ...state,
                entities
            };
        }

        case UPDATE_SYSTEMNOTIFICATION_SUCCESS:
        case CREATE_SYSTEMNOTIFICATION_SUCCESS: {
            const systemNotification = action.payload;
            const entities = {
                ...state.entities,
                [systemNotification.id]: systemNotification
            };
            return {
                ...state,
                entities
            };
        }
    }
    return state;
}

export const getSystemNotificationLoading = (state: SystemNotificationState) =>
    state.loading;
export const getSystemNotificationLoaded = (state: SystemNotificationState) =>
    state.loaded;
export const getSystemNotificationEntities = (state: SystemNotificationState) =>
    state.entities;
