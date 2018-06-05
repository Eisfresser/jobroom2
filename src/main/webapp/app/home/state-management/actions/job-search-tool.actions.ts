import { Action } from '@ngrx/store';
import { JobSearchToolState } from '../state/job-search-tool.state';
import { TypeaheadMultiselectModel } from '../../../shared/input-components/typeahead/typeahead-multiselect-model';

export const JOB_SEARCH_TOOL_SUBMITTED = 'JOB_SEARCH_TOOL_SUBMITTED';
export const JOB_SEARCH_TOOL_COUNT = 'JOB_SEARCH_TOOL_COUNT';
export const JOB_SEARCH_TOOL_COUNTED = 'JOB_SEARCH_TOOL_COUNTED';
export const RESET_JOB_SEARCH_TOOL_COUNT = 'RESET_JOB_SEARCH_TOOL_COUNT';
export const JOB_SEARCH_UPDATE_OCCUPATION_TRANSLATION = 'JOB_SEARCH_UPDATE_OCCUPATION_TRANSLATION';

export class JobSearchToolSubmittedAction implements Action {
    readonly type = JOB_SEARCH_TOOL_SUBMITTED;

    constructor(public payload: JobSearchToolState) {
    }
}

export class JobSearchToolCountAction implements Action {
    readonly type = JOB_SEARCH_TOOL_COUNT;

    constructor(public payload: JobSearchToolState) {
    }
}

export class JobSearchToolCountedAction implements Action {
    readonly type = JOB_SEARCH_TOOL_COUNTED;

    constructor(public payload: number) {
    }
}

export class ResetJobSearchToolCountAction implements Action {
    readonly type = RESET_JOB_SEARCH_TOOL_COUNT;

    constructor(public payload = {}) {
    }
}

export class JobSearchUpdateOccupationTranslationAction implements Action {
    readonly type = JOB_SEARCH_UPDATE_OCCUPATION_TRANSLATION;

    constructor(public payload: Array<TypeaheadMultiselectModel>) {
    }
}
