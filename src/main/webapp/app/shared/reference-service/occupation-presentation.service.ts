import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { TYPEAHEAD_QUERY_MIN_LENGTH } from '../../app.constants';
import {
    OccupationLabel,
    OccupationLabelAutocomplete,
    OccupationLabelData,
    OccupationLabelService,
    OccupationLabelSuggestion
} from './occupation-label.service';
import { TypeaheadMultiselectModel } from '../input-components/typeahead/typeahead-multiselect-model';
import { OccupationCode } from './occupation-code';

export class OccupationInputType {
    static OCCUPATION = 'occupation';
    static CLASSIFICATION = 'classification';
    static FREE_TEXT = 'free-text';
}

export interface SuggestionLoaderFn<T> {
    (term$: Observable<string>): Observable<T>;
}

export interface FormatterFn<T> {
    (obj: T): string;
}

// todo: Review if it is possible to use the same model interface for the TypeaheadMultiselect and a suggest input field.
export interface OccupationOption {
    key: string;
    label: string;
}

export interface GenderAwareOccupationLabel {
    default: string;
    male: string;
    female: string;
}

interface OccupationLabelDataCache {
    [key: string]: OccupationLabelData;
}

@Injectable()
export class OccupationPresentationService {
    private occupationLabelDataCache: OccupationLabelDataCache = {};

    constructor(private occupationLabelService: OccupationLabelService) {
    }

    findOccupationLabelsByAvamCode(avamCode: number | string, language: string): Observable<GenderAwareOccupationLabel> {
        const extractCode = () => {
            if (typeof avamCode === 'string') {
                return +avamCode.split(':')[1];
            }
            return avamCode;
        };
        return this.findOccupationLabelsByCode(new OccupationCode(extractCode(), 'avam').toString(), language);
    }

    findOccupationLabelsByBFSCode(bfsCode: number, language: string): Observable<GenderAwareOccupationLabel> {
        return this.findOccupationLabelsByCode(new OccupationCode(bfsCode, 'bfs').toString(), language);
    }

    findOccupationLabelsByCode(occupationCodeString: string, language: string): Observable<GenderAwareOccupationLabel> {
        const labelDataMapper = (labelData: OccupationLabelData) => Object.assign({}, {
            default: labelData['default'],
            male: labelData['m'],
            female: labelData['f']
        });

        const cacheKey = occupationCodeString + '_' + language;
        const cachedOccupation = this.occupationLabelDataCache[cacheKey];
        if (cachedOccupation) {
            return Observable.of(this.occupationLabelDataCache[cacheKey])
                .map(labelDataMapper);
        } else {
            return this.occupationLabelService.getOccupationLabelsByKey(occupationCodeString)
                .do((labelData: OccupationLabelData) => this.occupationLabelDataCache[cacheKey] = labelData)
                .map(labelDataMapper);
        }
    }

    fetchJobSearchOccupationSuggestions(query: string): Observable<Array<TypeaheadMultiselectModel>> {
        const occupationLabelMapper =
            (type: string) =>
                (startIdx: number) =>
                    (o: OccupationLabel | OccupationLabelSuggestion, idx: number) => {
                        const avamMapping = o['mappings'] && o['mappings'].AVAM && o.type === 'X28'
                            ? { value: o['mappings'].AVAM, type: 'AVAM' }
                            : null;
                        const code = new OccupationCode(o.code, o.type, null, avamMapping);

                        return new TypeaheadMultiselectModel(type, code.toString(), o.label, idx + startIdx);
                    };

        const occupationMapper = occupationLabelMapper(OccupationInputType.OCCUPATION);
        const classificationMapper = occupationLabelMapper(OccupationInputType.CLASSIFICATION);

        return this.occupationLabelService.suggestOccupation(query, ['x28', 'sbn3', 'sbn5'])
            .map((occupationAutocomplete: OccupationLabelAutocomplete) => {
                const { occupations, classifications } = occupationAutocomplete;

                const mappedOccupations = occupations.map(occupationMapper(0));
                const mappedClassifications = classifications.map(classificationMapper(occupations.length));

                return [...mappedOccupations, ...mappedClassifications];
            });
    }

    fetchCandidateSearchOccupationSuggestions = (query: string): Observable<Array<TypeaheadMultiselectModel>> => {
        const occupationLabelMapper =
            (type: string) =>
                (startIdx: number) =>
                    (o: OccupationLabel | OccupationLabelSuggestion, idx: number) => {
                        const bfsCodeMapping = o['mappings'] && o['mappings'].BFS && o.type === 'AVAM'
                            ? { value: o['mappings'].BFS, type: 'BFS' }
                            : null;
                        const code = new OccupationCode(o.code, o.type, null, bfsCodeMapping);

                        return new TypeaheadMultiselectModel(type, code.toString(), o.label, idx + startIdx);
                    };

        const occupationMapper = occupationLabelMapper(OccupationInputType.OCCUPATION);
        const classificationMapper = occupationLabelMapper(OccupationInputType.CLASSIFICATION);

        return this.occupationLabelService.suggestOccupation(query, ['avam', 'sbn3', 'sbn5'])
            .map((occupationAutocomplete: OccupationLabelAutocomplete) => {
                const { occupations, classifications } = occupationAutocomplete;

                const mappedOccupations = occupations.map(occupationMapper(0));
                const mappedClassifications = classifications.map(classificationMapper(occupations.length));

                return [...mappedOccupations, ...mappedClassifications];
            })
    };

    fetchJobPublicationOccupationSuggestions = (prefix$: Observable<string>): Observable<Array<OccupationOption>> =>
        prefix$
            .switchMap((prefix: string) => prefix.length < TYPEAHEAD_QUERY_MIN_LENGTH
                ? Observable.of([])
                : this.occupationLabelService.suggestOccupation(prefix, ['avam'])
                    .map((autoComplete: OccupationLabelAutocomplete) => autoComplete.occupations)
                    .map((occupations: OccupationLabelSuggestion[]) =>
                        occupations.map((o: OccupationLabelSuggestion) => Object.assign({}, {
                            key: new OccupationCode(o.code, 'avam').toString(),
                            label: o.label
                        })))
            );

    occupationFormatter = (occupationOption: OccupationOption) => occupationOption.label ? occupationOption.label : '';
}
