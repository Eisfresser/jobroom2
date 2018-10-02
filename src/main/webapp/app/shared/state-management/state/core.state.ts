import { createFeatureSelector, createSelector } from '@ngrx/store';
import { User } from '../..';

export interface CoreState {
    language: string;
    currentUser: User;
    alerts: Alert[]
}

export class Alert {
    type: string;
    message: string;

    constructor(type: string, message: string) {
        this.type = type;
        this.message = message;
    }

    equals(that: Alert) {
        if (that) {
            return this.type === that.type && this.message === that.message;
        }
        return false;
    }
}

export const initialState: CoreState = {
    language: null,
    currentUser: null,
    alerts: []
};

export const getCoreState = createFeatureSelector<CoreState>('coreReducer');
export const getLanguage = createSelector(getCoreState, (state: CoreState) => state.language);
export const getCurrentUser = createSelector(getCoreState, (state: CoreState) => state.currentUser);
export const getAlerts = createSelector(getCoreState, (state: CoreState) => state.alerts);
