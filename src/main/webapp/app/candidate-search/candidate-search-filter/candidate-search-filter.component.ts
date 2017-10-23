import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { CandidateSearchFilter } from '../state-management/state/candidate-search.state';
import { FormBuilder, FormGroup } from '@angular/forms';
import {
    Availability,
    DrivingLicenceCategory,
    Experience,
    ISCED_1997,
    WorkForm
} from '../services/candidate-search-request';
import { Observable } from 'rxjs/Observable';
import { LocalityService } from '../../shared/reference-service/locality.service';
import { LanguageSkillService } from '../services/language-skill.service';
import { Subscription } from 'rxjs/Subscription';
import {
    CantonSuggestion,
    LocalityAutocomplete,
    LocalityInputType,
    LocalitySuggestion
} from '../../shared/reference-service/locality-autocomplete';
import { TypeaheadMultiselectModel } from '../../shared/input-components/index';

@Component({
    selector: 'jr2-candidate-search-filter',
    templateUrl: './candidate-search-filter.component.html',
    styleUrls: ['./candidate-search-filter.component.scss']
})
export class CandidateSearchFilterComponent implements OnInit, OnDestroy {
    @Input() searchFilter: CandidateSearchFilter;
    @Output() searchCandidates = new EventEmitter<CandidateSearchFilter>();

    experiences = Experience;
    availabilities = Availability;
    educationLevels = ISCED_1997;
    workForms = WorkForm;
    drivingLicenceCategories = DrivingLicenceCategory;
    filterForm: FormGroup;

    private subscription: Subscription;

    constructor(private languageSkillService: LanguageSkillService,
                private localityService: LocalityService,
                private fb: FormBuilder) {
    }

    ngOnInit(): void {
        this.filterForm = this.fb.group({
            experience: [this.searchFilter.experience],
            workplace: [this.searchFilter.workplace],
            availability: [this.searchFilter.availability],
            workload: [this.searchFilter.workload],
            workForm: [this.searchFilter.workForm],
            educationLevel: [this.searchFilter.educationLevel],
            drivingLicenceCategory: [this.searchFilter.drivingLicenceCategory],
            languageSkills: [[...this.searchFilter.languageSkills || []]]
        });

        this.subscription = this.filterForm.valueChanges.subscribe((formValue: any) => {
                const searchFilter = Object.assign({}, this.searchFilter, formValue);
                this.searchCandidates.emit(searchFilter)
            }
        );
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    getLanguageOptions(): Observable<Array<string>> {
        return this.languageSkillService.getLanguages();
    }

    fetchLocalitySuggestions = (prefix: string) =>
        this.localityService.fetchSuggestions(prefix, customLocalityAutocompleteMapper);
}

function customLocalityAutocompleteMapper(localityAutocomplete: LocalityAutocomplete): TypeaheadMultiselectModel[] {
    const localities = localityAutocomplete.localities
        .map((o: LocalitySuggestion) =>
            new TypeaheadMultiselectModel(LocalityInputType.LOCALITY, `${o.cantonCode}:${o.regionCode}`, o.city, 0));

    const cantons = localityAutocomplete.cantons
        .map((o: CantonSuggestion) =>
            new TypeaheadMultiselectModel(LocalityInputType.CANTON, String(o.code), o.name + ' (' + o.code + ')', 1));

    return [...localities, ...cantons];
}
