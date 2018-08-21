import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { JobroomSharedModule, ModalUtils } from '../shared';
import { HttpClientModule } from '@angular/common/http';
import { BrowserModule } from '@angular/platform-browser';
import { BackgroundUtils } from '../shared/utils/background-utils';
import { RegistrationQuestionnaireComponent } from './registration/questionnaire/registration-questionnaire.component';
import { JobroomSharedCommonModule } from '../shared/shared-common.module';
import {
    accountState,
    JobseekerDialogComponent,
    RegistrationCompanyDialogComponent,
    RegistrationPavDialogComponent,
    RegistrationService,
    ReLoginComponent,
} from './';
import { RegistrationDialogService } from './registration/registration-dialog.service';
import { RegistrationAccessCodeComponent } from './registration/access-code/registration-access-code.component';
import { RegistrationGuardService } from './registration/registration-guard.service';
import { ContactTemplateManagementComponent } from '../account/contact-template-management/contact-template-management.component';

@NgModule({
    imports: [
        JobroomSharedModule,
        ReactiveFormsModule,
        CommonModule,
        RouterModule.forRoot(accountState, { useHash: true }),
        BrowserModule,
        HttpClientModule,
        JobroomSharedCommonModule
    ],
    declarations: [
        ReLoginComponent,
        JobseekerDialogComponent,
        ContactTemplateManagementComponent,
        RegistrationCompanyDialogComponent,
        RegistrationQuestionnaireComponent,
        RegistrationPavDialogComponent,
        RegistrationAccessCodeComponent
    ],
    providers: [
        RegistrationService,
        BackgroundUtils,
        ModalUtils,
        RegistrationDialogService,
        RegistrationGuardService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    entryComponents: [
        JobseekerDialogComponent,
        RegistrationPavDialogComponent,
        RegistrationCompanyDialogComponent
    ]
})
export class JobroomAccountModule {
}
