import {
    initialState,
    JobPublicationDetailState,
    LoadingStatus
} from '../state/job-publication-detail.state';
import {
    Actions,
    CANCELLATION_FAILED,
    CANCELLATION_SUCCEEDED,
    HIDE_ERROR_MESSAGE,
    HIDE_SUCCESS_MESSAGE,
    JOB_ADVERTISEMENT_LOADED,
    LOAD_JOB_ADVERTISEMENT,
    LOAD_JOB_ADVERTISEMENT_FAILED,
} from '../actions/job-publication-detail.actions';

export function jobPublicationDetailReducer(state = initialState, action: Actions): JobPublicationDetailState {
    let newState;
    switch (action.type) {
        case CANCELLATION_SUCCEEDED:
            const updatedJobAdvertisement = Object.assign({}, action.payload);
            newState = Object.assign({}, state, {
                jobAdvertisement: updatedJobAdvertisement,
                showCancellationSuccess: true,
                showCancellationError: false,
            });
            break;

        case HIDE_SUCCESS_MESSAGE:
            newState = Object.assign({}, state, { showCancellationSuccess: false });
            break;

        case CANCELLATION_FAILED:
            newState = Object.assign({}, state, {
                showCancellationError: true,
                showCancellationSuccess: false
            });
            break;

        case HIDE_ERROR_MESSAGE:
            newState = Object.assign({}, state, { showCancellationError: false });
            break;

        case LOAD_JOB_ADVERTISEMENT:
            newState = Object.assign({}, state, {
                jobAdvertisement: null,
                loadingStatus: LoadingStatus.INITIAL
            });
            break;

        case JOB_ADVERTISEMENT_LOADED:
            const jobAdvertisement = Object.assign({}, action.payload);
            newState = Object.assign({}, state, {
                jobAdvertisement,
                loadingStatus: LoadingStatus.OK
            });
            break;

        case LOAD_JOB_ADVERTISEMENT_FAILED:
            newState = Object.assign({}, state, {
                loadingStatus: LoadingStatus.FAILED
            });
            break;

        default:
            newState = state;
    }

    return newState;
}
