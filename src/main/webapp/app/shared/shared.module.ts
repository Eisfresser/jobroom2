import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { DatePipe } from '@angular/common';

import {
    AccountService,
    AuthServerProvider,
    CSRFService,
    HasAnyAuthorityDirective,
    JhiLoginModalComponent,
    JobroomSharedCommonModule,
    JobroomSharedLibsModule,
    LoginModalService,
    LoginService,
    Principal,
    StateStorageService,
    UserService
} from './';
import { LocaleAwareDatePipe } from './pipes/locale-aware-date.pipe';
import { LocaleAwareDecimalPipe } from './pipes/locale-aware-number.pipe';
import { PhoneNumberPipe } from './pipes/phone-number.pipe';
import { SafeHtmlPipe } from './pipes/safe-html.pipe';
import { LanguageComponent } from './components/language/language.component';
import { DetailsPagePaginationControlsComponent } from './components/details-page-pagination-controls/details-page-pagination-controls.component';
import { DetailsPagePaginationComponent } from './components/details-page-pagination/details-page-pagination.component';
import { dateParserFormatterProvider } from './input-components/datepicker/ngb-date-moment-parser-formatter';
import { datepickerI18nService } from './input-components/datepicker/ngb-datepicker-i18n-service';
import { OrganizationService } from './organization/organization.service';
import { TextSizeLimitDirective } from './validation/text-size-limit.directive';
import { LanguageFilterService } from './input-components/language-filter/language-filter.service';
import { ScrollToTopComponent } from './components/scroll-to-top/scroll-to-top.component';
import { JobAdvertisementService } from './job-advertisement/job-advertisement.service';
import { Jobroom2LanguageService } from './language/jobroom2-language.service';
import { ShortenPipe } from './pipes/shorten.pipe';
import { CompanyService } from './company/company.service';
import { MarkdownEscapePipe } from './pipes/markdown-escape.pipe';
import { CompanySelectionComponent } from './components/company-selection/company-selection.component';
import { CurrentSelectedCompanyService } from './company/current-selected-company.service';
import { UserInfoService } from './user-info/user-info.service';
import { FormValidationDirective } from './validation/form-validation.directive';
import { CapitalizePipe } from './pipes/capitalize.pipe';

@NgModule({
    imports: [
        JobroomSharedLibsModule,
        JobroomSharedCommonModule
    ],
    declarations: [
        JhiLoginModalComponent,
        HasAnyAuthorityDirective,
        LocaleAwareDatePipe,
        LocaleAwareDecimalPipe,
        PhoneNumberPipe,
        SafeHtmlPipe,
        MarkdownEscapePipe,
        LanguageComponent,
        DetailsPagePaginationControlsComponent,
        DetailsPagePaginationComponent,
        TextSizeLimitDirective,
        FormValidationDirective,
        ScrollToTopComponent,
        ShortenPipe,
        CompanySelectionComponent,
        CapitalizePipe
    ],
    providers: [
        LoginService,
        LoginModalService,
        AccountService,
        StateStorageService,
        Principal,
        CSRFService,
        AuthServerProvider,
        UserService,
        DatePipe,
        LocaleAwareDatePipe,
        LocaleAwareDecimalPipe,
        PhoneNumberPipe,
        SafeHtmlPipe,
        MarkdownEscapePipe,
        Jobroom2LanguageService,
        dateParserFormatterProvider(),
        datepickerI18nService(),
        OrganizationService,
        CompanyService,
        JobAdvertisementService,
        LanguageFilterService,
        CurrentSelectedCompanyService,
        UserInfoService
    ],
    entryComponents: [JhiLoginModalComponent],
    exports: [
        JobroomSharedCommonModule,
        JhiLoginModalComponent,
        HasAnyAuthorityDirective,
        DatePipe,
        LocaleAwareDatePipe,
        LocaleAwareDecimalPipe,
        PhoneNumberPipe,
        SafeHtmlPipe,
        MarkdownEscapePipe,
        LanguageComponent,
        DetailsPagePaginationControlsComponent,
        DetailsPagePaginationComponent,
        TextSizeLimitDirective,
        FormValidationDirective,
        ScrollToTopComponent,
        ShortenPipe,
        CompanySelectionComponent,
        CapitalizePipe
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class JobroomSharedModule {
}
