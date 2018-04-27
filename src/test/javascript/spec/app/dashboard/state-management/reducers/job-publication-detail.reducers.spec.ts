import {
    initialState,
    JobPublicationDetailState,
    LoadingStatus
} from '../../../../../../../main/webapp/app/dashboard/state-management/state/job-publication-detail.state';
import { jobPublicationDetailReducer } from '../../../../../../../main/webapp/app/dashboard/state-management/reducers/job-publication-detail.reducers';
import {
    CancellationFailedAction,
    CancellationSucceededAction,
    HideErrorMessageAction,
    HideSuccessMessageAction,
    JobAdvertisementLoadedAction,
    LoadJobAdvertisementAction,
    LoadJobAdvertisementFailedAction
} from '../../../../../../../main/webapp/app/dashboard/state-management/actions/job-publication-detail.actions';
import {
    Locale,
    Status
} from '../../../../../../../main/webapp/app/shared/job-publication/job-publication.model';
import { createJobAdvertisement } from '../../../shared/job-publication/utils';
import { JobAdvertisementStatus } from '../../../../../../../main/webapp/app/shared/job-advertisement/job-advertisement.model';

describe('jobPublicationDetailReducer', () => {

    it('should update JobPublicationDetailState for CANCELLATION_SUCCEEDED action', () => {
        // GIVEN
        const jobAdvertisement = createJobAdvertisement();
        const state = Object.assign({}, initialState, { jobAdvertisement });
        const changedJobPublication = Object.assign({}, jobAdvertisement, { status: JobAdvertisementStatus.CANCELLED });
        const action = new CancellationSucceededAction(changedJobPublication);

        // WHEN
        const newState: JobPublicationDetailState = jobPublicationDetailReducer(state, action);

        // THEN
        expect(newState.showCancellationSuccess).toBeTruthy();
        expect(newState.showCancellationError).toBeFalsy();
        expect(newState.jobAdvertisement.status).toEqual(JobAdvertisementStatus.CANCELLED);
    });

    it('should update JobPublicationDetailState for HIDE_SUCCESS_MESSAGE action', () => {
        // GIVEN
        const state = Object.assign({}, initialState, { showCancellationSuccess: true });
        const action = new HideSuccessMessageAction();

        // WHEN

        const newState: JobPublicationDetailState = jobPublicationDetailReducer(state, action);

        // THEN
        expect(newState.showCancellationSuccess).toBeFalsy();
    });

    it('should update JobPublicationDetailState for CANCELLATION_FAILED action', () => {
        // GIVEN
        const state = Object.assign({}, initialState, { showCancellationSuccess: true });
        const action = new CancellationFailedAction();

        // WHEN
        const newState: JobPublicationDetailState = jobPublicationDetailReducer(state, action);

        // THEN
        expect(newState.showCancellationError).toBeTruthy();
        expect(newState.showCancellationSuccess).toBeFalsy();
    });

    it('should update JobPublicationDetailState for HIDE_ERROR_MESSAGE action', () => {
        // GIVEN
        const state = Object.assign({}, initialState, { showCancellationError: true });
        const action = new HideErrorMessageAction();

        // WHEN
        const newState: JobPublicationDetailState = jobPublicationDetailReducer(state, action);

        // THEN
        expect(newState.showCancellationError).toBeFalsy();
    });

    it('should update JobPublicationDetailState for LOAD_JOB_ADVERTISEMENT action', () => {
        // GIVEN
        const jobAdvertisement = createJobAdvertisement();
        const state = Object.assign({}, initialState, { jobAdvertisement });
        const action = new LoadJobAdvertisementAction({
            id: 'id'
        });

        // WHEN
        const newState: JobPublicationDetailState = jobPublicationDetailReducer(state, action);

        // THEN
        expect(newState.jobAdvertisement).toBeNull();
        expect(newState.loadingStatus).toEqual(LoadingStatus.INITIAL);
    });

    it('should update JobPublicationDetailState for JOB_ADVERTISEMENT_LOADED action', () => {
        // GIVEN
        const jobAdvertisement = createJobAdvertisement();
        const state = Object.assign({}, initialState);
        const action = new JobAdvertisementLoadedAction(jobAdvertisement);

        // WHEN
        const newState: JobPublicationDetailState = jobPublicationDetailReducer(state, action);

        // THEN
        expect(newState.jobAdvertisement).toEqual(jobAdvertisement);
        expect(newState.loadingStatus).toEqual(LoadingStatus.OK);
    });

    it('should update JobPublicationDetailState for LOAD_JOB_ADVERTISEMENT_FAILED action', () => {
        // GIVEN
        const action = new LoadJobAdvertisementFailedAction();
        const state = Object.assign({}, initialState);

        // WHEN
        const newState: JobPublicationDetailState = jobPublicationDetailReducer(state, action);

        // THEN
        expect(newState.loadingStatus).toEqual(LoadingStatus.FAILED);
    });
});
