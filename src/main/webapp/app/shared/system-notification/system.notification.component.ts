import { Component, OnInit } from '@angular/core';
import { SystemNotificationService } from './system.notification.service';
import { SystemNotification } from './system.notification.model';

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
        this.systemNotifications = this.systemNotificationService.getAllSystemNotifications();
    }

    getActiveSystemNotification(): SystemNotification[] {
        return this.systemNotifications.filter(
            (systemNotification: SystemNotification) =>
                systemNotification.isActive
        );
    }

}
