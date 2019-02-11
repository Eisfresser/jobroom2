import { inject, TestBed } from '@angular/core/testing';
import { JobroomTestModule } from '../../../test.module';
import { LocalityService } from '../../../../../../main/webapp/app/shared/index';
import {
    GeoPoint,
    LocalityAutocomplete,
    LocalityInputType
} from '../../../../../../main/webapp/app/shared/reference-service/locality-autocomplete';
import { TypeaheadMultiselectModel } from '../../../../../../main/webapp/app/shared/input-components';
import { NAVIGATOR_TOKEN } from '../../../../../../main/webapp/app/shared/reference-service/locality.service';
import { HttpTestingController } from '@angular/common/http/testing';

describe('LocalityService', () => {
    const mockNavigator: any = {};
    mockNavigator.geolocation = jasmine.createSpyObj('mockGeolocation', ['getCurrentPosition']);
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [JobroomTestModule],
            providers: [
                LocalityService,
                { provide: NAVIGATOR_TOKEN, useValue: mockNavigator }
            ]
        });

        httpMock = TestBed.get(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    describe('fetchSuggestions', () => {
        it('should call http.get with the correct URL parameters',
            inject([LocalityService], (service: LocalityService) => {
                // WHEN
                service.fetchSuggestions('ber').subscribe();

                // THEN
                httpMock.expectOne((req) =>
                    req.url === '/referenceservice/api/_search/localities'
                    && req.params.get('prefix') === 'ber'
                    && req.params.get('distinctByLocalityCity') === 'true');
            }));

        it('should map the response Locality list without duplicates to an array of TypeaheadMultiselectModel',
            inject([LocalityService], (service: LocalityService) => {
                // GIVEN
                const suggestResponse: LocalityAutocomplete = {
                    localities: [
                        {
                            city: 'Bern',
                            communalCode: 351,
                            cantonCode: 'BE',
                            regionCode: 'BE01',
                            zipCode: '33333'
                        }, {
                            city: 'Bertschikon (Gossau ZH)',
                            communalCode: 115,
                            cantonCode: 'ZH',
                            regionCode: 'ZH12',
                            zipCode: '33333'
                        }
                    ],
                    cantons: [{
                        code: 'BE',
                        name: 'Bern / Berne'
                    }]
                };

                // WHEN
                let model: Array<TypeaheadMultiselectModel>;
                service.fetchSuggestions('bern').subscribe((res: any) => model = res);
                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(suggestResponse);

                // THEN
                expect(model.length).toEqual(3);
                expect(model).toEqual([
                    new TypeaheadMultiselectModel(LocalityInputType.LOCALITY, '351', 'Bern', 0),
                    new TypeaheadMultiselectModel(LocalityInputType.LOCALITY, '115', 'Bertschikon (Gossau ZH)', 1),
                    new TypeaheadMultiselectModel(LocalityInputType.CANTON, 'BE', 'Bern / Berne (BE)', 2),
                ]);

            })
        );
    })
    ;

    describe('getNearestLocality', () => {
        it('should call http.get with the correct URL parameters',
            inject([LocalityService], (service: LocalityService) => {
                // WHEN
                service.getNearestLocality({ latitude: 1111, longitude: 2222 }).subscribe();

                // THEN
                httpMock.expectOne((req) =>
                    req.url === '/referenceservice/api/_search/localities/nearest'
                    && req.params.get('latitude') === '1111'
                    && req.params.get('longitude') === '2222');
            }));
    });

    describe('getCurrentPosition', () => {

        it('should return error if geolocation detection is not permitted by the user',
            inject([LocalityService], (service: LocalityService) => {
                // GIVEN
                const positionError: PositionError = {
                    code: 100,
                    message: 'not allowed',
                    PERMISSION_DENIED: 1,
                    POSITION_UNAVAILABLE: 1,
                    TIMEOUT: 1
                };
                mockNavigator.geolocation.getCurrentPosition.and.callFake(
                    (successCallback: PositionCallback, errorCallback?: PositionErrorCallback) => {
                        errorCallback(positionError);
                    });

                // WHEN
                let error, point;
                service.getCurrentPosition().subscribe((p: GeoPoint) => point = p, (e: any) => error = e);

                // THEN
                expect(point).not.toBeDefined();
                expect(error).toEqual(positionError);
            }));
    });

    it('should return geolocation ', inject([LocalityService], (service: LocalityService) => {
        // GIVEN
        mockNavigator.geolocation.getCurrentPosition.and.callFake((successCallback: PositionCallback) => {
            successCallback({
                coords: {
                    longitude: 101,
                    latitude: 202
                }
            } as Position);
        });

        // WHEN
        let error, point;
        service.getCurrentPosition().subscribe((p: GeoPoint) => point = p, (e: any) => error = e);

        // THEN
        expect(error).not.toBeDefined();
        expect(point).toEqual({
            longitude: 101,
            latitude: 202
        });
    }));
})
;
