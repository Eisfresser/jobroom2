import { Component, Input, OnInit } from '@angular/core';
import { SystemNotification } from './system.notification.model';
import { JhiLanguageService } from 'ng-jhipster';

import { Store } from '@ngrx/store';
import { SystemNotificationState } from '../state-management/state/system-notification-state';
import { GetActiveSystemNotificationsAction } from '../state-management';

@Component({
    selector: 'jr2-system-notification',
    templateUrl: './system.notification.component.html',
    styleUrls: ['./system.notification.component.scss']
})
export class SystemNotificationComponent implements OnInit {
    @Input() activeSystemNotifications: SystemNotification[];
    languageService: JhiLanguageService;

    constructor(
        jhiLanguageService: JhiLanguageService,
        private store: Store<SystemNotificationState>
    ) {
        this.languageService = jhiLanguageService;
    }

    ngOnInit() {
        this.store.dispatch(new GetActiveSystemNotificationsAction());
    }

    getCurrentLanguageCode(activeSystemNotification: SystemNotification) {
        if (this.languageService.currentLang === 'de') {
            return activeSystemNotification.text_de;
        }
        if (this.languageService.currentLang === 'fr') {
            return activeSystemNotification.text_fr;
        }
        if (this.languageService.currentLang === 'it') {
            return activeSystemNotification.text_it;
        }
        if (this.languageService.currentLang === 'en') {
            return activeSystemNotification.text_en;
        }
        return activeSystemNotification.text_de;
    }
}
