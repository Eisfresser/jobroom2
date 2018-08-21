import { Component, OnInit } from '@angular/core';
import { CurrentSelectedCompanyService } from '../../company/current-selected-company.service';
import { Observable } from 'rxjs/Observable';
import { Accountability } from '../../user-info/user-info.model';

@Component({
    selector: 'jr2-company-selection',
    templateUrl: './company-selection.html'
})
export class CompanySelectionComponent implements OnInit {

    public availableCompanies$: Observable<Array<Accountability>>;

    public selectedCompany$: Observable<Accountability>;

    constructor(private currentSelectedCompanyService: CurrentSelectedCompanyService) {
    }

    ngOnInit(): void {
        this.availableCompanies$ = this.currentSelectedCompanyService.getAccountabilities();
        this.selectedCompany$ = this.currentSelectedCompanyService.getSelectedAccountability();
    }

    public onCompanySelected(selectedCompany: Accountability) {
        this.currentSelectedCompanyService.selectAccountability(selectedCompany).subscribe()
    }

}
