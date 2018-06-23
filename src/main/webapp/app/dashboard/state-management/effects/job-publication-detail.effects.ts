import { Injectable } from '@angular/core';
import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';
import { Observable } from 'rxjs/Observable';
import { createJobAdvertisementCancellationRequest } from '../util/cancellation-request.mapper';
import {
    CancellationFailedAction,
    CancellationSucceededAction,
    JobAdvertisementLoadedAction,
    LOAD_JOB_ADVERTISEMENT,
    LoadJobAdvertisementAction,
    LoadJobAdvertisementFailedAction,
    SUBMIT_CANCELLATION,
    SubmitCancellationAction
} from '../actions/job-publication-detail.actions';
import { JobAdvertisementService } from '../../../shared/job-advertisement/job-advertisement.service';
import { JobAdvertisement } from '../../../shared/job-advertisement/job-advertisement.model';
import { JobAdvertisementCancelRequest } from '../../../shared/job-advertisement/job-advertisement-cancel-request';

@Injectable()
export class JobPublicationDetailEffects {

    @Effect()
    loadJobAdvertisement$: Observable<Action> = this.actions$
        .ofType(LOAD_JOB_ADVERTISEMENT)
        .switchMap((action: LoadJobAdvertisementAction) => {
            const job$ = action.payload.token ? this.jobAdvertisementService.findByToken(action.payload.token)
                : this.jobAdvertisementService.findById(action.payload.id);

            return job$
                .map((jobAdvertisement: JobAdvertisement) => new JobAdvertisementLoadedAction(jobAdvertisement))
                .catch((error) => Observable.of(new LoadJobAdvertisementFailedAction(error)))
        });

    @Effect()
    cancelJobAdvertisement$: Observable<Action> = this.actions$
        .ofType(SUBMIT_CANCELLATION)
        .map((action: SubmitCancellationAction) => createJobAdvertisementCancellationRequest(action.payload))
        .switchMap((jobCancelRequest: JobAdvertisementCancelRequest) => {
            return this.jobAdvertisementService.cancel(jobCancelRequest)
                .flatMap((code) => jobCancelRequest.token
                    ? this.jobAdvertisementService.findByToken(jobCancelRequest.token)
                    : this.jobAdvertisementService.findById(jobCancelRequest.id))
                .map((jobAdvertisement: JobAdvertisement) => new CancellationSucceededAction(jobAdvertisement))
                .catch((error) => Observable.of(new CancellationFailedAction(error)))
        });

    constructor(private actions$: Actions,
                private jobAdvertisementService: JobAdvertisementService) {
    }
}
