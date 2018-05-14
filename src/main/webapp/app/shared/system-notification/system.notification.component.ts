import { Component, Input } from '@angular/core';
import { SystemNotification } from './system.notification.model';
import { JhiLanguageService } from 'ng-jhipster';

@Component({
    selector: 'jr2-system-notification',
    templateUrl: './system.notification.component.html',
    styleUrls: ['./system.notification.component.scss']
})
export class SystemNotificationComponent {
    @Input() activeSystemNotifications: Array<SystemNotification[]>;
    languageService: JhiLanguageService;

    constructor(
        jhiLanguageService: JhiLanguageService
    ) {
        this.languageService = jhiLanguageService;
    }

    // TODO: clean up & handle unauthorized
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
    }
}
