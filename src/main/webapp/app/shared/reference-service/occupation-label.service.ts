import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { HttpClient, HttpParams } from '@angular/common/http';

const DEFAULT_RESPONSE_SIZE = '10';
const OCCUPATION_LABEL_RESOURCE_SEARCH_URL = 'referenceservice/api/_search/occupations/label';
const OCCUPATION_LABEL_RESOURCE_URL = 'referenceservice/api/occupations/label';

export interface OccupationLabelAutocomplete {
    occupations: OccupationLabelSuggestion[];
    classifications: OccupationLabel[];
}

interface Map<T> {
    [key: string]: T;
}

export interface OccupationLabelSuggestion {
    id: string;
    code: number;
    type: string;
    language: string;
    classifier: string;
    label: string;
    mappings: Map<number>;
}

export interface OccupationLabel {
    id: string;
    code: number;
    type: string;
    language: string;
    classifier: string;
    label: string;
}

export interface OccupationLabelData {
    [key: string]: string;
}

export interface OccupationLabelMapping {
    id: string;
    bfsCode: number;
    avamCode: number;
    sbn3Code: number;
    sbn5Code: number;
    description: string;
}

/**
 * This service is supposed to be a data service. The UI components should use the OccupationPresentationService
 * instead of this.
 */
@Injectable()
export class OccupationLabelService {
    constructor(private http: HttpClient) {
    }

    suggestOccupation(prefix: string, types: Array<string>): Observable<OccupationLabelAutocomplete> {
        const params = new HttpParams()
            .set('prefix', prefix)
            .set('types', types.join(','))
            .set('resultSize', DEFAULT_RESPONSE_SIZE);

        return this.http.get<OccupationLabelAutocomplete>(OCCUPATION_LABEL_RESOURCE_SEARCH_URL, { params });
    }

    getOccupationLabelsByCodeAndType(code: number, type: string): Observable<OccupationLabelData> {
        return this.getOccupationLabelsByCodeAndTypeAndClassifier(code, type, null);
    }

    getOccupationLabelsByCodeAndTypeAndClassifier(code: number, type: string, classifier: string): Observable<OccupationLabelData> {
        let url = OCCUPATION_LABEL_RESOURCE_URL + '/' + type + '/' + code;
        if (classifier) {
            url += '/' + classifier;
        }

        return this.http.get<OccupationLabelData>(url);
    }

    getOccupationLabelsByKey(key: string): Observable<OccupationLabelData> {
        return this.http.get<OccupationLabelData>(`${OCCUPATION_LABEL_RESOURCE_URL}/${key.replace(':', '/')}`);
    }

    getOccupationMappingByAvamCode(code: number): Observable<OccupationLabelMapping> {
        return this.getOccupationMapping(code, 'avam');
    }

    getOccupationMappingByBFSCode(code: number): Observable<OccupationLabelMapping> {
        return this.getOccupationMapping(code, 'bfs');
    }

    getOccupationMappingByX28Code(code: number): Observable<OccupationLabelMapping> {
        return this.getOccupationMapping(code, 'x28');
    }

    private getOccupationMapping(code: number, type: string): Observable<OccupationLabelMapping> {
        return this.http.get<OccupationLabelMapping>(`${OCCUPATION_LABEL_RESOURCE_URL}/mapping/${type}/${code}`);
    }
}
