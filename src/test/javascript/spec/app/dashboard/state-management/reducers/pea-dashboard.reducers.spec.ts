import {
    CancellationSucceededAction,
    FilterJobAdvertisementsDashboardAction,
    JobAdvertisementsLoadedAction
} from '../../../../../../../main/webapp/app/dashboard/state-management/actions/pea-dashboard.actions';
import {
    initialState,
    PEADashboardState
} from '../../../../../../../main/webapp/app/dashboard/state-management/state/pea-dashboard.state';
import { peaDashboardReducer } from '../../../../../../../main/webapp/app/dashboard/state-management/reducers/pea-dashboard.reducers';
import { createJobAdvertisement } from '../../../shared/job-publication/utils';

describe('peaDashboardReducer', () => {
    it('should update PEADashboardState for JOB_ADVERTISEMENTS_LOADED action', () => {
        // GIVEN
        const state = Object.assign({}, initialState);
        const jobAdvertisements = [];
        const action = new JobAdvertisementsLoadedAction({
            jobAdvertisements,
            totalCount: 3,
            page: 1
        });

        // WHEN
        const newState: PEADashboardState = peaDashboardReducer(state, action);

        // THEN
        expect(newState.page).toEqual(1);
        expect(newState.totalCount).toEqual(3);
        expect(newState.jobAdvertisements).toEqual(jobAdvertisements);
        expect(newState.jobAdvertisementFilter).toEqual(initialState.jobAdvertisementFilter);
    });

    it('should update DashboardState for FILTER_JOB_ADVERTISEMENTS_DASHBOARD action', () => {
        // GIVEN
        const state = Object.assign({}, initialState);
        const action = new FilterJobAdvertisementsDashboardAction({
            jobTitle: 'Se',
            onlineSinceDays: 2
        });

        // WHEN
        const newState: PEADashboardState = peaDashboardReducer(state, action);

        // THEN
        const expectedState = Object.assign({}, initialState, {
            jobAdvertisementFilter: {
                jobTitle: 'Se',
                onlineSinceDays: 2
            },
            page: 0
        });
        expect(newState).toEqual(expectedState);
    });

    it('should update DashboardState for CANCELLATION_SUCCEEDED action', () => {
        // GIVEN
        const jobPublication1 = createJobAdvertisement('id1', 'id-avam');
        const jobPublication2 = createJobAdvertisement('id2', 'id-avam');
        const jobPublication3 = createJobAdvertisement('id3', 'id-avam');
        const jobPublication4 = createJobAdvertisement('id4', 'id-avam');
        const state = Object.assign({}, initialState, {
            jobAdvertisements: [
                jobPublication1,
                jobPublication2,
                jobPublication3,
                jobPublication4
            ]
        });

        const updatedJobPublication = createJobAdvertisement('id2', 'id-avam');

        const action = new CancellationSucceededAction(updatedJobPublication);

        // WHEN
        const newState: PEADashboardState = peaDashboardReducer(state, action);

        // THEN
        const expectedState = Object.assign({}, initialState, {
            jobAdvertisements: [
                jobPublication1,
                updatedJobPublication,
                jobPublication3,
                jobPublication4
            ]
        });
        expect(newState).toEqual(expectedState);
    });
});
