import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { JobroomSharedModule } from '../shared';
import { JobroomElasticsearchReindexModule } from './elasticsearch-reindex/elasticsearch-reindex.module';
import {
    adminState,
    AuditsComponent,
    AuditsService,
    GatewayRoutesService,
    JhiConfigurationComponent,
    JhiConfigurationService,
    JhiDocsComponent,
    JhiGatewayComponent,
    JhiHealthCheckComponent,
    JhiHealthModalComponent,
    JhiHealthService,
    JhiMetricsMonitoringComponent,
    JhiMetricsMonitoringModalComponent,
    JhiMetricsService,
    LogsComponent,
    LogsService,
    SystemNotificationsManagementComponent,
    SystemNotificationsManagementModalCreateComponent,
    SystemNotificationsManagementModalDeleteComponent,
    SystemNotificationsManagementModalDetailComponent,
    UserDeleteDialogComponent,
    UserDialogComponent,
    UserMgmtComponent,
    UserMgmtDeleteDialogComponent,
    UserMgmtDetailComponent,
    UserMgmtDialogComponent,
    UserModalService,
    UserResolve,
    UserResolvePagingParams
} from './';
import { SystemNotificationService } from '../home/system-notification/system.notification.service';
import { StoreModule } from '@ngrx/store';
import { systemNotificationReducer } from './system-notifications-management/state-management/reducers/system-notification-management-reducers';
import { EffectsModule } from '@ngrx/effects';
import { SystemNotificationManagementEffects } from './system-notifications-management/state-management/effects/system-notification-management.effects';
import { ReactiveFormsModule } from '@angular/forms';

/* jhipster-needle-add-admin-module-import - JHipster will add admin modules imports here */

@NgModule({
    imports: [
        JobroomSharedModule,
        RouterModule.forRoot(adminState, { useHash: true }),
        StoreModule.forFeature('SystemNotifications', systemNotificationReducer),
        EffectsModule.forFeature([SystemNotificationManagementEffects]),
        ReactiveFormsModule,
        JobroomElasticsearchReindexModule
        /* jhipster-needle-add-admin-module - JHipster will add admin modules here */
    ],
    declarations: [
        AuditsComponent,
        UserMgmtComponent,
        UserDialogComponent,
        UserDeleteDialogComponent,
        UserMgmtDetailComponent,
        UserMgmtDialogComponent,
        UserMgmtDeleteDialogComponent,
        LogsComponent,
        JhiConfigurationComponent,
        JhiHealthCheckComponent,
        JhiHealthModalComponent,
        JhiDocsComponent,
        JhiGatewayComponent,
        JhiMetricsMonitoringComponent,
        JhiMetricsMonitoringModalComponent,
        SystemNotificationsManagementComponent,
        SystemNotificationsManagementModalCreateComponent,
        SystemNotificationsManagementModalDeleteComponent,
        SystemNotificationsManagementModalDetailComponent
    ],
    entryComponents: [
        UserMgmtDialogComponent,
        UserMgmtDeleteDialogComponent,
        SystemNotificationsManagementModalCreateComponent,
        SystemNotificationsManagementModalDeleteComponent,
        SystemNotificationsManagementModalDetailComponent,
        JhiHealthModalComponent,
        JhiMetricsMonitoringModalComponent
    ],
    providers: [
        AuditsService,
        SystemNotificationService,
        JhiConfigurationService,
        JhiHealthService,
        JhiMetricsService,
        GatewayRoutesService,
        LogsService,
        UserResolvePagingParams,
        UserResolve,
        UserModalService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class JobroomAdminModule {
}
