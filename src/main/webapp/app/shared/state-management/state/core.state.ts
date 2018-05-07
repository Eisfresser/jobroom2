import { createFeatureSelector, createSelector } from '@ngrx/store';
import { User } from '../..';

export interface CoreState {
    language: string;
    currentUser: User;
}

export const initialState: CoreState = {
    language: null,
    currentUser: null
};

export const getCoreState = createFeatureSelector<CoreState>('coreReducer');
export const getLanguage = createSelector(getCoreState, (state: CoreState) => state.language);
export const getCurrentUser = createSelector(getCoreState, (state: CoreState) => state.currentUser);
