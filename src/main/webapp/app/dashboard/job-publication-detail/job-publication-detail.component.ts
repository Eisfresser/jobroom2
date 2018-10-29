import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Store } from '@ngrx/store';
import {
    getJobAdvertisement,
    getShowCancellationError,
    getShowCancellationSuccess,
    JobPublicationDetailState
} from '../state-management/state/job-publication-detail.state';
import {
    HideErrorMessageAction,
    HideSuccessMessageAction,
    SubmitCancellationAction
} from '../state-management/actions/job-publication-detail.actions';
import {
    Address,
    ApplyChannel,
    JobAdvertisement,
    JobDescription
} from '../../shared/job-advertisement/job-advertisement.model';
import { JobAdvertisementService } from '../../shared/job-advertisement/job-advertisement.service';
import { JobAdvertisementUtils } from '../job-advertisement.utils';
import { JobAdvertisementCancelDialogService } from '../dialogs/job-advertisement-cancel-dialog.service';
import { CoreState, getLanguage } from '../../shared/state-management/state/core.state';
import { ActivatedRoute, Router } from '@angular/router';
import {AddressMapper} from "../../shared/model/address-mapper";

@Component({
    selector: 'jr2-job-publication-detail',
    templateUrl: './job-publication-detail.component.html',
    styleUrls: []
})
export class JobPublicationDetailComponent implements OnInit {
    jobAdvertisement$: Observable<JobAdvertisement>;
    showCancellationSuccess$: Observable<boolean>;
    showCancellationError$: Observable<boolean>;
    showCancellationLink$: Observable<boolean>;
    jobDescription$: Observable<JobDescription>;
    token: string;

    constructor(private jobAdvertisementService: JobAdvertisementService,
                private store: Store<JobPublicationDetailState>,
                private coreStore: Store<CoreState>,
                private jobAdvertisementCancelDialogService: JobAdvertisementCancelDialogService,
                private route: ActivatedRoute,
                private router: Router) {
    }

    ngOnInit() {
        this.route.queryParamMap.subscribe((params) => this.token = params.get('token'));

        this.showCancellationSuccess$ = this.store.select(getShowCancellationSuccess);
        this.showCancellationError$ = this.store.select(getShowCancellationError);
        this.jobAdvertisement$ = this.store.select(getJobAdvertisement)
            .map(this.fixApplicationUrl);
        this.showCancellationLink$ = this.store.select(getJobAdvertisement)
            .filter((jobAdvertisement: JobAdvertisement) => !!jobAdvertisement)
            .map((jobAdvertisement: JobAdvertisement) =>
                this.jobAdvertisementService.isJobAdvertisementCancellable(jobAdvertisement.status));

        this.jobDescription$ = this.coreStore.select(getLanguage)
            .combineLatest(this.jobAdvertisement$)
            .map(([lang, jobAdvertisement]: [string, JobAdvertisement]) => JobAdvertisementUtils.getJobDescription(jobAdvertisement, lang));
    }

    private fixApplicationUrl(jobAdvertisement: JobAdvertisement) {
        const applyChannel = jobAdvertisement.jobContent.applyChannel;
        if (applyChannel && applyChannel.formUrl && !applyChannel.formUrl.startsWith('http')) {
            jobAdvertisement.jobContent.applyChannel = Object.assign(applyChannel, {
                formUrl: `http://${applyChannel.formUrl}`
            });
        }
        return jobAdvertisement;
    }

    showCancellationDialog(id: string, token: string) {
        const onSubmit = (cancellationData) => this.store.dispatch(new SubmitCancellationAction(cancellationData));
        this.jobAdvertisementCancelDialogService.open(id, onSubmit, token);
    }

    copyJobAdvertisement(id: string, token: string) {
        const params = { jobPublicationId: id };
        if (token) {
            Object.assign(params, { token });
        }
        this.router.navigate(['/agents/jobpublication', params]);
    }

    closeSuccessMessage() {
        this.store.dispatch(new HideSuccessMessageAction());
    }

    closeErrorMessage() {
        this.store.dispatch(new HideErrorMessageAction());
    }

    printJobAdvertisement() {
        window.print();
    }

    getPostAddress(applyChannel: ApplyChannel): string {
        return applyChannel.postAddress
            ? AddressMapper.mapAddressToString(applyChannel.postAddress)
            : applyChannel.rawPostAddress;
    }
}
