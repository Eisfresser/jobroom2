import { CandidateSearchListComponent } from '../../../../../../main/webapp/app/candidate-search/candidate-search-list/candidate-search-list.component';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { JobroomTestModule } from '../../../test.module';
import { Store } from '@ngrx/store';
import { LoadNextPageAction } from '../../../../../../main/webapp/app/candidate-search/state-management/actions/candidate-search.actions';
import { Observable } from 'rxjs/Observable';
import { WINDOW } from '../../../../../../main/webapp/app/shared';
import { TranslateModule } from '@ngx-translate/core';
import { LocaleAwareDecimalPipe } from '../../../../../../main/webapp/app/shared/pipes/locale-aware-number.pipe';

describe('CandidateSearchListComponent', () => {
    const mockStore = jasmine.createSpyObj('mockStore', ['select', 'dispatch']);
    mockStore.select.and.returnValue(Observable.of([]));

    const mockWindow = jasmine.createSpyObj('mockWindow', ['scroll']);

    let component: CandidateSearchListComponent;
    let fixture: ComponentFixture<CandidateSearchListComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                JobroomTestModule,
                TranslateModule.forRoot()
            ],
            declarations: [CandidateSearchListComponent],
            providers: [
                { provide: Store, useValue: mockStore },
                { provide: WINDOW, useValue: mockWindow },
                LocaleAwareDecimalPipe
            ]
        })
            .overrideTemplate(CandidateSearchListComponent, '')
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(CandidateSearchListComponent);
        component = fixture.componentInstance;
        component.occupationCodes = [];
        component.occupationNames = [];
        component.residenceFilterString = '';
        fixture.detectChanges();
    });

    it('should be created', () => {
        expect(component).toBeTruthy();
    });

    describe('onScroll', () => {
        it('should dispatch an LoadNextPageAction', () => {

            // WHEN
            component.onScroll({});

            // THEN
            expect(mockStore.dispatch).toHaveBeenCalledWith(new LoadNextPageAction());
        });
    });
});
