import {
    Actions,
    CANCELLATION_SUCCEEDED,
    FILTER_JOB_ADVERTISEMENTS_DASHBOARD,
    JOB_ADVERTISEMENTS_LOADED
} from '../actions/pea-dashboard.actions';
import { initialState, PEADashboardState } from '../state/pea-dashboard.state';

export function peaDashboardReducer(state = initialState, action: Actions): PEADashboardState {
    let newState;
    switch (action.type) {
        case JOB_ADVERTISEMENTS_LOADED:
            const { totalCount, page, jobAdvertisements } = action.payload;
            newState = Object.assign({}, state, {
                totalCount,
                page,
                jobAdvertisements
            });
            break;

        case FILTER_JOB_ADVERTISEMENTS_DASHBOARD:
            newState = Object.assign({}, state, {
                jobAdvertisementFilter: action.payload,
                page: 0
            });
            break;

        case CANCELLATION_SUCCEEDED:
            const updatedJobAdvertisement = action.payload;
            const patchedJobAds = state.jobAdvertisements
                .map((jobAd) => jobAd.id === updatedJobAdvertisement.id
                    ? updatedJobAdvertisement
                    : jobAd);
            newState = Object.assign({}, state, {
                jobAdvertisements: patchedJobAds
            });

            break;

        default:
            newState = state;
    }

    return newState;
}
