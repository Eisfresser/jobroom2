import {
    ChangeDetectionStrategy,
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnInit,
    Output,
    SimpleChanges
} from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ITEMS_PER_PAGE, Principal } from '../../shared';
import {
    JobAdvertisementFilter,
    PEADashboardState
} from '../state-management/state/pea-dashboard.state';
import { Store } from '@ngrx/store';
import {
    LoadNextJobAdvertisementsDashboardPageAction,
    SubmitCancellationAction
} from '../state-management/actions/pea-dashboard.actions';
import {
    JobAdvertisement,
    JobAdvertisementStatus
} from '../../shared/job-advertisement/job-advertisement.model';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { JobAdvertisementService } from '../../shared/job-advertisement/job-advertisement.service';
import { JobAdvertisementUtils } from '../job-advertisement.utils';
import { JobAdvertisementCancelDialogService } from '../dialogs/job-advertisement-cancel-dialog.service';
import { CompanyService } from '../../shared/company/company.service';
import { Company } from '../../shared/company/company.model';

interface JobAdvertisementView {
    id: string;
    stellennummerEgov: string;
    stellennummerAvam: string;
    publicationDate: string;
    title: string;
    location: string;
    status: string;
}

@Component({
    selector: 'jr2-pea-dashboard',
    templateUrl: './pea-dashboard.component.html',
    styleUrls: ['./pea-dashboard.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PeaDashboardComponent implements OnInit, OnChanges {
    readonly PAGE_SIZE = ITEMS_PER_PAGE;

    @Input()
    jobAdvertisementFilter: JobAdvertisementFilter;
    @Input()
    jobAdvertisementList: JobAdvertisement[];
    @Input()
    totalCount: number;
    @Input()
    page: number;

    @Output()
    filterJobAdvertisements = new EventEmitter<JobAdvertisementFilter>();
    @Output()
    pageChange = new EventEmitter<number>();

    identity$: Observable<any>;
    organization$: Observable<Company>;
    jobFilterForm: FormGroup;
    jobAdvertisementList$: Observable<JobAdvertisementView[]>;

    constructor(private fb: FormBuilder,
                private principal: Principal,
                private companyService: CompanyService,
                private store: Store<PEADashboardState>,
                private jobAdvertisementService: JobAdvertisementService,
                private translateService: TranslateService,
                private jobAdvertisementCancelDialogService: JobAdvertisementCancelDialogService) {
    }

    ngOnInit(): void {
        this.store.dispatch(new LoadNextJobAdvertisementsDashboardPageAction({ page: this.page }));

        this.jobFilterForm = this.fb.group({
            jobTitle: [this.jobAdvertisementFilter.jobTitle],
            onlineSinceDays: [this.jobAdvertisementFilter.onlineSinceDays]
        });

        this.identity$ = this.principal.currentUser();
        this.organization$ = this.identity$
            .flatMap((currentUser) => this.companyService.findByExternalId(currentUser.companyId));
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['jobAdvertisementList']) {
            this.jobAdvertisementList$ = Observable.merge(
                Observable.of(this.translateService.currentLang),
                this.translateService.onLangChange.map((e: LangChangeEvent) => e.lang))
                .map(this.mapJobAdvertisementsToViews.bind(this));
        }
    }

    private mapJobAdvertisementsToViews(lang: string): JobAdvertisementView[] {
        return this.jobAdvertisementList
            .map((jobAdvertisement: JobAdvertisement): JobAdvertisementView => ({
                id: jobAdvertisement.id,
                stellennummerEgov: jobAdvertisement.stellennummerEgov,
                stellennummerAvam: jobAdvertisement.stellennummerAvam,
                publicationDate: jobAdvertisement.publication.startDate,
                title: JobAdvertisementUtils.getJobDescription(jobAdvertisement, lang).title,
                location: jobAdvertisement.jobContent.location.city,
                status: jobAdvertisement.status.toString()
            }));
    }

    filter(): void {
        this.filterJobAdvertisements.emit(Object.assign({}, this.jobFilterForm.value));
    }

    changePage(page: number): void {
        this.pageChange.emit(page);
    }

    isJobAdvertisementCancellable(status: JobAdvertisementStatus): boolean {
        return this.jobAdvertisementService.isJobAdvertisementCancellable(status);
    }

    showCancellationDialog(id: string) {
        const onSubmit = (cancellationData) => this.store.dispatch(new SubmitCancellationAction(cancellationData));
        this.jobAdvertisementCancelDialogService.open(id, onSubmit);
    }
}
