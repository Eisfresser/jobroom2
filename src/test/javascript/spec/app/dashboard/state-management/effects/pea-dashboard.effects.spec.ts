import { Observable } from 'rxjs/Observable';
import { Store, StoreModule } from '@ngrx/store';
import { PEADashboardEffects } from '../../../../../../../main/webapp/app/dashboard/state-management/effects/pea-dashboard.effects';
import { DashboardState } from '../../../../../../../main/webapp/app/dashboard/state-management/state/dashboard.state';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { cold, hot } from 'jasmine-marbles';
import { Principal, ResponseWrapper } from '../../../../../../../main/webapp/app/shared';
import {
    FilterJobAdvertisementsDashboardAction,
    JobAdvertisementsLoadedAction,
    JobAdvertisementsLoadErrorAction,
    LoadNextJobAdvertisementsDashboardPageAction
} from '../../../../../../../main/webapp/app/dashboard/state-management/actions/pea-dashboard.actions';
import { createJobAdvertisement } from '../../../shared/job-publication/utils';
import { dashboardReducer } from '../../../../../../../main/webapp/app/dashboard/state-management/reducers/dahboard.reducers';
import { HttpHeaders } from '@angular/common/http';
import { JobAdvertisementService } from '../../../../../../../main/webapp/app/shared/job-advertisement/job-advertisement.service';

describe('PEADashboardEffects', () => {
    let effects: PEADashboardEffects;
    let actions$: Observable<any>;
    let store: Store<DashboardState>;

    const mockJobAdvertisementService = jasmine.createSpyObj('mockJobAdvertisementService', ['search']);
    const mockPrincipal = jasmine.createSpyObj('mockPrincipal', ['getAuthenticationState']);

    mockPrincipal.getAuthenticationState.and.returnValue(Observable.of({ email: 'email' }));

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                StoreModule.forRoot(dashboardReducer)
            ],
            providers: [
                PEADashboardEffects,
                provideMockActions(() => actions$),
                {
                    provide: JobAdvertisementService,
                    useValue: mockJobAdvertisementService
                },
                {
                    provide: Principal,
                    useValue: mockPrincipal
                }
            ],
        })
        ;

        effects = TestBed.get(PEADashboardEffects);
        store = TestBed.get(Store);
    });

    describe('loadNextJobAdvertisementsPage$', () => {
        const action = new LoadNextJobAdvertisementsDashboardPageAction({ page: 0 });

        it('should return JobAdvertisementsLoadedAction when load next page', () => {
            const jobAdvertisements = [
                createJobAdvertisement()
            ];
            const responseWrapper = new ResponseWrapper(new HttpHeaders({ 'X-Total-Count': '100' }), {
                content: jobAdvertisements,
                totalElements: 100
            }, 200);

            actions$ = hot('-a', { a: action });
            const response = cold('-a|', { a: responseWrapper });
            mockJobAdvertisementService.search.and.returnValue(response);

            const jobPublicationsLoadedAction = new JobAdvertisementsLoadedAction({
                jobAdvertisements,
                totalCount: 100,
                page: 0
            });
            const expected = cold('--b', { b: jobPublicationsLoadedAction });

            expect(effects.loadNextJobAdvertisementsPage$).toBeObservable(expected);
        });

        it('should return JobAdvertisementsLoadErrorAction on error', () => {
            actions$ = hot('-a', { a: action });
            const response = cold('-#');
            mockJobAdvertisementService.search.and.returnValue(response);

            const errorAction = new JobAdvertisementsLoadErrorAction();
            const expected = cold('--b', { b: errorAction });

            expect(effects.loadNextJobAdvertisementsPage$).toBeObservable(expected);
        });
    });

    describe('filterJobAdvertisements$', () => {
        const action = new FilterJobAdvertisementsDashboardAction({
            jobTitle: 'name',
            onlineSinceDays: 3
        });

        it('should return JobAdvertisementsLoadedAction when filter', () => {
            const jobAdvertisements = [
                createJobAdvertisement()
            ];
            const responseWrapper = new ResponseWrapper(new HttpHeaders({ 'X-Total-Count': '100' }), {
                content: jobAdvertisements,
                totalElements: 100
            }, 200);

            actions$ = hot('-a', { a: action });
            const response = cold('-a|', { a: responseWrapper });
            mockJobAdvertisementService.search.and.returnValue(response);

            const jobPublicationsLoadedAction = new JobAdvertisementsLoadedAction({
                jobAdvertisements,
                totalCount: 100,
                page: 0
            });
            const expected = cold('--b', { b: jobPublicationsLoadedAction });

            expect(effects.filterJobAdvertisements$).toBeObservable(expected);
        });

        it('should return JobAdvertisementsLoadErrorAction on error', () => {
            actions$ = hot('-a', { a: action });
            const response = cold('-#');
            mockJobAdvertisementService.search.and.returnValue(response);

            const errorAction = new JobAdvertisementsLoadErrorAction();
            const expected = cold('--b', { b: errorAction });

            expect(effects.filterJobAdvertisements$).toBeObservable(expected);
        });
    });

});
