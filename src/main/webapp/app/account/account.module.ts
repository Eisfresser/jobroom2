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
import { StoreModule } from '@ngrx/store';
import { RegistrationQuestionnaireEffects } from './registration/questionnaire/state-management/effects/registration-questionnaire.effects';
import { EffectsModule } from '@ngrx/effects';
import { registrationQuestionnaireReducer } from './registration/questionnaire/state-management/reducers/registration-questionnaire.reducers';
import {
    accountState,
    ActivateComponent,
    ActivateService,
    JobseekerDialogComponent,
    PasswordComponent,
    PasswordResetFinishComponent,
    PasswordResetFinishService,
    PasswordResetInitComponent,
    PasswordResetInitService,
    PasswordService,
    PasswordStrengthBarComponent,
    RegistrationCompanyDialogComponent,
    RegistrationPavDialogComponent,
    RegistrationService,
    ReLoginComponent,
    SettingsComponent
} from './';
import { RegistrationDialogService } from './registration/registration-dialog.service';
import { RegistrationAccessCodeComponent } from './registration/access-code/registration-access-code.component';
import { ExistingPavDialogComponent } from './registration/existing-pav/existing-pav-dialog.component';
import { RegistrationGuardService } from './registration/registration-guard.service';

@NgModule({
    imports: [
        JobroomSharedModule,
        ReactiveFormsModule,
        CommonModule,
        RouterModule.forRoot(accountState, { useHash: true }),
        BrowserModule,
        HttpClientModule,
        StoreModule.forFeature('registrationQuestionnaire', registrationQuestionnaireReducer),
        EffectsModule.forFeature([RegistrationQuestionnaireEffects]),
        JobroomSharedCommonModule
    ],
    declarations: [
        ActivateComponent,
        ReLoginComponent,
        PasswordComponent,
        PasswordStrengthBarComponent,
        PasswordResetInitComponent,
        PasswordResetFinishComponent,
        SettingsComponent,
        JobseekerDialogComponent,
        ExistingPavDialogComponent,
        RegistrationCompanyDialogComponent,
        RegistrationQuestionnaireComponent,
        RegistrationCompanyDialogComponent,
        RegistrationPavDialogComponent,
        RegistrationAccessCodeComponent
    ],
    providers: [
        ActivateService,
        PasswordService,
        PasswordResetInitService,
        PasswordResetFinishService,
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
        ExistingPavDialogComponent
    ]
})
export class JobroomAccountModule {
}
