import { SystemNotification } from '../../system-notification/system.notification.model';
import { Action } from '@ngrx/store';

export const GET_ACTIVE_SYSTEMNOTIFICATIONS = 'SYSTEMNOTIFICATION:GET_ACTIVE';
export const GET_ACTIVE_SYSTEMNOTIFICATIONS_SUCCESS = 'SYSTEMNOTIFICATION:GET_ACTIVE_SUCCESS';
export const GET_ACTIVE_SYSTEMNOTIFICATIONS_FAILED = 'SYSTEMNOTIFICATION:GET_ACTIVE_FAILED';

export class GetActiveSystemNotificationsAction implements Action {
    readonly type = GET_ACTIVE_SYSTEMNOTIFICATIONS;
}

export class GetActiveSystemNotificationsSuccessAction implements Action {
    readonly type = GET_ACTIVE_SYSTEMNOTIFICATIONS_SUCCESS;
    constructor(public payload: SystemNotification[]) {
    }
}

export class GetActiveSystemNotificationsFailedAction implements Action {
    readonly type = GET_ACTIVE_SYSTEMNOTIFICATIONS_FAILED;
    constructor(public payload = {}) {
    }
}
