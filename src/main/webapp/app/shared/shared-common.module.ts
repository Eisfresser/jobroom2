import { NgModule, LOCALE_ID } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { registerLocaleData } from '@angular/common';
import locale from '@angular/common/locales/de';

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
    TypeaheadMultiselectComponent,
    TypeaheadSingleselectComponent
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
        TypeaheadSingleselectComponent,
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
        TypeaheadSingleselectComponent,
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
    }
}
