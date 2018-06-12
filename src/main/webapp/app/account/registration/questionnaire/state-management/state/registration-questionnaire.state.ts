import { createFeatureSelector, createSelector } from '@ngrx/store';

export interface RegistrationQuestionnaireState {
    role: string;
    termsAndConditions: boolean;
    user: boolean;
    showTermsAndConditionsSection: boolean;
    showIfUserExistOrNotSection: boolean
    nextButtonActive: boolean;
}

export const initialState: RegistrationQuestionnaireState = {
    role: '',
    termsAndConditions: false,
    user: false,
    showTermsAndConditionsSection: false,
    showIfUserExistOrNotSection : false,
    nextButtonActive: false
};

export const getRegistrationQuestionnaireState = createFeatureSelector<RegistrationQuestionnaireState>('registrationQuestionnaire');
export const getTermsAndConditionsChecked = createSelector(getRegistrationQuestionnaireState, (state: RegistrationQuestionnaireState) => state.termsAndConditions);
export const getShowTermsAndConditions = createSelector(getRegistrationQuestionnaireState, (state: RegistrationQuestionnaireState) => state.showTermsAndConditionsSection);
export const getShowIfUserExistOrNotSection = createSelector(getRegistrationQuestionnaireState, (state: RegistrationQuestionnaireState) => state.showIfUserExistOrNotSection);
export const getNextButtonActive = createSelector(getRegistrationQuestionnaireState, (state: RegistrationQuestionnaireState) => !state.termsAndConditions);
