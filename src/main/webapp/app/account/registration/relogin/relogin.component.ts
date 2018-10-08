import { Component, OnInit } from '@angular/core';
import { LoginService } from '../../../shared';

@Component({
  selector: 'jr2-relogin',
  templateUrl: './relogin.component.html',
  styleUrls: ['./relogin.component.scss']
})
export class ReLoginComponent implements OnInit {

    constructor(private loginService: LoginService) { }

    ngOnInit() {
    }

    logout() {
        this.loginService.logout();
        document.location.href = 'authentication/logout';
    }

}
