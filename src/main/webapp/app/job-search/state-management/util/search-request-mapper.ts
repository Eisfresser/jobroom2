import {
    ContractType, JobSearchFilter, JobSearchQuery,
    Sort
} from '../state/job-search.state';
import { TypeaheadMultiselectModel } from '../../../shared/input-components';
import {
    LocalityInputType,
    OccupationInputType
} from '../../../shared/reference-service';
import { ITEMS_PER_PAGE } from '../../../shared/constants/pagination.constants';
import { JobSearchToolState } from '../../../home/state-management/state/job-search-tool.state';
import { OccupationCode } from '../../../shared/reference-service/occupation-code';
import { JobAdvertisementSearchRequest, JobAdvertisementSearchRequestBody, ProfessionCode } from '../../../shared/job-advertisement/job-advertisement-search-request';

const toCode = (value: TypeaheadMultiselectModel) => value.code;
const toLabel = (value: TypeaheadMultiselectModel) => value.label;
const byValue = (type: string) => (value: TypeaheadMultiselectModel) => value.type === type;

export function createJobSearchRequest(searchQuery: JobSearchQuery, searchFilter: JobSearchFilter, page = 0): JobAdvertisementSearchRequest {
    const { baseQuery, localityQuery } = searchQuery;
    const { companyName, onlineSince } = searchFilter;

    let request = populateBaseQuery({}, baseQuery);
    request = populateLocalityQuery(request, localityQuery);

    const permanent = mapContractType(searchFilter.contractType);
    const sort = mapSort(searchFilter.sort);
    const regionCodes = [];

    const body = Object.assign({
        regionCodes,
        permanent,
        workloadPercentageMin: searchFilter.workingTime[0],
        workloadPercentageMax: searchFilter.workingTime[1],
        companyName,
        onlineSince,
    }, request);

    return {
        page,
        size: ITEMS_PER_PAGE,
        // sort, //TODO: fix
        body
    };
}

function toProfessionCode(occupation: OccupationCode): ProfessionCode {
    return {
        type: occupation.type,
        value: occupation.value.toString()
    };
}

function populateBaseQuery(request, baseQuery: Array<TypeaheadMultiselectModel>) {
    const keywords = baseQuery.filter(byValue(OccupationInputType.FREE_TEXT)).map(toLabel);
    const occupations = baseQuery.filter(byValue(OccupationInputType.OCCUPATION))
        .map(toCode)
        .map((code: string) => code.split(','))
        .reduce((prev: string[], curr: string[]) => prev.concat(curr), [])
        .map(OccupationCode.fromString)
        .map(toProfessionCode);
    const classifications = baseQuery.filter(byValue(OccupationInputType.CLASSIFICATION))
        .map(toCode)
        .map(OccupationCode.fromString)
        .map(toProfessionCode);
    const professionCodes = [...occupations, ...classifications];

    return Object.assign({}, request, { keywords, professionCodes });
}

function populateLocalityQuery(request, localityQuery: Array<TypeaheadMultiselectModel>) {
    const regionCodes = localityQuery.filter(byValue(LocalityInputType.LOCALITY)).map(toCode);
    const cantonCodes = localityQuery.filter(byValue(LocalityInputType.CANTON)).map(toCode);
    return Object.assign({}, request, { regionCodes, cantonCodes });
}

export function createJobSearchRequestFromToolState(toolState: JobSearchToolState): JobAdvertisementSearchRequestBody {
    const { baseQuery, localityQuery, onlineSince } = toolState;
    const request = populateBaseQuery({ onlineSince }, baseQuery);
    return populateLocalityQuery(request, localityQuery);
}

function mapContractType(contractType: ContractType): boolean {
    let contractTypeFlag;
    if (contractType === ContractType.PERMANENT) {
        contractTypeFlag = true;
    } else if (contractType === ContractType.TEMPORARY) {
        contractTypeFlag = false;
    } else {
        contractTypeFlag = null;
    }

    return contractTypeFlag;
}

function mapSort(sort: Sort): string {
    let sortArray;
    if (sort === Sort.DATE_ASC) {
        sortArray = ['registrationDate,asc', '_score,desc'];
    } else if (sort === Sort.DATE_DESC) {
        sortArray = ['registrationDate,desc', '_score,desc'];
    } else {
        sortArray = ['_score,desc', 'registrationDate,desc'];
    }

    return sortArray;
}
