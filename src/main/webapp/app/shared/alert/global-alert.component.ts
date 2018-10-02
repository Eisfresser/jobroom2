import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { Alert, CoreState, getAlerts } from '../state-management/state/core.state';
import { Observable } from 'rxjs';
import { HideAlertAction } from '../state-management/actions/core.actions';

@Component({
    selector: 'jr2-global-alert',
    templateUrl: './global-alert.component.html',
    styleUrls: ['global-alert.component.scss']
})
export class GlobalAlertComponent {

    alerts$: Observable<Alert[]>;

    constructor(private coreStore: Store<CoreState>) {
        this.alerts$ = this.coreStore.select(getAlerts)
    }

    dismissAlert(alert: Alert) {
        this.coreStore.dispatch(new HideAlertAction(alert));
    }
}
