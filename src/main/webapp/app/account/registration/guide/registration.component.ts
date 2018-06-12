import { Component, OnInit } from '@angular/core';
import { registrationQuestionnaireRoute } from '../questionnaire/registration-questionnaire.route';

@Component({
    selector: 'jr2-registration',
    templateUrl: './registration.component.html',
    styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit {

    selectPagePath = '/#/' + registrationQuestionnaireRoute.path;

    constructor() {
    }

    ngOnInit() {
    }

}
