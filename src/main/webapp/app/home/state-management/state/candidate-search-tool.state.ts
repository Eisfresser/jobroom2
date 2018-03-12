import { TypeaheadMultiselectModel } from '../../../shared/input-components';

export interface CandidateSearchToolState {
    occupations?: Array<TypeaheadMultiselectModel>;
    workplace?: Array<TypeaheadMultiselectModel>;
    skills?: Array<string>;
    totalCount: number;
}

export const initialState: CandidateSearchToolState = {
    totalCount: -1
};
