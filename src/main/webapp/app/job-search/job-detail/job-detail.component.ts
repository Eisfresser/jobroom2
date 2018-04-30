import {
    AfterViewInit,
    Component,
    ElementRef,
    HostListener,
    ViewChild
} from '@angular/core';
import { Observable } from 'rxjs/Observable';
import {
    JobCenter,
    ReferenceService
} from '../../shared/reference-service/reference.service';
import {
    getJobList,
    getSelectedJob,
    getTotalJobCount,
    JobSearchState
} from '../state-management/state/job-search.state';
import { Store } from '@ngrx/store';
import { TOOLTIP_AUTO_HIDE_TIMEOUT } from '../../app.constants';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { JobAdvertisement, JobDescription, SourceSystem } from '../../shared/job-advertisement/job-advertisement.model';
import { JobAdvertisementUtils } from '../../dashboard/job-advertisement.utils';
import { CoreState, getLanguage } from '../../shared/state-management/state/core.state';

@Component({
    selector: 'jr2-job-detail',
    templateUrl: './job-detail.component.html',
    styleUrls: [
        './job-detail.scss'
    ]
})
export class JobDetailComponent implements AfterViewInit {
    job$: Observable<JobAdvertisement>;
    jobDescription$: Observable<JobDescription>;
    jobList$: Observable<JobAdvertisement[]>;
    jobCenter$: Observable<JobCenter>;
    jobListTotalSize$: Observable<number>;
    externalJobDisclaimerClosed = false;

    @ViewChild('copyToClipboard')
    copyToClipboardElementRef: ElementRef;

    @ViewChild(NgbTooltip)
    clipboardTooltip: NgbTooltip;

    constructor(private referenceService: ReferenceService,
                private store: Store<JobSearchState>,
                private coreStore: Store<CoreState>) {
        this.job$ = this.store.select(getSelectedJob);
        this.jobList$ = this.store.select(getJobList);
        this.jobListTotalSize$ = this.store.select(getTotalJobCount);
        this.jobCenter$ = this.job$
            .filter((job) => !!job)
            .map((job) => job.jobCenterCode)
            .filter((jobCenterCode) => !!jobCenterCode)
            .switchMap((jobCenterCode) => this.referenceService.resolveJobCenter(jobCenterCode));

        this.jobDescription$ = coreStore.select(getLanguage)
            .combineLatest(this.job$)
            .map(([lang, job]: [string, JobAdvertisement]) => JobAdvertisementUtils.getJobDescription(job, lang));
    }

    isExternalJobDisclaimerShown(job: JobAdvertisement) {
        return job.sourceSystem === SourceSystem.EXTERN && !this.externalJobDisclaimerClosed;
    }

    ngAfterViewInit(): void {
        window.scroll(0, 0);
    }

    printJob() {
        window.print();
    }

    getJobUrl() {
        return window.location.href;
    }

    onCopyLink(): void {
        this.clipboardTooltip.open();
        setTimeout(() => this.clipboardTooltip.close(), TOOLTIP_AUTO_HIDE_TIMEOUT)
    }

    @HostListener('document:click', ['$event.target'])
    onClick(targetElement: HTMLElement): void {
        if (!targetElement) {
            return;
        }

        if (!this.copyToClipboardElementRef.nativeElement.contains(targetElement)) {
            this.clipboardTooltip.close();
        }
    }
}
