import {
    Component, Input, OnDestroy, OnInit, OnChanges, SimpleChanges
} from '@angular/core';
import { Observable } from 'rxjs/Observable';
import {
    LocalityService,
    LocalitySuggestion,
    OccupationPresentationService
} from '../../shared/reference-service';
import { Store } from '@ngrx/store';
import { JobSearchState, ToolbarChangedAction } from '../state-management';
import { FormBuilder, FormGroup } from '@angular/forms';
import { JobSearchQuery } from '../state-management';
import { LocalityInputType } from '../../shared/reference-service';
import { TypeaheadMultiselectModel } from '../../shared/input-components';
import { ResetFilterAction } from '../state-management';
import { Subject } from 'rxjs/Subject';

@Component({
    selector: 'jr2-job-search-toolbar',
    templateUrl: './job-search-toolbar.component.html',
    styleUrls: ['./job-search-toolbar.component.scss']
})
export class JobSearchToolbarComponent implements OnInit, OnDestroy, OnChanges {

    @Input() loading: boolean;
    @Input() searchQuery: JobSearchQuery;

    @Input()
    set reset(value: number) {
        if (value && this.toolbarForm) {
            this.toolbarForm.reset({
                baseQuery: [...this.searchQuery.baseQuery],
                localityQuery: [...this.searchQuery.localityQuery]
            });
        }
    }

    private unsubscribe$ = new Subject<void>();

    toolbarForm: FormGroup;

    constructor(private occupationPresentationService: OccupationPresentationService,
                private localityService: LocalityService,
                private store: Store<JobSearchState>,
                private fb: FormBuilder) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        const model = changes['searchQuery'];
        if (model && !model.isFirstChange()) {
            const { baseQuery } = model.currentValue;
            this.toolbarForm.get('baseQuery').patchValue(baseQuery, { emitEvent: false });
        }
    }

    ngOnInit(): void {
        this.toolbarForm = this.fb.group({
            baseQuery: [[...this.searchQuery.baseQuery]],
            localityQuery: [[...this.searchQuery.localityQuery]]
        });

        this.toolbarForm.valueChanges
            .takeUntil(this.unsubscribe$)
            .subscribe((formValue: any) =>
                this.search(formValue)
            );
    }

    ngOnDestroy(): void {
        this.unsubscribe$.next();
        this.unsubscribe$.complete();
    }

    search(formValue: any) {
        this.store.dispatch(new ToolbarChangedAction(formValue));
    }

    handleLocalitySelect(locality: LocalitySuggestion) {
        const currentLocality = new TypeaheadMultiselectModel(LocalityInputType.LOCALITY, String(locality.communalCode), locality.city, 0);
        const ctrl = this.toolbarForm.get('localityQuery');
        const exists = !!ctrl.value.find((i: TypeaheadMultiselectModel) => currentLocality.equals(i));

        if (!exists) {
            ctrl.setValue([...ctrl.value, currentLocality]);
        }
    }

    resetFilters(event: any): void {
        event.preventDefault();
        this.store.dispatch(new ResetFilterAction(new Date().getTime()));
    }

    fetchOccupationSuggestions = (query: string): Observable<TypeaheadMultiselectModel[]> =>
        this.occupationPresentationService.fetchJobSearchOccupationSuggestions(query);

    fetchLocalitySuggestions = (query: string): Observable<TypeaheadMultiselectModel[]> =>
        this.localityService.fetchSuggestions(query);
}
