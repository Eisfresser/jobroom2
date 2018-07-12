import { Action } from '@ngrx/store';

export const SHOW_STATUS_NOTIFICATION_MESSAGE = 'STATUS_NOTIFICATION:SHOW_MESSAGE';
export const HIDE_STATUS_NOTIFICATION_MESSAGE = 'STATUS_NOTIFICATION:HIDE_MESSAGE';

export class ShowStatusNotificationMessageAction implements Action {
    readonly type = SHOW_STATUS_NOTIFICATION_MESSAGE;

    constructor(public payload: string) {
    }
}

export class HideStatusNotificationMessageAction implements Action {
    readonly type = HIDE_STATUS_NOTIFICATION_MESSAGE;

    constructor() {
    }
}
