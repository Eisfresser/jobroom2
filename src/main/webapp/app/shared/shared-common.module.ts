import { NgModule, LOCALE_ID } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { registerLocaleData } from '@angular/common';
import locale from '@angular/common/locales/de';
import localeFr from '@angular/common/locales/fr';
import localeIt from '@angular/common/locales/it';
import localeEn from '@angular/common/locales/en';

import {
    JobroomSharedLibsModule,
    JhiLanguageHelper,
    FindLanguageFromKeyPipe,
    JhiAlertComponent,
    JhiAlertErrorComponent
} from './';
import {
    LanguageFilterComponent,
    MultiselectComponent,
    PhoneNumberInputComponent,
    RangeInputComponent,
    SearchButtonComponent,
    TypeaheadMultiselectComponent
} from './input-components';
import { WorkingTimeRangePipe } from './pipes/working-time-range.pipe';
import { KeysPipe } from './pipes/enum-keys.pipe';

@NgModule({
    imports: [
        JobroomSharedLibsModule
    ],
    declarations: [
        FindLanguageFromKeyPipe,
        KeysPipe,
        JhiAlertComponent,
        JhiAlertErrorComponent,
        TypeaheadMultiselectComponent,
        RangeInputComponent,
        MultiselectComponent,
        PhoneNumberInputComponent,
        LanguageFilterComponent,
        SearchButtonComponent,
        WorkingTimeRangePipe
    ],
    providers: [
        JhiLanguageHelper,
        Title,
        {
            provide: LOCALE_ID,
            useValue: 'de'
        },
        KeysPipe,
        WorkingTimeRangePipe
    ],
    exports: [
        JobroomSharedLibsModule,
        FindLanguageFromKeyPipe,
        JhiAlertComponent,
        JhiAlertErrorComponent,
        TypeaheadMultiselectComponent,
        RangeInputComponent,
        MultiselectComponent,
        PhoneNumberInputComponent,
        LanguageFilterComponent,
        SearchButtonComponent,
        KeysPipe,
        WorkingTimeRangePipe
    ]
})
export class JobroomSharedCommonModule {
    constructor() {
        registerLocaleData(locale);
        registerLocaleData(localeFr);
        registerLocaleData(localeIt);
        registerLocaleData(localeEn);
    }
}
