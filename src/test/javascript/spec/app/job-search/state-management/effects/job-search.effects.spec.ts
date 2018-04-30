import {
    JobSearchEffects,
    jobSearchReducer,
    JobSearchState
} from '../../../../../../../main/webapp/app/job-search/state-management';
import { TestBed } from '@angular/core/testing';
import { MockRouter } from '../../../../helpers/mock-route.service';
import { Router } from '@angular/router';
import { Store, StoreModule } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import { cold, getTestScheduler, hot } from 'jasmine-marbles';
import * as actions from '../../../../../../../main/webapp/app/job-search/state-management/actions/job-search.actions';
import {
    InitJobSearchAction,
    NextPageLoadedAction,
    ShowJobListErrorAction
} from '../../../../../../../main/webapp/app/job-search/state-management/actions/job-search.actions';
import { provideMockActions } from '@ngrx/effects/testing';
import { ResponseWrapper } from '../../../../../../../main/webapp/app/shared/model/response-wrapper.model';
import {
    JOB_SEARCH_DEBOUNCE,
    JOB_SEARCH_SCHEDULER
} from '../../../../../../../main/webapp/app/job-search/state-management/effects/job-search.effects';
import {
    LoadNextItemsPageAction,
    LoadNextItemsPageErrorAction,
    NextItemsPageLoadedAction
} from '../../../../../../../main/webapp/app/shared/components/details-page-pagination/state-management/actions/details-page-pagination.actions';
import { HttpHeaders } from '@angular/common/http';
import { createJobAdvertisement } from '../../../shared/job-publication/utils';
import { JobAdvertisementService } from '../../../../../../../main/webapp/app/shared/job-advertisement/job-advertisement.service';

