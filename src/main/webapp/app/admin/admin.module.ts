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
    SystemNotificationsManagementModalDetailComponent,
    SystemNotificationsManagementModalDeleteComponent,
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

/* jhipster-needle-add-admin-module-import - JHipster will add admin modules imports here */

@NgModule({
    imports: [
        JobroomSharedModule,
        RouterModule.forRoot(adminState, { useHash: true }),
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
