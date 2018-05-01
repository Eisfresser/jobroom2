import { Component, OnInit } from '@angular/core';
import { SystemNotificationService } from './system.notification.service';
import { SystemNotification } from './system.notification.model';
import moment = require('moment');

@Component({
    selector: 'jr2-system-notification',
    templateUrl: './system.notification.component.html',
    styleUrls: ['./system.notification.component.scss']
})
export class SystemNotificationComponent implements OnInit {
    systemNotificationService: SystemNotificationService;
    systemNotifications: SystemNotification[];
    activeSystemNotification: SystemNotification;

    constructor(systemNotificationService: SystemNotificationService) {
        this.systemNotificationService = systemNotificationService;
    }

    ngOnInit(): void {
        this.systemNotificationService
            .getAllSystemNotifications()
            .subscribe(
                (data: SystemNotification[]) =>
                    (this.systemNotifications = data)
            );
    }

    isSystemNotificationVisible(): boolean {
        this.activeSystemNotification = this.systemNotifications.find(
            (systemNotification: SystemNotification) =>
                systemNotification.active
        );
        return (
            moment(moment.now()).isAfter(
                this.activeSystemNotification.startDate
            ) &&
            moment(moment.now()).isBefore(this.activeSystemNotification.endDate)
        );
    }
}
