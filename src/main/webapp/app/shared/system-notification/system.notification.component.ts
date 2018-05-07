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

    constructor(systemNotificationService: SystemNotificationService) {
        this.systemNotificationService = systemNotificationService;
    }

    ngOnInit(): void {

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
}
