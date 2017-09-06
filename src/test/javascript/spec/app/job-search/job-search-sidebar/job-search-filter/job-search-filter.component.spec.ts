import { Observable } from 'rxjs/Observable';
import {
    COMPANY_DEBOUNCE_TIME,
    JobSearchFilterComponent,
    SORT_DATE_ASC
} from '../../../../../../../main/webapp/app/job-search/job-search-sidebar/job-search-filter/job-search-filter.component';
import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { JobroomTestModule } from '../../../../test.module';
import { ReactiveFormsModule } from '@angular/forms';
import { Store } from '@ngrx/store';
import {
    ContractType,
    initialState
} from '../../../../../../../main/webapp/app/job-search/state-management/state/job-search.state';
import { FilterChangedAction } from '../../../../../../../main/webapp/app/job-search/state-management/actions/job-search.actions';

describe('JobSearchFilterComponent', () => {
    const mockStore = jasmine.createSpyObj('mockStore', ['select', 'dispatch']);
    mockStore.select.and.returnValue(Observable.of([]));

    let component: JobSearchFilterComponent;
    let fixture: ComponentFixture<JobSearchFilterComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [JobroomTestModule, ReactiveFormsModule],
            declarations: [JobSearchFilterComponent],
            providers: [
                { provide: Store, useValue: mockStore }
            ]
        })
            .overrideTemplate(JobSearchFilterComponent, '')
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(JobSearchFilterComponent);
        component = fixture.componentInstance;
        component.searchFilter = initialState.searchFilter;
        fixture.detectChanges();
    });

    it('should be created', () => {
        expect(component).toBeTruthy();
    });

    describe('ngOnInit', () => {
        it('should subscribe to filterForm value changes', () => {
            // WHEN
            component.filterForm.setValue({
                contractType: ContractType.Permanent,
                workingTime: [80, 100],
                sort: SORT_DATE_ASC
            }, { emitEvent: true });

            // THEN
            expect(mockStore.dispatch).toHaveBeenCalledWith(new FilterChangedAction({
                contractType: ContractType.Permanent,
                workingTime: [80, 100],
                sort: SORT_DATE_ASC,
                companyName: null
            }));
        });

        it('should subscribe to companyName value changes', fakeAsync(() => {
            // WHEN
            component.companyName.setValue('ab', { emitEvent: true });
            tick(COMPANY_DEBOUNCE_TIME);

            // THEN
            expect(mockStore.dispatch).toHaveBeenCalledWith(new FilterChangedAction({
                contractType: ContractType.All,
                workingTime: [0, 100],
                sort: null,
                companyName: 'ab'
            }));
        }));

        it('should not filter by companyName if companyName.length < 2', fakeAsync(() => {
            // WHEN
            component.companyName.setValue('a', { emitEvent: true });
            tick(COMPANY_DEBOUNCE_TIME);

            // THEN
            expect(mockStore.dispatch).not.toHaveBeenCalledWith(new FilterChangedAction({
                contractType: ContractType.All,
                workingTime: [0, 100],
                sort: null,
                companyName: 'a'
            }));
        }));
    });
});
