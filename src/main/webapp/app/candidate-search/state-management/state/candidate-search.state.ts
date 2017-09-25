import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
    Availability,
    DrivingLicenceCategory,
    Experience,
    Graduation,
    GreaterRegion,
    ISCED_1997,
    LanguageSkill,
    WorkForm
} from '../../services/candidate-search-request';

export interface CandidateSearchState {
    searchFilter: CandidateSearchFilter
}

export interface CandidateSearchFilter {
    occupation?: string,
    skills: Array<string>,
    experience?: Experience,
    workplace?: string,
    residence?: GreaterRegion,
    availability?: Availability,
    workload: [number, number];
    workForm?: WorkForm,
    educationLevel?: ISCED_1997,
    graduation?: Graduation,
    drivingLicenceCategory?: DrivingLicenceCategory
    languageSkills: Array<LanguageSkill>
}

export const initialState: CandidateSearchState = {
    searchFilter: {
        skills: [],
        languageSkills: [],
        workload: [0, 100]
    }
};

export const getCandidateSearchState = createFeatureSelector<CandidateSearchState>('candidateSearch');
export const getSearchFilter = createSelector(getCandidateSearchState, (state: CandidateSearchState) => state.searchFilter);
