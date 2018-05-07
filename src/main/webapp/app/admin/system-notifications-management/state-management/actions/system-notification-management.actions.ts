import { Action } from '@ngrx/store';
import { SystemNotification } from '../../../../shared/system-notification/system.notification.model';

// READ ACTIONS

export const GET_ALL_SYSTEMNOTIFICATIONS = 'SYSTEMNOTIFICATION:GET_ALL';
export const GET_ALL_SYSTEMNOTIFICATIONS_SUCCESS = 'SYSTEMNOTIFICATION:GET_ALL_SUCCESS';
export const GET_ALL_SYSTEMNOTIFICATIONS_FAILED = 'SYSTEMNOTIFICATION:GET_ALL_FAILED';

export class GetAllSystemNotificationsAction implements Action {
    readonly type = GET_ALL_SYSTEMNOTIFICATIONS;
}

export class GetAllSystemNotificationsSuccessAction implements Action {
    readonly type = GET_ALL_SYSTEMNOTIFICATIONS_SUCCESS;
    constructor(public payload: SystemNotification[]) {
    }
}

export class GetAllSystemNotificationsFailedAction implements Action {
    readonly type = GET_ALL_SYSTEMNOTIFICATIONS_FAILED;
    constructor(public payload = {}) {
    }
}

// CREATE ACTIONS

export const CREATE_SYSTEMNOTIFICATION = 'SYSTEMNOTIFICATION:CREATE_SYSTEMNOTIFICATION';
export const CREATE_SYSTEMNOTIFICATION_SUCCESS = 'SYSTEMNOTIFICATION:CREATE_SYSTEMNOTIFICATION SUCCESS';
export const CREATE_SYSTEMNOTIFICATION_FAILED = 'SYSTEMNOTIFICATION:CREATE_SYSTEMNOTIFICATION FAILED';

export class CreateSystemNotificationAction implements Action {
    readonly type = CREATE_SYSTEMNOTIFICATION;
    constructor(public payload: SystemNotification) {
    }
}

export class CreateSystemNotificationSuccessAction implements Action {
    readonly type = CREATE_SYSTEMNOTIFICATION_SUCCESS;
    constructor(public payload: SystemNotification) {
    }
}

export class CreateSystemNotificationFailedAction implements Action {
    readonly type = CREATE_SYSTEMNOTIFICATION_FAILED;
    constructor(public payload: any) {
    }
}

// UPDATE ACTIONS

export const UPDATE_SYSTEMNOTIFICATION = 'SYSTEMNOTIFICATION:UPDATE_SYSTEMNOTIFICATION';
export const UPDATE_SYSTEMNOTIFICATION_SUCCESS = 'SYSTEMNOTIFICATION:UPDATE_SYSTEMNOTIFICATION SUCCESS';
export const UPDATE_SYSTEMNOTIFICATION_FAILED = 'SYSTEMNOTIFICATION:UPDATE_SYSTEMNOTIFICATION FAILED';

export class UpdateSystemNotificationAction implements Action {
    readonly type = UPDATE_SYSTEMNOTIFICATION;
    constructor(public payload: SystemNotification) {
    }
}

export class UpdateSystemNotificationSuccessAction implements Action {
    readonly type = UPDATE_SYSTEMNOTIFICATION_SUCCESS;
    constructor(public payload: SystemNotification) {
    }
}

export class UpdateSystemNotificationFailedAction implements Action {
    readonly type = UPDATE_SYSTEMNOTIFICATION_FAILED;
    constructor(public payload: SystemNotification) {
    }
}

// DELETE ACTIONS

export const DELETE_SYSTEMNOTIFICATION = 'SYSTEMNOTIFICATION:DELETE_SYSTEMNOTIFICATION';
export const DELETE_SYSTEMNOTIFICATION_SUCCESS = 'SYSTEMNOTIFICATION:DELETE_SYSTEMNOTIFICATION SUCCESS';
export const DELETE_SYSTEMNOTIFICATION_FAILED = 'SYSTEMNOTIFICATION:DELETE_SYSTEMNOTIFICATION FAILED';

export class DeleteSystemNotificationAction implements Action {
    readonly type = DELETE_SYSTEMNOTIFICATION;
    constructor(public payload: SystemNotification) {
    }
}

export class DeleteSystemNotificationSuccessAction implements Action {
    readonly type = DELETE_SYSTEMNOTIFICATION_SUCCESS;
    constructor(public payload: SystemNotification) {
    }
}

export class DeleteSystemNotificationFailedAction implements Action {
    readonly type = DELETE_SYSTEMNOTIFICATION_FAILED;
    constructor(public payload: any) {
    }
}

export type SystemNotificationActions =
    | GetAllSystemNotificationsAction
    | GetAllSystemNotificationsSuccessAction
    | GetAllSystemNotificationsFailedAction
    | CreateSystemNotificationAction
    | CreateSystemNotificationSuccessAction
    | CreateSystemNotificationFailedAction
    | UpdateSystemNotificationAction
    | UpdateSystemNotificationSuccessAction
    | UpdateSystemNotificationFailedAction
    | DeleteSystemNotificationAction
    | DeleteSystemNotificationSuccessAction
    | DeleteSystemNotificationFailedAction;
