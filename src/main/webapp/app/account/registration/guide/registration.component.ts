import { Component, OnInit } from '@angular/core';

@Component({
    selector: 'jr2-registration',
    templateUrl: './registration.component.html',
    styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit {

    selectPagePath = '/samllogin';

    constructor() {
    }

    ngOnInit() {
    }

}
