import { Action } from '@ngrx/store';
import { User } from '../..';

export const LANGUAGE_CHANGED = 'CORE:LANGUAGE_CHANGED';
export const INIT_LANGUAGE = 'CORE:INIT_LANGUAGE';
export const USER_LOGIN = 'CORE:USER_LOGIN';

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

export type Actions =
    | InitLanguageAction
    | LanguageChangedAction
    | UserLoginAction;
