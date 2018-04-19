export class SystemNotification {
    notificationId: number;
    notificationTitle: string;
    notificationType: string;
    notificationStartDate: string;
    notificationEndDate: string;
    isNotificationActive: boolean;

    constructor(
        notificationId: number,
        notificationTitle: string,
        notificationType: string,
        notificationStartDate: string,
        notificationEndDate: string,
        isNotificationActive: boolean
    ) {
        this.notificationId = notificationId ? notificationId : null;
        this.notificationTitle = notificationTitle ? notificationTitle : null;
        this.notificationType = notificationType ? notificationType : null;
        this.notificationStartDate = notificationStartDate ? notificationStartDate : null;
        this.notificationEndDate = notificationEndDate ? notificationEndDate : null;
        this.isNotificationActive = isNotificationActive ? isNotificationActive : null;
    }
}
