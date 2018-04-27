import { Observable } from 'rxjs/Observable';
import { Store, StoreModule } from '@ngrx/store';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { JobPublicationDetailEffects } from '../../../../../../../main/webapp/app/dashboard/state-management/effects/job-publication-detail.effects';
import { JobPublicationDetailState } from '../../../../../../../main/webapp/app/dashboard/state-management/state/job-publication-detail.state';
import {
    CancellationFailedAction,
    CancellationSucceededAction,
    JobAdvertisementLoadedAction,
    LoadJobAdvertisementAction,
    LoadJobAdvertisementFailedAction,
    SubmitCancellationAction
} from '../../../../../../../main/webapp/app/dashboard/state-management/actions/job-publication-detail.actions';
import { cold, hot } from 'jasmine-marbles';
import { jobSearchReducer } from '../../../../../../../main/webapp/app/job-search/state-management/reducers/job-search.reducers';
import { CancellationData } from '../../../../../../../main/webapp/app/dashboard/dialogs/cancellation-data';
import { HttpClientModule } from '@angular/common/http';
import { JobAdvertisementService } from '../../../../../../../main/webapp/app/shared/job-advertisement/job-advertisement.service';
import { createJobAdvertisement } from '../../../shared/job-publication/utils';

describe('JobPublicationCancelEffects', () => {
    let effects: JobPublicationDetailEffects;
    let actions$: Observable<any>;
    let store: Store<JobPublicationDetailState>;

    const mockJobAdvertisementService = jasmine.createSpyObj('mockJobAdvertisementService', ['findById', 'cancelJobPublication']);

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientModule,
                StoreModule.forRoot({ jobSearch: jobSearchReducer })
            ],
            providers: [
                JobPublicationDetailEffects,
                provideMockActions(() => actions$),
                {
                    provide: JobAdvertisementService,
                    useValue: mockJobAdvertisementService
                }
            ]
        });

        effects = TestBed.get(JobPublicationDetailEffects);
        store = TestBed.get(Store);
    });

    describe('loadJobAdvertisement$', () => {
        it('should return a new JobAdvertisementLoadedAction with the loaded job publication on success', () => {
            const id = 'id';
            const action = new LoadJobAdvertisementAction({ id });

            actions$ = hot('-a', { a: action });

            const jobAdvertisement = createJobAdvertisement(id, 'id-avam');
            const response = cold('-a|', { a: jobAdvertisement });
            mockJobAdvertisementService.findById.and.returnValue(response);

            const jobPublicationLoadedAction = new JobAdvertisementLoadedAction(jobAdvertisement);
            const expected = cold('--b', { b: jobPublicationLoadedAction });

            expect(effects.loadJobAdvertisement$).toBeObservable(expected);
        });

        it('should return a new LoadJobAdvertisementFailedAction on error', () => {
            const id = 'id';
            const action = new LoadJobAdvertisementAction({ id });

            actions$ = hot('-a', { a: action });

            const response = cold('-#|', {}, 'error');
            mockJobAdvertisementService.findById.and.returnValue(response);

            const loadJobPublicationFailedAction = new LoadJobAdvertisementFailedAction('error');
            const expected = cold('--b', { b: loadJobPublicationFailedAction });

            expect(effects.loadJobAdvertisement$).toBeObservable(expected);
        });
    });

    describe('cancelJobAdvertisement$', () => {
        it('should return a new CancellationSucceededAction with the updated job publication on success', () => {
            const cancellationData: CancellationData = {
                id: 'id',
                accessToken: 'token',
                cancellationReason: {
                    positionOccupied: true,
                    occupiedWith: {
                        jobCenter: true,
                        privateAgency: false,
                        self: false
                    }
                }
            };
            const jobAdvertisement = createJobAdvertisement('id', 'id-avam');

            const action = new SubmitCancellationAction(cancellationData);
            actions$ = hot('-a', { a: action });

            const cancellationResponse = cold('-a|', { a: 200 });
            mockJobAdvertisementService.cancelJobPublication.and.returnValue(cancellationResponse);

            const findByIdAndTokenResponse = cold('-a|', { a: jobAdvertisement });
            mockJobAdvertisementService.findById.and.returnValue(findByIdAndTokenResponse);

            const cancellationSucceededAction = new CancellationSucceededAction(jobAdvertisement);
            const expected = cold('---b', { b: cancellationSucceededAction });

            expect(effects.cancelJobAdvertisement$).toBeObservable(expected);
        });

        it('should return a new CancellationFailedAction on error', () => {
            const cancellationData: CancellationData = {
                id: 'id',
                accessToken: 'token',
                cancellationReason: {
                    positionOccupied: true,
                    occupiedWith: {
                        jobCenter: true,
                        privateAgency: false,
                        self: false
                    }
                }
            };

            const action = new SubmitCancellationAction(cancellationData);
            actions$ = hot('-a', { a: action });

            const response = cold('-#|', {}, 'error');
            mockJobAdvertisementService.cancelJobPublication.and.returnValue(response);

            const cancellationFailedAction = new CancellationFailedAction('error');
            const expected = cold('--b', { b: cancellationFailedAction });

            expect(effects.cancelJobAdvertisement$).toBeObservable(expected);
        });
    });
});
