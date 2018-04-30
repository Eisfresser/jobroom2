import { Component, Input } from '@angular/core';
import { JobAdvertisement, JobDescription } from '../../shared/job-advertisement/job-advertisement.model';

@Component({
    selector: 'jr2-job-search-list-item',
    templateUrl: './job-search-list-item.component.html'
})
export class JobSearchListItemComponent {
    @Input() job: JobAdvertisement;
    @Input() jobDescription: JobDescription;
}
