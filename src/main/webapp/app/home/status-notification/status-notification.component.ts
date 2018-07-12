import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import {
    getStatusNotificationMessageKey,
    getStatusNotificationState,
    HomeState
} from '../state-management';
import { Store } from '@ngrx/store';
import {
    HideStatusNotificationMessageAction,
    ShowStatusNotificationMessageAction
} from '../state-management/actions/status-notification.actions';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jr2-status-notification',
    templateUrl: './status-notification.component.html'
})
export class StatusNotificationComponent implements OnInit, OnDestroy {

    private readonly MESSAGE_QUERY_PARAMETER = 'show-message';

    showMessage$: Observable<boolean>;
    title: string;
    message: string;

    private unsubscribe$ = new Subject<void>();

    constructor(private activeRouter: ActivatedRoute,
                private store: Store<HomeState>,
                private translateService: TranslateService) {
    }

    ngOnInit(): void {
        this.activeRouter.queryParams
            .takeUntil(this.unsubscribe$)
            .map((params: Params) => params[this.MESSAGE_QUERY_PARAMETER])
            .withLatestFrom(this.store.select(getStatusNotificationMessageKey))
            .filter(([messageKey, prevMessageKey]) => messageKey && messageKey !== prevMessageKey)
            .subscribe(([messageKey]) => this.store.dispatch(new ShowStatusNotificationMessageAction(messageKey)));

        this.showMessage$ = this.store.select(getStatusNotificationState)
            .do((state) => {
                this.title = `home.${state.messageKey}.title`;
                this.message = `home.${state.messageKey}.message`;
            })
            .flatMap((state) => this.translateService.get([this.title, this.message])
                .map((translations) => state.showMessage
                    && this.hasTranslation(translations, this.title)
                    && this.hasTranslation(translations, this.message))
            );
    }

    private hasTranslation(translations: any, key: string): boolean {
        return translations[key] && translations[key].lastIndexOf('translation-not-found', 0) < 0;
    }

    ngOnDestroy(): void {
        this.unsubscribe$.next();
        this.unsubscribe$.complete();
    }

    hideMessage(): void {
        this.store.dispatch(new HideStatusNotificationMessageAction());
    }
}
