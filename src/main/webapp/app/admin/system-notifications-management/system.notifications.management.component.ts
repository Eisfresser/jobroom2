import {Component, OnInit} from "@angular/core";
import {SystemNotification} from "../../shared/system-notification/system.notification.model";

@Component({
    selector: "jhi-sys-notifications",
    templateUrl: "./system.notifications.management.component.html"
})
export class SystemNotificationsManagementComponent implements OnInit {

    systemNotifications: SystemNotification[];

    constructor() {}

    ngOnInit() {}
}
