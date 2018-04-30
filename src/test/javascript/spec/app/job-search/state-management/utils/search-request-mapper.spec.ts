import {
    createJobSearchRequest,
    createJobSearchRequestFromToolState
} from '../../../../../../../main/webapp/app/job-search/state-management/util/search-request-mapper';
import {
    ContractType, JobSearchFilter, JobSearchQuery,
    Sort
} from '../../../../../../../main/webapp/app/job-search/state-management/state/job-search.state';
import { TypeaheadMultiselectModel } from '../../../../../../../main/webapp/app/shared/input-components';
import { LocalityInputType } from '../../../../../../../main/webapp/app/shared/reference-service/locality-autocomplete';
import { OccupationInputType } from '../../../../../../../main/webapp/app/shared/reference-service/occupation-presentation.service';
import { JobSearchToolState } from '../../../../../../../main/webapp/app/home/state-management/state/job-search-tool.state';
import { JobAdvertisementSearchRequest, JobAdvertisementSearchRequestBody } from '../../../../../../../main/webapp/app/shared/job-advertisement/job-advertisement-search-request';

describe('createJobSearchRequest', () => {
    const defaultQuery: JobSearchQuery = {
        baseQuery: [],
        localityQuery: []
    };

    const defaultFilter: JobSearchFilter = {
        contractType: ContractType.ALL,
        workingTime: [0, 100],
        sort: Sort.RELEVANCE_DESC,
        onlineSince: 60
    };

    xit('should map JobSearchFilter with default sort', () => {
        // WHEN
        const jobSearchRequest: JobAdvertisementSearchRequest = createJobSearchRequest(defaultQuery, defaultFilter);

        // THEN
        expect(jobSearchRequest.sort).toEqual(['_score,desc', 'registrationDate,desc']);
    });

    // TODO: enable after implementing sort
    xit('should map JobSearchFilter with sort by date asc', () => {
        // GIVEN
        const filter: JobSearchFilter = Object.assign({}, defaultFilter, { sort: Sort.DATE_ASC });

        // WHEN
        const jobSearchRequest: JobAdvertisementSearchRequest = createJobSearchRequest(defaultQuery, filter);

        // THEN
        expect(jobSearchRequest.sort).toEqual(['registrationDate,asc', '_score,desc']);
    });

    xit('should map JobSearchFilter with sort by date desc', () => {
        // GIVEN
        const filter: JobSearchFilter = Object.assign({}, defaultFilter, { sort: Sort.DATE_DESC });

        // WHEN
        const jobSearchRequest: JobAdvertisementSearchRequest = createJobSearchRequest(defaultQuery, filter);

        // THEN
        expect(jobSearchRequest.sort).toEqual(['registrationDate,desc', '_score,desc']);
    });

    it('should map JobSearchFilter with default contract type', () => {
        // WHEN
        const jobSearchRequest: JobAdvertisementSearchRequest = createJobSearchRequest(defaultQuery, defaultFilter);

        // THEN
        expect(jobSearchRequest.body.permanent).toEqual(null);
    });

    it('should map JobSearchFilter with permanent contract type', () => {
        // GIVEN
        const filter: JobSearchFilter = Object.assign({}, defaultFilter, { contractType: ContractType.PERMANENT, });

        // WHEN
        const jobSearchRequest: JobAdvertisementSearchRequest = createJobSearchRequest(defaultQuery, filter);

        // THEN
        expect(jobSearchRequest.body.permanent).toBeTruthy();
    });

    it('should map JobSearchFilter with temporary contract type', () => {
        // GIVEN
        const filter: JobSearchFilter = Object.assign({}, defaultFilter, { contractType: ContractType.TEMPORARY, });

        // WHEN
        const jobSearchRequest: JobAdvertisementSearchRequest = createJobSearchRequest(defaultQuery, filter);

        // THEN
        expect(jobSearchRequest.body.permanent).toBeFalsy();
    });

    it('should map JobSearchQuery', () => {
        // GIVEN
        const query: JobSearchQuery = {
            baseQuery: [
                new TypeaheadMultiselectModel(OccupationInputType.FREE_TEXT, 'c1', 'l1'),
                new TypeaheadMultiselectModel(OccupationInputType.OCCUPATION, 'x28:33,avam:11', 'l2'),
                new TypeaheadMultiselectModel(OccupationInputType.OCCUPATION, 'avam:12', 'l22'),
                new TypeaheadMultiselectModel(OccupationInputType.CLASSIFICATION, 'sbn3:111', 'l3')
            ],
            localityQuery: [
                new TypeaheadMultiselectModel(LocalityInputType.LOCALITY, 'c4', 'l4'),
                new TypeaheadMultiselectModel(LocalityInputType.CANTON, 'c5', 'l5')
            ]
        };

        // WHEN
        const jobSearchRequest: JobAdvertisementSearchRequest = createJobSearchRequest(query, defaultFilter);

        // THEN
        expect(jobSearchRequest.body.keywords).toEqual(['l1']);
        expect(jobSearchRequest.body.professionCodes[0].value).toEqual('33');
        expect(jobSearchRequest.body.professionCodes[0].type).toEqual('x28');

        expect(jobSearchRequest.body.professionCodes[1].value).toEqual('11');
        expect(jobSearchRequest.body.professionCodes[1].type).toEqual('avam');

        expect(jobSearchRequest.body.professionCodes[2].value).toEqual('12');
        expect(jobSearchRequest.body.professionCodes[2].type).toEqual('avam');

        expect(jobSearchRequest.body.professionCodes[3].value).toEqual('111');
        expect(jobSearchRequest.body.professionCodes[3].type).toEqual('sbn3');

        expect(jobSearchRequest.body.regionCodes).toEqual(['c4']);
        expect(jobSearchRequest.body.cantonCodes).toEqual(['c5']);
    });

    it('should map JobSearchFilter with default online since', () => {
        // WHEN
        const jobSearchRequest: JobAdvertisementSearchRequest = createJobSearchRequest(defaultQuery, defaultFilter);

        // THEN
        expect(jobSearchRequest.body.onlineSince).toEqual(60);
    })
});

describe('createJobSearchRequestFromToolState', () => {

    const defaultFilter: JobSearchToolState = {
        baseQuery: [],
        localityQuery: [],
        totalCount: -1,
        onlineSince: 33
    };

    it('should map JobSearchToolState with onlineSince value', () => {
        // WHEN
        const jobSearchRequest: JobAdvertisementSearchRequestBody = createJobSearchRequestFromToolState(defaultFilter);

        // THEN
        expect(jobSearchRequest.onlineSince).toEqual(33);
    })
});
