import { Component, OnInit } from '@angular/core';
import { SystemNotificationService } from './system.notification.service';
import { SystemNotification } from './system.notification.model';
import { GetAllSystemNotificationsAction } from '../../admin/system-notifications-management/state-management/actions/system-notification-management.actions';
import { getAllSystemNotifications, SystemNotificationState } from '../../admin/system-notifications-management/state-management/state/system-notification-management.state';
import { Observable } from 'rxjs/Observable';
import { Store } from '@ngrx/store';
import * as moment from 'moment';
import { JhiLanguageService } from 'ng-jhipster';
import { Jobroom2LanguageService } from '../language/jobroom2-language.service';

@Component({
    selector: 'jr2-system-notification',
    templateUrl: './system.notification.component.html',
    styleUrls: ['./system.notification.component.scss']
})
export class SystemNotificationComponent implements OnInit {
    systemNotificationService: SystemNotificationService;
    systemNotifications$: Observable<SystemNotification[]>;
    languageService: JhiLanguageService;

    constructor(systemNotificationService: SystemNotificationService, private store: Store<SystemNotificationState>, jhiLanguageService: JhiLanguageService) {
        this.systemNotificationService = systemNotificationService;
        this.languageService = jhiLanguageService;
    }

    ngOnInit(): void {

        this.systemNotifications$ = this.store.select(getAllSystemNotifications);
        this.store.dispatch(new GetAllSystemNotificationsAction);
    }

    isInTimeRange(systemNotification: SystemNotification): boolean {
        return (
            moment(moment.now()).isAfter(systemNotification.startDate) &&
            moment(moment.now()).isBefore(systemNotification.endDate)
        );
    }
    isActive(systemNotification: SystemNotification): boolean {
        return systemNotification.active;
    }
    // TODO: clean up & handle unauthorized
    getCurrentLanguageCode(systemNotification: SystemNotification) {
        if (this.languageService.currentLang === 'de') {
            return systemNotification.text_de
        }
        if (this.languageService.currentLang === 'fr') {
            return systemNotification.text_fr
        }
        if (this.languageService.currentLang === 'it') {
            return systemNotification.text_it
        }
        if (this.languageService.currentLang === 'en') {
            return systemNotification.text_en
        }
    }
}
