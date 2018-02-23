import { Inject, Injectable, InjectionToken } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import {
    CantonSuggestion,
    GeoPoint,
    LocalityAutocomplete,
    LocalityInputType,
    LocalitySuggestion
} from './locality-autocomplete';
import { Observer } from 'rxjs/Observer';
import { TypeaheadMultiselectModel } from '../input-components';
import { HttpClient, HttpParams } from '@angular/common/http';

const LOCALITIES_URL = 'referenceservice/api/_search/localities';
const DEFAULT_RESPONSE_SIZE = '10';

export const NAVIGATOR_TOKEN = new InjectionToken<NavigatorGeolocation>('NavigatorGeolocation');

export type LocalityResultMapper<T> = (LocalityAutocomplete) => T;

@Injectable()
export class LocalityService {

    constructor(private http: HttpClient,
                @Inject(NAVIGATOR_TOKEN) private navigator: NavigatorGeolocation) {
    }

    fetchSuggestions<T = TypeaheadMultiselectModel[]>(prefix: string, resultMapper?: LocalityResultMapper<T>,
                                                      distinctByLocalityCity = true): Observable<T> {
        const params = new HttpParams()
            .set('prefix', prefix)
            .set('resultSize', DEFAULT_RESPONSE_SIZE)
            .set('distinctByLocalityCity', distinctByLocalityCity.toString());

        const _resultMapper = resultMapper
            ? resultMapper
            : defaultLocalityAutocompleteMapper as LocalityResultMapper<any>;

        return this.http.get(LOCALITIES_URL, { params })
            .map(_resultMapper)
            .catch(this.handleError);
    }

    getNearestLocality(geoPoint: GeoPoint): Observable<LocalitySuggestion> {
        const params = new HttpParams()
            .set('latitude', geoPoint.latitude.toString())
            .set('longitude', geoPoint.longitude.toString());

        return this.http.get<LocalitySuggestion>(`${LOCALITIES_URL}/nearest`, { params });
    }

    getCurrentPosition(): Observable<GeoPoint> {
        return new Observable((observer: Observer<GeoPoint>) => {
            // Invokes getCurrentPosition method of Geolocation API.
            if ('geolocation' in this.navigator) {
                this.navigator.geolocation.getCurrentPosition(
                    (position: Position) => {
                        observer.next(position.coords);
                        observer.complete();
                    },
                    (error: PositionError) => {
                        observer.error(error);
                    }
                );
            } else {
                observer.error('Geolocation is not available!');
            }
        });
    }

    private handleError(error: Response) {
        // todo: Error handling concept is not defined yet
        return Observable.of([]);
    }
}

function defaultLocalityAutocompleteMapper(localityAutocomplete: LocalityAutocomplete): TypeaheadMultiselectModel[] {
    const localities = localityAutocomplete.localities
        .map((o: LocalitySuggestion, index) =>
            new TypeaheadMultiselectModel(LocalityInputType.LOCALITY, String(o.communalCode), o.city, index));

    const cantons = localityAutocomplete.cantons
        .map((o: CantonSuggestion, index) =>
            new TypeaheadMultiselectModel(LocalityInputType.CANTON, String(o.code),
                o.name + ' (' + o.code + ')', localities.length + index));

    return [...localities, ...cantons];
}
