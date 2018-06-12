import { Injectable } from '@angular/core';
import { Actions, Effect } from '@ngrx/effects';
import { Action } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import {
    SELECT_REGISTRATION_ROLE,
    ACCEPT_TERMS_AND_CONDITIONS,
    SELECT_WHETHER_USER_EXIST_OR_NOT,
    SelectRegistrationRoleAction,
    ShowTermsAndConditionsSectionAction,
    ShowIfUserExistOrNotSectionAction,
    ActivateNextButtonAction,
    ResetRegistrationQuestionnaireAction, NEXT_REGISTRATION_PAGE,
    NextRegistrationPageAction
} from '../actions/registration-questionnaire.actions';
import { Router } from '@angular/router';
import { registrationRoute } from '../../../guide/registration.route';
import { RegistrationService } from '../../../registration.service';
import { RegistrationCompanyDialogComponent } from '../../../..';
import { RegistrationDialogService } from '../../../registration-dialog.service';
import { ModalUtils } from '../../../../../shared';

@Injectable()
export class RegistrationQuestionnaireEffects {

    @Effect()
    selectRegistrationRole: Observable<Action> = this.actions$
        .ofType(SELECT_REGISTRATION_ROLE)
        .map((action: SelectRegistrationRoleAction) => {
                switch (action.payload) {
                    case 'rav' :
                    case 'employer':
                        return new ShowTermsAndConditionsSectionAction();
                    case 'agency' :
                        return new ShowIfUserExistOrNotSectionAction();
                    default:
                        return new ResetRegistrationQuestionnaireAction();
                }
            }
        );

    @Effect()
    selectWhetherUserExistOrNot: Observable<Action> = this.actions$
        .ofType(SELECT_WHETHER_USER_EXIST_OR_NOT)
        .map((_) => new ShowTermsAndConditionsSectionAction());

    @Effect()
    acceptTermsAndConditions: Observable<Action> = this.actions$
        .ofType(ACCEPT_TERMS_AND_CONDITIONS)
        .map((_) => new ActivateNextButtonAction());

    @Effect({ dispatch: false })
    nextRegistrationPage: Observable<Action> = this.actions$
        .ofType(NEXT_REGISTRATION_PAGE)
        .do((action: NextRegistrationPageAction) => {
            switch (action.payload.role) {
                case 'employer':
                    this.modalUtils.openLargeModal(RegistrationCompanyDialogComponent);
                    break;
                case 'agency':
                    if (action.payload.user.toString() === 'true') {
                        this.registrationDialogService.openExistingPavDialog();
                    } else {
                        this.registrationDialogService.openRegisterPavDialog();
                    }
                    break;
                case 'rav':
                    this.registrationDialogService.openRegisterJobSeekerDialog();
                    break;
                default:
                    this.router.navigate(['/' + registrationRoute.path]);
            }
        });

    constructor(private actions$: Actions,
                private router: Router,
                private registrationDialogService: RegistrationDialogService,
                private registrationService: RegistrationService,
                private modalUtils: ModalUtils) {
    }
}
