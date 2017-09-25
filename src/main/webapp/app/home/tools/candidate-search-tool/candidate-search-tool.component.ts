import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Observable } from 'rxjs/Observable';
import { OccupationService } from '../../../shared/job-search/service/occupation.service';
import { TypeaheadMultiselectModel } from '../../../shared/input-components';
import {
    Graduation,
    GreaterRegion
} from '../../../candidate-search/services/candidate-search-request';
import { Store } from '@ngrx/store';
import { CandidateSearchToolState } from '../../state-management/state/candidate-search-tool.state';
import { CandidateSearchToolSubmittedAction } from '../../state-management/actions/candidate-search-tool.actions';

@Component({
    selector: 'jr2-candidate-search-tool',
    templateUrl: './candidate-search-tool.component.html'
})
export class CandidateSearchToolComponent implements OnInit {
    @Input() candidateSearchToolModel: CandidateSearchToolState;

    candidateSearchForm: FormGroup;

    greaterRegions = GreaterRegion;
    graduations = Graduation;

    constructor(private occupationService: OccupationService,
                private store: Store<CandidateSearchToolState>,
                private fb: FormBuilder) {
    }

    ngOnInit(): void {
        this.candidateSearchForm = this.fb.group({
            occupation: [this.candidateSearchToolModel.occupation],
            residence: [this.candidateSearchToolModel.residence],
            graduation: [this.candidateSearchToolModel.graduation]
        });
    }

    fetchOccupationSuggestions = (prefix: string): Observable<TypeaheadMultiselectModel[]> => this.occupationService.getOccupations(prefix);

    search(formValue: any) {
        this.store.dispatch(new CandidateSearchToolSubmittedAction(formValue));
    }
}
