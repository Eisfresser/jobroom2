import { HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';

import { Injector, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { EffectsModule } from '@ngrx/effects';
import { RouterStateSerializer, StoreRouterConnectingModule } from '@ngrx/router-store';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { JhiEventManager } from 'ng-jhipster';
import { JhiBase64Service } from 'ng-jhipster/src/service/base64.service';
import { CookieService } from 'ngx-cookie';
import {
    LocalStorageService,
    Ng2Webstorage,
    SessionStorageService
} from 'ngx-webstorage';
import { JobroomAccountModule } from './account/account.module';
import { JobroomAdminModule } from './admin/admin.module';
import { DEBUG_INFO_ENABLED, VERSION } from './app.constants';

import { AppRoutingModule } from './app.routing.module';

import { PaginationConfig } from './blocks/config/uib-pagination.config';
import { AuthExpiredInterceptor } from './blocks/interceptor/auth-expired.interceptor';
import { AuthInterceptor } from './blocks/interceptor/auth.interceptor';
import { CacheKeyInterceptor } from './blocks/interceptor/cache.interceptor';
import { ErrorHandlerInterceptor } from './blocks/interceptor/errorhandler.interceptor';
import { NotificationInterceptor } from './blocks/interceptor/notification.interceptor';
import { CandidateSearchModule } from './candidate-search/candidate-search.module';
import { DashboardModule } from './dashboard/dashboard.module';
import { JobroomEntityModule } from './entities/entity.module';
import { JobroomHomeModule } from './home';
import { JobSearchModule } from './job-search/job-search.module';
import {
    ActiveMenuDirective,
    ErrorComponent,
    FooterComponent,
    JhiMainComponent,
    LayoutRoutingModule,
    MegaMenuDirective,
    NavbarComponent,
    PageRibbonComponent,
    ProfileService,
    VersionComponent,
    VersionService
} from './layouts';

import { JobroomSharedModule, UserRouteAccessService } from './shared';
import { CustomRouterStateSerializer, } from './shared/custom-router-state-serializer/custom-router-state-serializer';
import { reducers } from './shared/state-management/reducers/core.reducers';
import './vendor.ts';

// jhipster-needle-angular-add-module-import JHipster will add new module here

export function translatePartialLoader(http: HttpClient) {
    // todo: remove it after migrating to the latest ng-jhipster version
    return new TranslateHttpLoader(http, 'i18n/', `.json?version=${VERSION}`);
}

const imports = [
    BrowserModule,
    AppRoutingModule,
    LayoutRoutingModule,
    Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-' }),
    JobroomSharedModule,
    JobroomHomeModule,
    JobroomAdminModule,
    JobroomAccountModule,
    JobroomEntityModule,
    JobSearchModule,
    CandidateSearchModule,
    DashboardModule,
    StoreModule.forRoot(reducers),
    StoreRouterConnectingModule,
    EffectsModule.forRoot([])
];

if (DEBUG_INFO_ENABLED) {
    imports.push(StoreDevtoolsModule.instrument({ maxAge: 25 }));
}

@NgModule({
    imports: [
        ...imports
        // jhipster-needle-angular-add-module JHipster will add new module here
    ],
    declarations: [
        JhiMainComponent,
        NavbarComponent,
        ErrorComponent,
        PageRibbonComponent,
        VersionComponent,
        ActiveMenuDirective,
        FooterComponent,
        MegaMenuDirective
    ],
    providers: [
        ProfileService,
        VersionService,
        PaginationConfig,
        UserRouteAccessService,
        { provide: RouterStateSerializer, useClass: CustomRouterStateSerializer },
        { provide: TranslateLoader, useFactory: translatePartialLoader, deps: [ HttpClient ] },
        { provide: RouterStateSerializer, useClass: CustomRouterStateSerializer },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true,
            deps: [
                LocalStorageService,
                SessionStorageService
            ]
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthExpiredInterceptor,
            multi: true,
            deps: [
                Injector
            ]
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ErrorHandlerInterceptor,
            multi: true,
            deps: [
                JhiEventManager
            ]
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: NotificationInterceptor,
            multi: true,
            deps: [
                Injector
            ]
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: CacheKeyInterceptor,
            multi: true,
            deps: [
                CookieService,
                JhiBase64Service
            ]
        }
    ],
    bootstrap: [JhiMainComponent]
})
export class JobroomAppModule {
}
