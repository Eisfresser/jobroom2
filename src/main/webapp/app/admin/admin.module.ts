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
import { ApiUserManagementComponent } from './api-user-management/api-user-management.component';
import { ApiUserService } from './api-user-management/service/api-user.service';
import { ApiUserManagementEffects } from './api-user-management/state-management/effect/api-user-management.effects';
import { apiUserManagementReducer } from './api-user-management/state-management/reducer/api-user-management.reducer';
import { ApiUserDialogService } from './api-user-management/service/api-user-dialog.service';
import { ApiUserManagementDialogComponent } from './api-user-management/dialogs/api-user-management-dialog.component';
import { ApiUserManagementListComponent } from './api-user-management/api-user-management-list/api-user-management-list.component';

/* jhipster-needle-add-admin-module-import - JHipster will add admin modules imports here */

@NgModule({
    imports: [
        JobroomSharedModule,
        RouterModule.forRoot(adminState, { useHash: true }),
        StoreModule.forFeature('SystemNotifications', systemNotificationReducer),
        EffectsModule.forFeature([SystemNotificationManagementEffects]),
        ReactiveFormsModule,
        JobroomElasticsearchReindexModule,
        StoreModule.forFeature('apiUserManagement', apiUserManagementReducer),
        EffectsModule.forFeature([ApiUserManagementEffects])
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
        SystemNotificationsManagementModalDetailComponent,
        JhiMetricsMonitoringModalComponent,
        ApiUserManagementComponent,
        ApiUserManagementDialogComponent,
        ApiUserManagementListComponent
    ],
    entryComponents: [
        UserMgmtDialogComponent,
        UserMgmtDeleteDialogComponent,
        SystemNotificationsManagementModalCreateComponent,
        SystemNotificationsManagementModalDeleteComponent,
        SystemNotificationsManagementModalDetailComponent,
        JhiHealthModalComponent,
        JhiMetricsMonitoringModalComponent,
        ApiUserManagementDialogComponent
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
        ApiUserService,
        ApiUserDialogService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class JobroomAdminModule {
}
