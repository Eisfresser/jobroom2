import { BackgroundUtils } from '../../../shared';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import {
    getNextButtonActive,
    getShowIfUserExistOrNotSection,
    getShowTermsAndConditions,
    getTermsAndConditionsChecked,
    initialState,
    RegistrationQuestionnaireState
} from './state-management/state/registration-questionnaire.state';
import { Store } from '@ngrx/store';
import {
    AcceptTermsAndConditionsAction,
    NextRegistrationPageAction,
    ResetRegistrationQuestionnaireAction,
    SelectRegistrationRoleAction,
    SelectWhetherUserExistOrNotAction
} from './state-management/actions/registration-questionnaire.actions';
import { Observable } from 'rxjs/Observable';

@Component({
    selector: 'jr2-registration-questionnaire',
    templateUrl: './registration-questionnaire.component.html',
    styleUrls: ['./registration-questionnaire.component.scss']
})
export class RegistrationQuestionnaireComponent implements OnInit, OnDestroy {
    roleForm: FormGroup;

    termsAndConditionsSectionVisible$: Observable<boolean>;
    userExistSectionVisible$: Observable<boolean>;
    nextButtonActive$: Observable<boolean>;
    termsAndConditionsChecked$: Observable<boolean>;

    constructor(private store: Store<RegistrationQuestionnaireState>,
                private fb: FormBuilder,
                private backgroundUtils: BackgroundUtils) {
        this.termsAndConditionsSectionVisible$ = this.store.select(getShowTermsAndConditions);
        this.userExistSectionVisible$ = this.store.select(getShowIfUserExistOrNotSection);
        this.nextButtonActive$ = this.store.select(getNextButtonActive);
        this.termsAndConditionsChecked$ = this.store.select(getTermsAndConditionsChecked);
    }

    ngOnInit() {
        this.backgroundUtils.addBackgroundForJobseekers();
        this.roleForm = this.fb.group(initialState);
        this.roleForm.get('role')
            .valueChanges
            .subscribe((value) => {
                this.roleForm.get('user')
                    .reset(false);
                return this.store.dispatch(new SelectRegistrationRoleAction(value));
            });

        this.roleForm.get('termsAndConditions')
            .valueChanges
            .subscribe((value) => this.store.dispatch(new AcceptTermsAndConditionsAction(value)));

        this.roleForm.get('user')
            .valueChanges
            .subscribe((value) => this.store.dispatch(new SelectWhetherUserExistOrNotAction(value)));
    }

    ngOnDestroy(): void {
        this.backgroundUtils.removeAllBackgroundClasses();
        this.store.complete();
    }

    cancel() {
        this.roleForm.reset(initialState);
        this.store.dispatch(new ResetRegistrationQuestionnaireAction());
    }

    next() {
        this.store.dispatch(new NextRegistrationPageAction(this.roleForm.value));
    }
}
