import { LOCALE_ID, NgModule } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { KeysPipe } from './pipes/enum-keys.pipe';

import {
    FindLanguageFromKeyPipe,
    JhiAlertComponent,
    JhiAlertErrorComponent,
    JhiLanguageHelper,
    JobroomSharedLibsModule
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
}
