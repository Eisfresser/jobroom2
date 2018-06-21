import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { JobroomSharedModule, JobSearchSharedModule } from '../shared';
import { HomeComponent } from './';
import { JobSearchToolComponent } from './tools/job-search-tool/job-search-tool.component';
import { CandidateSearchToolComponent } from './tools/candidate-search-tool/candidate-search-tool.component';
import { JobPublicationToolComponent } from './tools/job-publication-tool/job-publication-tool.component';
import { ToolbarComponent } from './toolbar/toolbar.component';
import { ToolbarItemComponent } from './toolbar-item/toolbar-item.component';
import { StoreModule } from '@ngrx/store';
import { HomeEffects, homeReducers } from './state-management';
import { EffectsModule } from '@ngrx/effects';
import { CandidateSearchModule } from '../candidate-search/candidate-search.module';
import { MultiselectDropdownModule } from 'angular-2-dropdown-multiselect';
import { ZipCodeComponent } from './tools/job-publication-tool/zip-code/zip-code.component';
import { LanguageSkillsComponent } from './tools/job-publication-tool/language-skills/language-skills.component';
import { HomeRoutingModule } from './home-routing.module';
import { HomeRouterEffects } from './state-management/effects/router.effects';
import { UserDataResolverService } from './tools/job-publication-tool/service/user-data-resolver.service';
import { ScrollToFirstInvalidDirective } from './tools/job-publication-tool/scroll-to-first-invalid.directive';
import { JobDescriptionComponent } from './tools/job-publication-tool/job-description/job-description.component';
import { JobAdvertisementResolverService } from './tools/job-publication-tool/service/job-advertisement-resolver.service';
import { SystemNotificationComponent } from './system-notification/system.notification.component';
import { SystemNotificationService } from './system-notification/system.notification.service';
import { BackgroundUtils } from '../shared/utils/background-utils';

@NgModule({
    imports: [
        JobroomSharedModule,
        JobSearchSharedModule,
        CandidateSearchModule,
        ReactiveFormsModule,
        StoreModule.forFeature('home', homeReducers),
        EffectsModule.forFeature([HomeEffects, HomeRouterEffects]),
        HomeRoutingModule,
        MultiselectDropdownModule
    ],
    declarations: [
        HomeComponent,
        JobSearchToolComponent,
        CandidateSearchToolComponent,
        SystemNotificationComponent,
        JobPublicationToolComponent,
        ToolbarComponent,
        ToolbarItemComponent,
        LanguageSkillsComponent,
        ZipCodeComponent,
        ScrollToFirstInvalidDirective,
        JobDescriptionComponent
    ],
    entryComponents: [],
    providers: [
        JobAdvertisementResolverService,
        UserDataResolverService,
        SystemNotificationService,
        BackgroundUtils
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class JobroomHomeModule {
}
