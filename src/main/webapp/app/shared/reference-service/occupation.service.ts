import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Rx';
import { TranslateService } from '@ngx-translate/core';
import {
    ClassificationSuggestion,
    OccupationAutocomplete,
    OccupationSuggestion
} from './occupation-autocomplete';
import { TypeaheadMultiselectModel } from '../input-components';
import { OccupationInputType } from './occupation-presentation.service';
import { HttpClient, HttpParams } from '@angular/common/http';

const DEFAULT_RESPONSE_SIZE = '10';
const SEARCH_URL = 'referenceservice/api/_search/occupations/synonym';
const OCCUPATIONS_URL = 'referenceservice/api/occupations';

export interface Occupation {
    code: number;
    id: string;
    labels: any;
}

interface OccupationCache {
    [key: string]: Occupation
}

/**
 * This service is getting deprecated. Use the OccupationPresentationService and OccupationLabelService
 * service classes instead of this.
 *
 * @deprecated
 */
@Injectable()
export class OccupationService {
    private occupationCache: OccupationCache = {};

    private static mapOccupationSuggestions(occupationSuggestions: OccupationSuggestion[], startIndex = 0): TypeaheadMultiselectModel[] {
        return occupationSuggestions
            .map((o: OccupationSuggestion, index: number) =>
                new TypeaheadMultiselectModel(OccupationInputType.OCCUPATION, o.code, o.name, startIndex + index));
    }

    private static mapClassificationSuggestions(classificationSuggestions: ClassificationSuggestion[], startIndex = 0): TypeaheadMultiselectModel[] {
        return classificationSuggestions
            .map((c: ClassificationSuggestion, index: number) =>
                new TypeaheadMultiselectModel(OccupationInputType.CLASSIFICATION, c.code, c.name, startIndex + index));
    }

    constructor(private http: HttpClient, private translateService: TranslateService) {
    }

    fetchSuggestions(query: string): Observable<TypeaheadMultiselectModel[]> {
        return this.fetchSuggestionsInternal(query)
            .map((occupationAutocomplete) => {
                const occupations = OccupationService.mapOccupationSuggestions(occupationAutocomplete.occupations);
                const classifications = OccupationService.mapClassificationSuggestions(occupationAutocomplete.classifications, occupations.length);

                return [...occupations, ...classifications];
            })
            .catch(this.handleError);
    }

    getOccupations(query: string): Observable<OccupationSuggestion[]> {
        return this.fetchSuggestionsInternal(query)
            .map((occupationAutocomplete) => occupationAutocomplete.occupations)
            .catch(this.handleError);
    }

    findOccupationByCode(code: number): Observable<Occupation> {
        const currentLang = this.translateService.currentLang;
        const cacheKey = code + '_' + currentLang;
        const cachedOccupation = this.occupationCache[cacheKey];
        if (cachedOccupation) {
            return Observable.of(this.occupationCache[cacheKey]);
        }

        const params = new HttpParams()
            .set('code', code.toString())
            .set('language', currentLang);

        return this.http.get<Occupation>(OCCUPATIONS_URL, { params })
            .do((occupation: Occupation) => this.occupationCache[cacheKey] = occupation)
            .map((occupation: Occupation) => occupation)
    }

    private fetchSuggestionsInternal(query: string): Observable<OccupationAutocomplete> {
        const params = new HttpParams()
            .set('prefix', query)
            .set('resultSize', DEFAULT_RESPONSE_SIZE)
            .set('language', this.translateService.currentLang);

        return this.http.get<OccupationAutocomplete>(SEARCH_URL, { params });
    }

    private handleError(error: Response) {
        // todo: Error handling concept is not defined yet
        return Observable.of([]);
    }
}