describe('JobSearchEffects', () => {
    let effects: JobSearchEffects;
    let actions$: Observable<any>;
    let store: Store<JobSearchState>;

    const mockJobAdvertisementService = jasmine.createSpyObj('mockJobAdvertisementService', ['searchJobAds']);
    const mockRouter = new MockRouter();

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                StoreModule.forRoot({ jobSearch: jobSearchReducer })
            ],
            providers: [
                JobSearchEffects,
                provideMockActions(() => actions$),
                { provide: JobAdvertisementService, useValue: mockJobAdvertisementService },
                { provide: Router, useValue: mockRouter },
                { provide: JOB_SEARCH_SCHEDULER, useFactory: getTestScheduler },
                { provide: JOB_SEARCH_DEBOUNCE, useValue: 30 }
            ],
        });

        effects = TestBed.get(JobSearchEffects);
        store = TestBed.get(Store);
    });

    describe('initJobSearch', () => {
        const action = new InitJobSearchAction();

        it('should return new JobListLoadedAction if store is in initial state', () => {
            const jobList = [
                createJobAdvertisement('0')
            ];
            const responseWrapper = new ResponseWrapper(new HttpHeaders({ 'X-Total-Count': '100' }), jobList, 200);

            actions$ = hot('-a', { a: action });
            const response = cold('-a|', { a: responseWrapper });
            mockJobAdvertisementService.searchJobAds.and.returnValue(response);

            const jobListLoadedAction = new actions.JobListLoadedAction({
                jobList,
                totalCount: 100,
                page: 0
            });
            const expected = cold('--b|', { b: jobListLoadedAction });

            expect(effects.initJobSearch$).toBeObservable(expected);
        });

        it('should not return anything if store is not in initial state', () => {

            const loadJobListAction = new actions.JobListLoadedAction({
                jobList: [
                    createJobAdvertisement('0')
                ],
                totalCount: 100,
                page: 1
            });
            store.dispatch(loadJobListAction);

            actions$ = hot('-a', { a: action });

            const expected = cold('-');

            expect(effects.initJobSearch$).toBeObservable(expected);
        });
    });

    describe('loadJobList$', () => {
        it('should return a new JobListLoadedAction with the loaded jobs on success', () => {
            const action = new actions.ToolbarChangedAction({
                baseQuery: [],
                localityQuery: []
            });
            const jobList = [
                createJobAdvertisement('0'),
                createJobAdvertisement('1'),
                createJobAdvertisement('2')
            ];
            const responseWrapper = new ResponseWrapper(new HttpHeaders({ 'X-Total-Count': '100' }), jobList, 200);

            actions$ = hot('-a---', { a: action });
            const response = cold('-a|', { a: responseWrapper });
            mockJobAdvertisementService.searchJobAds.and.returnValue(response);

            const jobListLoadedAction = new actions.JobListLoadedAction({
                jobList,
                totalCount: 100,
                page: 0
            });
            const expected = cold('-----b', { b: jobListLoadedAction });

            expect(effects.loadJobList$).toBeObservable(expected);
        });

        it('should return a new ShowJobListErrorAction on error', () => {
            const action = new actions.ToolbarChangedAction({
                baseQuery: [],
                localityQuery: []
            });

            actions$ = hot('-a---', { a: action });
            const response = cold('-#|', {}, 'error');
            mockJobAdvertisementService.searchJobAds.and.returnValue(response);

            const showJobListErrorAction = new actions.ShowJobListErrorAction('error');
            const expected = cold('-----b', { b: showJobListErrorAction });

            expect(effects.loadJobList$).toBeObservable(expected);
        });
    });

    describe('loadNextPage$', () => {
        it('should return a new NextPageLoadedAction with the loaded jobs on success', () => {
            const jobList = [
                createJobAdvertisement('0'),
                createJobAdvertisement('1'),
                createJobAdvertisement('2')
            ];
            const responseWrapper = new ResponseWrapper(new HttpHeaders({ 'X-Total-Count': '100' }), jobList, 200);
            const action = new actions.LoadNextPageAction();

            actions$ = hot('-a', { a: action });
            const response = cold('-a|', { a: responseWrapper });
            mockJobAdvertisementService.searchJobAds.and.returnValue(response);

            const nextPageLoadedAction = new actions.NextPageLoadedAction(jobList);
            const expected = cold('--b', { b: nextPageLoadedAction });

            expect(effects.loadNextPage$).toBeObservable(expected);
        });

        it('should return a new ShowJobListErrorAction on error', () => {
            const action = new actions.LoadNextPageAction();

            actions$ = hot('-a', { a: action });
            const response = cold('-#', {}, 'error');
            mockJobAdvertisementService.searchJobAds.and.returnValue(response);

            const showJobListErrorAction = new actions.ShowJobListErrorAction('error');
            const expected = cold('--b', { b: showJobListErrorAction });

            expect(effects.loadNextPage$).toBeObservable(expected);
        });
    });

    describe('nextItemsPageLoaded$', () => {
        const job1 = createJobAdvertisement('0');
        const jobList = [job1];

        it('should return NextItemsPageLoadedAction on success', () => {
            const loadNextItemsPageAction = new LoadNextItemsPageAction({
                feature: 'job-detail'
            });
            const nextPageLoadedAction = new NextPageLoadedAction(jobList);

            actions$ = hot('-a-b', {
                a: loadNextItemsPageAction,
                b: nextPageLoadedAction
            });

            const nextItemsPageLoadedAction = new NextItemsPageLoadedAction({
                feature: 'job-detail',
                item: job1
            });
            const expected = cold('---b', { b: nextItemsPageLoadedAction });

            expect(effects.nextItemsPageLoaded$).toBeObservable(expected);
        });

        it('should return LoadNextItemsPageErrorAction on error', () => {
            const loadNextItemsPageAction = new LoadNextItemsPageAction({
                feature: 'job-detail'
            });
            const showJobListErrorAction = new ShowJobListErrorAction({});

            actions$ = hot('-a-b', {
                a: loadNextItemsPageAction,
                b: showJobListErrorAction
            });

            const loadNextItemsPageErrorAction = new LoadNextItemsPageErrorAction({
                feature: 'job-detail'
            });
            const expected = cold('---b', { b: loadNextItemsPageErrorAction });

            expect(effects.nextItemsPageLoaded$).toBeObservable(expected);
        });
    });
});
