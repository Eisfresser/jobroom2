import { SystemNotification } from '../../system-notification/system.notification.model';

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
