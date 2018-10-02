import {
    HttpErrorResponse,
    HttpEvent,
    HttpHandler,
    HttpInterceptor,
    HttpRequest
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { Alert, CoreState } from '../../shared/state-management/state/core.state';
import { ShowAlertAction } from '../../shared/state-management/actions/core.actions';
import { Principal } from '../../shared';
import { TranslateService } from '@ngx-translate/core';

export class TooManyRequestsInterceptor implements HttpInterceptor {

    private readonly AUTHENTICATED_MESSAGE_KEY = 'alert.error.tooManyRequests.authenticated';
    private readonly ANONYMOUS_MESSAGE_KEY = 'alert.error.tooManyRequests.anonymous';

    constructor(private coreStore: Store<CoreState>,
                private principal: Principal,
                private translateService: TranslateService) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(req).do(() => {
        }, (err: any) => {
            if (err instanceof HttpErrorResponse && err.status === 429) {
                const messageKey = this.principal.isAuthenticated() ? this.AUTHENTICATED_MESSAGE_KEY : this.ANONYMOUS_MESSAGE_KEY;
                this.translateService.get(messageKey).take(1).subscribe((message) => {
                    const alert = new Alert('warning', message);
                    this.coreStore.dispatch(new ShowAlertAction(alert));
                });
            }
        });
    }
}
