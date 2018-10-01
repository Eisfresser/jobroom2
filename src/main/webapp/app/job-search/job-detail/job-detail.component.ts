import {
    AfterViewInit,
    Component,
    ElementRef,
    HostListener, OnInit,
    ViewChild
} from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { JobCenter, ReferenceService } from '../../shared/reference-service';
import {
    getJobList,
    getSelectedJob,
    getTotalJobCount,
    JobSearchState
} from '../state-management';
import { Store } from '@ngrx/store';
import { TOOLTIP_AUTO_HIDE_TIMEOUT } from '../../app.constants';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import {
    JobAdvertisement, JobAdvertisementStatus,
    JobDescription,
    SourceSystem
} from '../../shared/job-advertisement/job-advertisement.model';
import { JobAdvertisementUtils } from '../../dashboard/job-advertisement.utils';
import { CoreState, getLanguage } from '../../shared/state-management/state/core.state';

@Component({
    selector: 'jr2-job-detail',
    templateUrl: './job-detail.component.html',
    styleUrls: [
        './job-detail.scss'
    ]
})
export class JobDetailComponent implements AfterViewInit, OnInit {
    job$: Observable<JobAdvertisement>;
    jobDescription$: Observable<JobDescription>;
    jobList$: Observable<JobAdvertisement[]>;
    jobCenter$: Observable<JobCenter>;
    jobListTotalSize$: Observable<number>;
    externalJobDisclaimerClosed = false;
    jobAdDeactivated = false;

    @ViewChild('copyToClipboard')
    copyToClipboardElementRef: ElementRef;

    @ViewChild(NgbTooltip)
    clipboardTooltip: NgbTooltip;

    constructor(private referenceService: ReferenceService,
                private store: Store<JobSearchState>,
                private coreStore: Store<CoreState>) {
    }

    ngOnInit(): void {
        this.job$ = this.store.select(getSelectedJob)
            .map(this.fixApplicationUrl)
            .do((job: JobAdvertisement) => {
                this.jobAdDeactivated = this.isDeactivated(job.status);
            });
        this.jobList$ = this.store.select(getJobList);

        this.jobListTotalSize$ = this.store.select(getTotalJobCount);

        this.jobCenter$ = this.job$
            .filter((job) => !!job)
            .map((job) => job.jobCenterCode)
            .filter((jobCenterCode) => !!jobCenterCode)
            .switchMap((jobCenterCode) => this.referenceService.resolveJobCenter(jobCenterCode));

        this.jobDescription$ = this.coreStore.select(getLanguage)
            .combineLatest(this.job$)
            .map(([lang, job]: [string, JobAdvertisement]) => JobAdvertisementUtils.getJobDescription(job, lang));

    }

    private isDeactivated(jobAdvertisementStatus: JobAdvertisementStatus): boolean {
        return jobAdvertisementStatus.toString() === 'CANCELLED' || jobAdvertisementStatus.toString() === 'ARCHIVED';
    }

    private fixApplicationUrl(jobAdvertisement: JobAdvertisement) {
        const applyChannel = jobAdvertisement.jobContent.applyChannel;
        if (applyChannel && applyChannel.formUrl) {
            if (!applyChannel.formUrl.startsWith('http://') && !applyChannel.formUrl.startsWith('https://')) {
                jobAdvertisement.jobContent.applyChannel = Object.assign(applyChannel, {
                    formUrl: `http://${applyChannel.formUrl}`
                });
            }
        }
        return jobAdvertisement;
    }

    isExternalJobDisclaimerShown(job: JobAdvertisement) {
        return job.sourceSystem.toString() === SourceSystem[SourceSystem.EXTERN] && !this.externalJobDisclaimerClosed;
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
