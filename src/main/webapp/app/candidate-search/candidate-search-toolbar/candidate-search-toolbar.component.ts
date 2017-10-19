import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Observable } from 'rxjs/Observable';
import { OccupationService } from '../../shared/reference-service/occupation.service';
import { CandidateSearchFilter } from '../state-management/state/candidate-search.state';
import { Graduation } from '../services/candidate-search-request';
import { MAX_CANDIDATE_LIST_SIZE } from '../../app.constants';
import { OccupationSuggestion } from '../../shared/reference-service/occupation-autocomplete';
import { IMultiSelectOption, IMultiSelectSettings } from 'angular-2-dropdown-multiselect';
import { CantonService } from '../services/canton.service';
import { Subscription } from 'rxjs/Subscription';

@Component({
    selector: 'jr2-candidate-search-toolbar',
    templateUrl: './candidate-search-toolbar.component.html',
    styleUrls: ['./candidate-search-toolbar.component.scss']
})

export class CandidateSearchToolbarComponent implements OnInit, OnDestroy {

    @Input() totalCount: number;
    @Input() loading: boolean;
    @Input() searchFilter: CandidateSearchFilter;
    @Output() searchCandidates = new EventEmitter<CandidateSearchFilter>();

    maxCandidateListSize: number = MAX_CANDIDATE_LIST_SIZE;
    graduationOptions = Graduation;

    cantonOptions$: Observable<IMultiSelectOption[]>;
    multiSelectSettings: IMultiSelectSettings = {
        buttonClasses: 'form-control form-control-lg custom-select',
        containerClasses: '',
        checkedStyle: 'fontawesome',
        isLazyLoad: true,
        dynamicTitleMaxItems: 1
    };

    toolbarForm: FormGroup;
    residence: FormControl;

    private subscription: Subscription;

    constructor(private occupationService: OccupationService,
                private cantonService: CantonService,
                private fb: FormBuilder) {
    }

    ngOnInit() {
        this.residence = this.fb.control(this.searchFilter.residence);
        this.toolbarForm = this.fb.group({
            occupation: [this.searchFilter.occupation],
            graduation: [this.searchFilter.graduation]
        });

        this.cantonOptions$ = this.cantonService.getCantonOptions();

        // todo: Review this:
        // Residence input triggers a value change event on language change,
        // that's why distinctUntilChanged is used.
        const residence$ = this.residence.valueChanges
            .distinctUntilChanged()
            .map((residence: string) => Object.assign(this.toolbarForm.value, { residence }));

        this.subscription = Observable.merge(this.toolbarForm.valueChanges, residence$)
            .subscribe((formValue: any) =>
                this.search(formValue)
            );
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    fetchOccupationSuggestions = (prefix$: Observable<string>) => prefix$
        .filter((prefix: string) => prefix.length > 2)
        .switchMap((prefix: string) => this.occupationService.getOccupations(prefix));

    occupationFormatter = (occupation: OccupationSuggestion) => occupation.name;

    search(formValue: any): void {
        const searchFilter = Object.assign({}, this.searchFilter, formValue);
        this.searchCandidates.emit(searchFilter);
    }
}
