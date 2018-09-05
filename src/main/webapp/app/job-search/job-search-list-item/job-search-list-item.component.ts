import { Component, Input, OnInit } from '@angular/core';
import { JobAdvertisement, JobDescription } from '../../shared/job-advertisement/job-advertisement.model';
import { Observable } from 'rxjs/Observable';

@Component({
    selector: 'jr2-job-search-list-item',
    templateUrl: './job-search-list-item.component.html'
})
export class JobSearchListItemComponent implements OnInit{
    @Input() job: JobAdvertisement;
    @Input() jobDescription: JobDescription;
    companyAnonymous: boolean;

    ngOnInit(): void {
        this.companyAnonymous = this.job.publication.publicAnonymous || this.job.publication.restrictedAnonymous;
    }
}
