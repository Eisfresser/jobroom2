import { Injectable } from '@angular/core';
import { Candidate, CandidateProfile, JobExperience } from './candidate';
import { Observable } from 'rxjs/Observable';
import { CandidateSearchRequest } from './candidate-search-request';
import {
    createPageableURLSearchParams,
    Experience,
    Principal,
    ResponseWrapper
} from '../../shared';
import { CandidateSearchFilter } from '../state-management/state/candidate-search.state';
import { JhiBase64Service } from 'ng-jhipster';
import { OccupationCode, OccupationMapping } from '../../shared/reference-service';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class CandidateService {

    private resourceUrl = SERVER_API_URL + 'candidateservice/api/candidates';
    private searchUrl = SERVER_API_URL + 'candidateservice/api/_search/candidates';
    private countUrl = SERVER_API_URL + 'candidateservice/api/_count/candidates';

    private static convertResponse(res: HttpResponse<any>): ResponseWrapper {
        return new ResponseWrapper(res.headers, res.body, res.status);
    }

    static getBestMatchingJobExperience(occupationCodes: Array<string>, jobExperiences: JobExperience[]) {
        const isMatched = (jobExperience: JobExperience, code: OccupationCode | OccupationMapping) => {
            const { value, type } = code;
            const { avamCode, bfsCode, sbn3Code, sbn5Code } = jobExperience.occupation;
            return (avamCode === value && type.toLowerCase() === 'avam')
                || (bfsCode === value && type.toLowerCase() === 'bfs')
                || (sbn3Code === value && type.toLowerCase() === 'sbn3')
                || (sbn5Code === value && type.toLowerCase() === 'sbn5');
        };
        const hasOccupationCode =
            (occupationCode: OccupationCode) =>
                (jobExperience: JobExperience) => {
                    const matchedByPrimaryOccupation = isMatched(jobExperience, occupationCode);
                    return occupationCode.mapping
                        ? matchedByPrimaryOccupation || isMatched(jobExperience, occupationCode.mapping)
                        : matchedByPrimaryOccupation;
                };

        const matchingExperiences = occupationCodes
            .map(OccupationCode.fromString)
            .map((occupationCode) => jobExperiences.find(hasOccupationCode(occupationCode)))
            .filter((jobExperience) => !!jobExperience)
            .reduce((acc, curr) => {
                const key = JSON.stringify(curr);
                if (!acc[key]) {
                    acc[key] = { count: 0, jobExperience: curr }
                }
                acc[key].count++;

                return acc;
            }, []);

        const matchingExperienceKeys = Object.keys(matchingExperiences);
        if (matchingExperienceKeys.length > 0) {
            const bestMatchingExperienceKey = matchingExperienceKeys
                .sort((k1, k2) => matchingExperiences[k1].count === matchingExperiences[k2].count
                    ? 0
                    : matchingExperiences[k1].count > matchingExperiences[k2].count ? -1 : 1
                )[0];

            return matchingExperiences[bestMatchingExperienceKey].jobExperience;
        } else {
            return null;
        }
    }

    constructor(private http: HttpClient,
                private base64Service: JhiBase64Service,
                private principal: Principal) {
    }

    encodeURISearchFilter(filter: CandidateSearchFilter): string {
        return this.base64Service.encode(JSON.stringify(filter));
    }

    decodeURISearchFilter(URISearchFilter: string): CandidateSearchFilter {
        return JSON.parse(this.base64Service.decode(URISearchFilter));
    }

    findCandidate(candidateProfile: CandidateProfile): Observable<Candidate> {
        return this.canViewCandidateProtectedData(candidateProfile)
            .flatMap((canViewProtectedData) => {
                if (canViewProtectedData) {
                    return this.http.get<Candidate>(`${this.resourceUrl}/${candidateProfile.id}`);
                }
                return Observable.of(null as Candidate);
            });
    }

    private canViewCandidateProtectedData(candidateProfile: CandidateProfile): Observable<boolean> {
        return Observable.fromPromise(
            this.principal
                .hasAnyAuthority(['ROLE_PRIVATE_EMPLOYMENT_AGENT', 'ROLE_PUBLIC_EMPLOYMENT_SERVICE'])
        ).map((isEmploymentAgentOrService) => isEmploymentAgentOrService && candidateProfile.showProtectedData);
    }

    findCandidateProfile(id: string): Observable<CandidateProfile> {
        return this.http.get<CandidateProfile>(`${this.resourceUrl}/profiles/${id}`);
    }

    search(req: CandidateSearchRequest): Observable<ResponseWrapper> {
        const params = createPageableURLSearchParams(req);

        return this.http.post(this.searchUrl, req, { params, observe: 'response' })
            .map((res) => CandidateService.convertResponse(res));
    }

    count(req: CandidateSearchRequest): Observable<number> {
        return this.http.post(this.countUrl, req, { observe: 'response' })
            .map((res) => CandidateService.convertResponse(res))
            .map((wrapper: ResponseWrapper) => Number.parseInt(wrapper.json.totalCount));
    }

    getRelevantJobExperience(occupationCodes: Array<string>, jobExperiences: JobExperience[]): JobExperience {
        jobExperiences = jobExperiences
            .filter((jobExperience) => jobExperience.wanted);

        if (occupationCodes) {
            const bestMatchingJobExperience = CandidateService.getBestMatchingJobExperience(occupationCodes, jobExperiences);
            if (bestMatchingJobExperience) {
                return bestMatchingJobExperience;
            }
        }

        if (!jobExperiences.length) {
            return null;
        }

        const keywordHitJobExperience = jobExperiences
            .find((jobExperience) => jobExperience.remark && jobExperience.remark.indexOf('<em>') > -1);

        if (keywordHitJobExperience) {
            return keywordHitJobExperience;
        }

        const lastJobExperience = jobExperiences
            .find((jobExperience) => jobExperience.lastJob);

        if (lastJobExperience) {
            return lastJobExperience;
        }

        const mostExperienced = jobExperiences
            .sort((a, b) => +Experience[b.experience] - +Experience[a.experience])[0];

        if (mostExperienced) {
            return mostExperienced;
        }

        return jobExperiences[0];
    }

    canSendAnonymousContactEmail(candidate: CandidateProfile): Observable<boolean> {
        return this.principal.isCompanyOrAgent()
            .map((hasAuthority) => hasAuthority
                && candidate
                && candidate.contactTypes
                && candidate.contactTypes.includes('EMAIL'))
    }
}
