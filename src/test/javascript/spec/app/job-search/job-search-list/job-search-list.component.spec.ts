import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable } from 'rxjs/Rx';
import { Store } from '@ngrx/store';
import { JobSearchListComponent } from '../../../../../../main/webapp/app/job-search/job-search-list/job-search-list.component';
import { JobroomTestModule } from '../../../test.module';
import { LoadNextPageAction } from '../../../../../../main/webapp/app/job-search/state-management';
import { TranslateModule } from '@ngx-translate/core';
import { LocaleAwareDecimalPipe } from '../../../../../../main/webapp/app/shared/pipes/locale-aware-number.pipe';

describe('JobSearchListComponent', () => {
    const mockStore = jasmine.createSpyObj('mockStore', ['select', 'dispatch']);
    mockStore.select.and.returnValue(Observable.of([]));

    let component: JobSearchListComponent;
    let fixture: ComponentFixture<JobSearchListComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                JobroomTestModule,
                TranslateModule.forRoot()
            ],
            declarations: [JobSearchListComponent],
            providers: [
                { provide: Store, useValue: mockStore },
                LocaleAwareDecimalPipe
            ]
        })
            .overrideTemplate(JobSearchListComponent, '')
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(JobSearchListComponent);
        component = fixture.componentInstance;
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
