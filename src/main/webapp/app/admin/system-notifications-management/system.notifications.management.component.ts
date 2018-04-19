import {Component, OnInit} from '@angular/core';
import {SystemNotification} from '../../shared/system-notification/system.notification.model';

@Component({
    selector: 'jhi-sys-notifications',
    templateUrl: './system.notifications.management.component.html'
})
export class SystemNotificationsManagementComponent implements OnInit {
    systemNotifications: SystemNotification[];

    // id: number,
    // title: string,
    // notificationType: string,
    // notificationStartDate: string,
    // notificationEndDate: string,
    // isNotificationActive: boolean

    constructor() {
    }

    ngOnInit() {
        this.systemNotifications = new Array(
            new SystemNotification(
                1,
                'Systemmitteilung 1',
                'SYSTEM',
                '01-01-2001',
                '01-01-2001',
                true
            ),
            new SystemNotification(
                2,
                'Systemmitteilung 2',
                'SYSTEM',
                '01-01-2001',
                '01-01-2001',
                true
            ),
            new SystemNotification(
                3,
                'Systemmitteilung 3',
                'SYSTEM',
                '01-01-2001',
                '01-01-2001',
                true
            ),
            new SystemNotification(
                4,
                'Systemmitteilung 4',
                'SYSTEM',
                '01-01-2001',
                '01-01-2001',
                true
            ),
            new SystemNotification(
                5,
                'Systemmitteilung 5',
                'SYSTEM',
                '01-01-2001',
                '01-01-2001',
                true
            )
        );
    }
}
