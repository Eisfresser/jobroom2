import { createFeatureSelector, createSelector } from '@ngrx/store';
import { getSystemNotificationEntities } from '../reducers/system-notification-management-reducers';
import { SystemNotification } from '../../../../shared/system-notification/system.notification.model';

export interface SystemNotificationState {
    entities: { [id: number]: SystemNotification};
    loaded: boolean;
    loading: boolean;
}

export const initialState: SystemNotificationState = {
    entities: {},
    loaded: false,
    loading: false,
};

export const getSystemNotificationState = createFeatureSelector<SystemNotificationState>('SystemNotifications');
export const getSystemNotificationsEntities = createSelector(getSystemNotificationState, getSystemNotificationEntities);
export const getAllSystemNotifications = createSelector(
    getSystemNotificationsEntities,
    (entities) => {
        return Object.keys(entities).map((id) => entities[id]);
    }
)
export const getActiveSystemNotifications = createSelector(
    getSystemNotificationsEntities,
    (entities) => {
        return Object.keys(entities).map((id) => entities[id]);
    }
)
