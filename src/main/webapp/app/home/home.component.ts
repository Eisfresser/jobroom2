import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import {
    CandidateSearchToolState,
    getActiveAgencyTabId,
    getActiveCompanyTabId,
    getActiveToolbarItem,
    getCandidateSearchToolState,
    getJobSearchToolState,
    HomeState,
    JobSearchToolState, getActiveSystemNotifications,
} from './state-management';
import { NgbTabChangeEvent } from '@ng-bootstrap/ng-bootstrap';
import {
    AgenciesTab,
    CompaniesTab,
    ToolbarItem
} from './state-management/state/layout.state';
import { ActivatedRoute, Router } from '@angular/router';
import { UserData } from './tools/job-publication-tool/service/user-data-resolver.service';
import { Subscription } from 'rxjs/Subscription';
import { JobAdvertisement } from '../shared/job-advertisement/job-advertisement.model';
import { SystemNotification } from './system-notification/system.notification.model';
import { BackgroundUtils } from '../shared/utils/background-utils';

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: [
        'home.scss'
    ]
})
export class HomeComponent implements OnInit, OnDestroy {
    ToolbarItem: typeof ToolbarItem = ToolbarItem;
    CompaniesTab: typeof CompaniesTab = CompaniesTab;
    AgenciesTab: typeof AgenciesTab = AgenciesTab;

    activeToolbarItem$: Observable<ToolbarItem>;
    jobSearchToolModel$: Observable<JobSearchToolState>;
    candidateSearchToolModel$: Observable<CandidateSearchToolState>;
    activeSystemNotifications$: Observable<SystemNotification[]>;
    activeCompanyTabId$: Observable<string>;
    activeAgencyTabId$: Observable<string>;
    jobAdvertisement$: Observable<JobAdvertisement>;
    userData$: Observable<UserData>;
    isSubnavCollapsed: boolean;

    private subscription: Subscription;

    constructor(private store: Store<HomeState>,
                private route: ActivatedRoute,
                private router: Router,
                private backgroundUtils: BackgroundUtils) {
        this.activeSystemNotifications$ = store.select(getActiveSystemNotifications);
        this.jobSearchToolModel$ = store.select(getJobSearchToolState);
        this.candidateSearchToolModel$ = store.select(getCandidateSearchToolState);
        this.activeToolbarItem$ = store.select(getActiveToolbarItem);
        this.activeCompanyTabId$ = store.select(getActiveCompanyTabId);
        this.activeAgencyTabId$ = store.select(getActiveAgencyTabId);

        this.isSubnavCollapsed = true;

        this.jobAdvertisement$ = this.route.data
            .map((data) => data['jobAdvertisement']);

        this.userData$ = this.route.data
            .map((data) => data['userData']);
    }

    ngOnInit(): void {
        this.subscription = this.activeToolbarItem$
            .subscribe((toolbarItem: ToolbarItem) => this.backgroundUtils.addBackGroundClass(toolbarItem));
    }

    ngOnDestroy(): void {
        this.backgroundUtils.removeAllBackgroundClasses();
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

    selectCompaniesTab(event: NgbTabChangeEvent): void {
        const url = event.nextId === CompaniesTab.JOB_PUBLICATION ? '/companies/jobpublication' : '/companies/candidates';
        this.router.navigate([url]);
    }

    selectRecruitmentAgenciesTab(event: NgbTabChangeEvent): void {
        const url = event.nextId === AgenciesTab.CANDIDATE_SEARCH ? '/agents/candidates' : '/agents/jobpublication';
        this.router.navigate([url]);
    }

    toggleSubnavbar() {
        this.isSubnavCollapsed = !this.isSubnavCollapsed;
    }

    collapseSubnavbar() {
        this.isSubnavCollapsed = true;
    }
}
