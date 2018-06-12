import { Action } from '@ngrx/store';
import { RegistrationQuestionnaireState } from '../state/registration-questionnaire.state';

export const SELECT_REGISTRATION_ROLE = 'REGISTRATION:SELECT_REGISTRATION_ROLE';
export const ACCEPT_TERMS_AND_CONDITIONS = 'REGISTRATION:ACCEPT_TERMS_AND_CONDITIONS';
export const SELECT_WHETHER_USER_EXIST_OR_NOT = 'REGISTRATION:SELECT_WHETHER_USER_EXIST_OR_NOT';
export const SHOW_TERMS_AND_CONDITIONS_SECTION = 'REGISTRATION:SHOW_TERMS_AND_CONDITIONS_SECTION';
export const SHOW_IF_USER_EXIST_OR_NOT_SECTION = 'REGISTRATION:SHOW_IF_USER_EXIST_OR_NOT_SECTION';
export const ACTIVATE_NEXT_BUTTON_ACTION = 'REGISTRATION:ACTIVATE_NEXT_BUTTON_ACTION';
export const RESET_REGISTRATION_QUESTIONNAIRE = 'REGISTRATION:RESET_REGISTRATION_QUESTIONNAIRE';
export const NEXT_REGISTRATION_PAGE = 'REGISTRATION:NEXT_REGISTRATION_PAGE';

export class SelectRegistrationRoleAction implements Action {
    readonly type = SELECT_REGISTRATION_ROLE;

    constructor(public payload: string) {
    }
}

export class AcceptTermsAndConditionsAction implements Action {
    readonly type = ACCEPT_TERMS_AND_CONDITIONS;

    constructor(public payload: boolean) {
    }
}

export class SelectWhetherUserExistOrNotAction implements Action {
    readonly type = SELECT_WHETHER_USER_EXIST_OR_NOT;

    constructor(public payload: boolean) {
    }
}

export class ShowTermsAndConditionsSectionAction implements Action {
    readonly type = SHOW_TERMS_AND_CONDITIONS_SECTION;
}

export class ShowIfUserExistOrNotSectionAction implements Action {
    readonly type = SHOW_IF_USER_EXIST_OR_NOT_SECTION;
}

export class ActivateNextButtonAction implements Action {
    readonly type = ACTIVATE_NEXT_BUTTON_ACTION;
}

export class ResetRegistrationQuestionnaireAction implements Action {
    readonly type = RESET_REGISTRATION_QUESTIONNAIRE;
}

export class NextRegistrationPageAction implements Action {
    readonly type = NEXT_REGISTRATION_PAGE;

    constructor(public payload: RegistrationQuestionnaireState) {
    }
}

export type Actions =
    | SelectRegistrationRoleAction
    | AcceptTermsAndConditionsAction
    | SelectWhetherUserExistOrNotAction
    | ShowTermsAndConditionsSectionAction
    | ShowIfUserExistOrNotSectionAction
    | ActivateNextButtonAction
    | ResetRegistrationQuestionnaireAction
    | NextRegistrationPageAction
    ;
