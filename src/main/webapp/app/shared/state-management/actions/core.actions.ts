import { Action } from '@ngrx/store';
import { User } from '../..';
import { Alert } from '../state/core.state';

export const LANGUAGE_CHANGED = 'CORE:LANGUAGE_CHANGED';
export const INIT_LANGUAGE = 'CORE:INIT_LANGUAGE';
export const USER_LOGIN = 'CORE:USER_LOGIN';
export const SHOW_ALERT = 'CORE:SHOW_ALERT';
export const HIDE_ALERT = 'CORE:HIDE_ALERT';

export class InitLanguageAction implements Action {
    readonly type = INIT_LANGUAGE;

    constructor(public payload: string) {
    }
}

export class LanguageChangedAction implements Action {
    readonly type = LANGUAGE_CHANGED;

    constructor(public payload: string) {
    }
}

export class UserLoginAction implements Action {
    readonly type = USER_LOGIN;

    constructor(public payload: User) {
    }
}

export class ShowAlertAction implements Action {
    readonly type = SHOW_ALERT;

    constructor(public payload: Alert) {
    }
}

export class HideAlertAction implements Action {
    readonly type = HIDE_ALERT;

    constructor(public payload: Alert) {
    }
}

export type Actions =
    | InitLanguageAction
    | LanguageChangedAction
    | UserLoginAction
    | ShowAlertAction
    | HideAlertAction;
