import { Component, Input, OnInit } from '@angular/core';
import { CandidateProfile, JobExperience } from '../services/candidate';
import { Observable } from 'rxjs/Observable';
import {
    GenderAwareOccupationLabel,
    OccupationPresentationService
} from '../../shared/reference-service';
import { CandidateService } from '../services/candidate.service';

@Component({
    selector: 'jr2-candidate-search-list-item',
    templateUrl: './candidate-search-list-item.component.html',
    styles: []
})
export class CandidateSearchListItemComponent implements OnInit {
    @Input() profile: CandidateProfile;
    @Input() occupationCode: string;

    jobExperience$: Observable<JobExperience>;
    validExperienceData = true;
    isDisplayExperience = false;

    constructor(private occupationPresentationService: OccupationPresentationService,
                private candidateService: CandidateService) {
    }

    ngOnInit(): void {
        const relevantJobExperience = this.candidateService.getRelevantJobExperience(
            this.occupationCode, this.profile.jobExperiences);

        if (relevantJobExperience) {
            this.isDisplayExperience = !!relevantJobExperience.experience;
            this.jobExperience$ = this.occupationPresentationService.findOccupationLabelsByAvamCode(relevantJobExperience.occupationCode)
                .map((occupationLabels: GenderAwareOccupationLabel) => Object.assign({}, relevantJobExperience,
                    {
                        occupation: occupationLabels.default
                    }))
                .share();
        } else {
            this.validExperienceData = false;
        }
    }
}
