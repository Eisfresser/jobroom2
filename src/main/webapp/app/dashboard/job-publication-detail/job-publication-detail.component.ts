import { Component } from '@angular/core';
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
import { JobAdvertisement, JobDescription } from '../../shared/job-advertisement/job-advertisement.model';
import { JobAdvertisementService } from '../../shared/job-advertisement/job-advertisement.service';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import { JobAdvertisementUtils } from '../job-advertisement.utils';
import { LanguageSkill } from '../../shared/job-publication/job-publication.model';
import { JobAdvertisementCancelDialogService } from '../dialogs/job-advertisement-cancel-dialog.service';

@Component({
    selector: 'jr2-job-publication-detail',
    templateUrl: './job-publication-detail.component.html',
    styleUrls: []
})
export class JobPublicationDetailComponent {

    jobAdvertisement$: Observable<JobAdvertisement>;
    showCancellationSuccess$: Observable<boolean>;
    showCancellationError$: Observable<boolean>;
    showCancellationLink$: Observable<boolean>;
    jobDescription$: Observable<JobDescription>;
    languageSkills$: Observable<LanguageSkill[]>;

    constructor(private jobAdvertisementService: JobAdvertisementService,
                private store: Store<JobPublicationDetailState>,
                private translateService: TranslateService,
                private jobAdvertisementCancelDialogService: JobAdvertisementCancelDialogService) {
        this.showCancellationSuccess$ = store.select(getShowCancellationSuccess);
        this.showCancellationError$ = store.select(getShowCancellationError);
        this.jobAdvertisement$ = store.select(getJobAdvertisement)
            .map(this.fixApplicationUrl);
        this.showCancellationLink$ = store.select(getJobAdvertisement)
            .filter((jobAdvertisement: JobAdvertisement) => !!jobAdvertisement)
            .map((jobAdvertisement: JobAdvertisement) =>
                this.jobAdvertisementService.isJobAdvertisementCancellable(jobAdvertisement.status));

        this.jobDescription$ = Observable.merge(
            Observable.of(this.translateService.currentLang),
            this.translateService.onLangChange.map((e: LangChangeEvent) => e.lang))
            .combineLatest(this.jobAdvertisement$)
            .map(([lang, jobAdvertisement]: [string, JobAdvertisement]) => JobAdvertisementUtils.getJobDescription(jobAdvertisement, lang));

        this.languageSkills$ = this.jobAdvertisement$
            .map(this.mapLanguages);
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

    private mapLanguages(jobAdvertisement: JobAdvertisement): LanguageSkill[] {
        return jobAdvertisement.jobContent.languageSkills
            .map((languageSkill) => ({
                code: languageSkill.languageIsoCode,
                spokenLevel: languageSkill.spokenLevel,
                writtenLevel: languageSkill.writtenLevel
            }));
    }

    showCancellationDialog(id: string) {
        const onSubmit = (cancellationData) => this.store.dispatch(new SubmitCancellationAction(cancellationData));
        this.jobAdvertisementCancelDialogService.open(id, onSubmit);
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
}
