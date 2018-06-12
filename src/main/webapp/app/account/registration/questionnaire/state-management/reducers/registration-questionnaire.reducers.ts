import { RegistrationQuestionnaireState, initialState } from '../state/registration-questionnaire.state';
import {
    Actions,
    SELECT_REGISTRATION_ROLE,
    SELECT_WHETHER_USER_EXIST_OR_NOT,
    ACCEPT_TERMS_AND_CONDITIONS,
    SHOW_TERMS_AND_CONDITIONS_SECTION,
    SHOW_IF_USER_EXIST_OR_NOT_SECTION,
    RESET_REGISTRATION_QUESTIONNAIRE,
    ACTIVATE_NEXT_BUTTON_ACTION, NEXT_REGISTRATION_PAGE,
} from '../actions/registration-questionnaire.actions';

export function registrationQuestionnaireReducer(state = initialState, action: Actions): RegistrationQuestionnaireState {
    switch (action.type) {
        case SELECT_REGISTRATION_ROLE:
            return { ...state, ...initialState, ...{ role: action.payload } };

        case ACCEPT_TERMS_AND_CONDITIONS:
            return { ...state, ...{ termsAndConditions: action.payload } };

        case SELECT_WHETHER_USER_EXIST_OR_NOT:
            return { ...state, ...{ termsAndConditions: false, user: action.payload } };

        case SHOW_TERMS_AND_CONDITIONS_SECTION:
            return { ...state, ...{ showTermsAndConditionsSection: true } };

        case SHOW_IF_USER_EXIST_OR_NOT_SECTION:
            return { ...state, ...{ showIfUserExistOrNotSection: true } };

        case RESET_REGISTRATION_QUESTIONNAIRE:
            return { ...initialState };

        case ACTIVATE_NEXT_BUTTON_ACTION:
            return { ...state, ...{ nextButtonActive: true } };

        case NEXT_REGISTRATION_PAGE:
            return { ...action.payload  };

        default:
            return state;
    }
}
