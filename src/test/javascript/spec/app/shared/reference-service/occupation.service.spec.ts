import { fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { HttpTestingController } from '@angular/common/http/testing';
import { TranslateService } from '@ngx-translate/core';
import { JobroomTestModule } from '../../../test.module';
import {
    Occupation,
    OccupationService,
} from '../../../../../../main/webapp/app/shared/reference-service/occupation.service';
import { TypeaheadMultiselectModel } from '../../../../../../main/webapp/app/shared/input-components';
import {
    OccupationAutocomplete,
    OccupationSuggestion
} from '../../../../../../main/webapp/app/shared/reference-service/occupation-autocomplete';
import { OccupationInputType } from '../../../../../../main/webapp/app/shared/reference-service';

describe('OccupationService', () => {
    let httpMock: HttpTestingController;
    let service: OccupationService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [JobroomTestModule],
            providers: [
                OccupationService,
                { provide: TranslateService, useValue: { currentLang: 'de' } }
            ]
        });

        service = TestBed.get(OccupationService);
        httpMock = TestBed.get(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    describe('fetchSuggestions', () => {

        it('should call http.get with the correct URL and parameters', () => {
            // WHEN
            service.fetchSuggestions('info').subscribe();

            // THEN
            httpMock.expectOne((req) =>
                req.url === 'referenceservice/api/_search/occupations/synonym'
                && req.params.get('prefix') === 'info'
                && req.params.get('resultSize') === '10'
                && req.params.get('language') === 'de');
        });

        it('should map the OccupationAutocomplete response to an array of TypeaheadMultiselectModel',
            fakeAsync(() => {
                // GIVEN
                const suggestResponse: OccupationAutocomplete = {
                    occupations: [
                        { code: '00', name: 'Informatiker' },
                        { code: '01', name: 'Bioinformatiker' },
                        { code: '02', name: 'Wirtschaftinformatiker' },
                    ],
                    classifications: [
                        { code: '10', name: 'Berufe der Informatik' }
                    ]
                };

                // WHEN
                let model: Array<TypeaheadMultiselectModel>;
                service.fetchSuggestions('info').subscribe((res: any) => model = res);
                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(suggestResponse);
                tick();

                // THEN
                expect(model.length).toEqual(4);
                expect(model).toEqual([
                    new TypeaheadMultiselectModel(OccupationInputType.OCCUPATION, '00', 'Informatiker', 0),
                    new TypeaheadMultiselectModel(OccupationInputType.OCCUPATION, '01', 'Bioinformatiker', 1),
                    new TypeaheadMultiselectModel(OccupationInputType.OCCUPATION, '02', 'Wirtschaftinformatiker', 2),
                    new TypeaheadMultiselectModel(OccupationInputType.CLASSIFICATION, '10', 'Berufe der Informatik', 3)
                ]);
            }));
    });

    describe('getOccupations', () => {
        it('should call http.get with the correct URL and parameters', () => {
            // WHEN
            service.getOccupations('info').subscribe();

            // THEN
            httpMock.expectOne((req) =>
                req.url === 'referenceservice/api/_search/occupations/synonym'
                && req.params.get('prefix') === 'info'
                && req.params.get('resultSize') === '10'
                && req.params.get('language') === 'de');
        });

        it('should map the OccupationAutocomplete.occupations response to an array of TypeaheadMultiselectModel',
            fakeAsync(() => {
                // GIVEN
                const suggestResponse: OccupationAutocomplete = {
                    occupations: [
                        { code: '00', name: 'Informatiker' },
                        { code: '01', name: 'Bioinformatiker' },
                    ],
                    classifications: [
                        { code: '10', name: 'Berufe der Informatik' }
                    ]
                };

                // WHEN
                let model: Array<OccupationSuggestion>;
                service.getOccupations('info').subscribe((res: any) => model = res);
                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(suggestResponse);
                tick();

                // THEN
                expect(model.length).toEqual(2);
                expect(model).toEqual([
                    { code: '00', name: 'Informatiker' },
                    { code: '01', name: 'Bioinformatiker' },
                ]);
            }));
    });

    describe('findOccupationByCode', () => {
        it('should call http.get with the correct URL and parameters', () => {
            // WHEN
            service.findOccupationByCode(2242422).subscribe();

            // THEN
            httpMock.expectOne((req) =>
                req.url === 'referenceservice/api/occupations'
                && req.params.get('code') === '2242422');
        });

        it('should map the response to Occupation',
            fakeAsync(() => {
                // GIVEN
                const suggestResponse: Occupation = {
                    code: 2242422,
                    id: 'id1',
                    labels: [{
                        en: 'Label'
                    }]
                };

                // WHEN
                let model: Occupation;
                service.findOccupationByCode(2242422).subscribe((res: any) => model = res);
                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(suggestResponse);
                tick();

                // THEN
                expect(model).toEqual(suggestResponse);
            })
        );
    });
});
