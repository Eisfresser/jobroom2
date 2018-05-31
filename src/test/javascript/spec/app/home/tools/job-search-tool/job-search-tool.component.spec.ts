import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { JobSearchToolComponent } from '../../../../../../../main/webapp/app/home/tools/job-search-tool/job-search-tool.component';
import { LocalityService, OccupationPresentationService } from '../../../../../../../main/webapp/app/shared/reference-service';
import { Observable } from 'rxjs/Observable';
import { Store } from '@ngrx/store';
import { initialState } from '../../../../../../../main/webapp/app/home/state-management/state/job-search-tool.state';
import { TranslateModule } from '@ngx-translate/core';
import { LocaleAwareDecimalPipe } from '../../../../../../../main/webapp/app/shared/pipes/locale-aware-number.pipe';

describe('JobSearchToolComponent', () => {
    const mockOccupationOccupationPresentationService = jasmine.createSpyObj('mockOccupationOccupationPresentationService', ['fetchJobSearchOccupationSuggestions']);
    const mockLocalityService = jasmine.createSpyObj('mockLocalityService', ['fetchSuggestions']);

    const mockStore = jasmine.createSpyObj('mockStore', ['select', 'dispatch']);
    mockStore.select.and.returnValue(Observable.of([]));

    let component: JobSearchToolComponent;
    let fixture: ComponentFixture<JobSearchToolComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                TranslateModule.forRoot()
            ],
            declarations: [JobSearchToolComponent],
            providers: [
                { provide: OccupationPresentationService, useValue: mockOccupationOccupationPresentationService },
                { provide: LocalityService, useValue: mockLocalityService },
                { provide: Store, useValue: mockStore },
                LocaleAwareDecimalPipe
            ]
        })
            .overrideTemplate(JobSearchToolComponent, '')
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(JobSearchToolComponent);
        component = fixture.componentInstance;
        component.jobSearchToolModel = initialState;
        fixture.detectChanges();
    });

    it('should be created', () => {
        expect(component).toBeTruthy();
    });
});
