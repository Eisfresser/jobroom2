import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { PeaDashboardComponent } from '../../../../../../main/webapp/app/dashboard/pea-dashboard/pea-dashboard.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MockPrincipal } from '../../../helpers/mock-principal.service';
import { Principal } from '../../../../../../main/webapp/app/shared';
import { TranslateService } from '@ngx-translate/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import { HttpClientModule } from '@angular/common/http';
import { JobAdvertisementService } from '../../../../../../main/webapp/app/shared/job-advertisement/job-advertisement.service';
import { JobAdvertisementCancelDialogService } from '../../../../../../main/webapp/app/dashboard/dialogs/job-advertisement-cancel-dialog.service';
import { CompanyService } from '../../../../../../main/webapp/app/shared/company/company.service';
import { CurrentSelectedCompanyService } from '../../../../../../main/webapp/app/shared/company/current-selected-company.service';

describe('PEA-DashboardComponent', () => {
    let component: PeaDashboardComponent;
    let fixture: ComponentFixture<PeaDashboardComponent>;
    const mockStore = jasmine.createSpyObj('mockStore', ['select', 'dispatch']);
    mockStore.select.and.returnValue(Observable.of([]));
    const mockCompanyService = jasmine.createSpyObj('mockCompanyService', ['findByExternalId']);
    const mockJobPublicationCancelDialogService = jasmine.createSpyObj('mockJobPublicationCancelDialogService', ['open']);
    const mockTranslateService = jasmine.createSpyObj('mockTranslateService', ['v']);
    const mockCurrentSelectedCompanyService = jasmine.createSpyObj('mockCurrentSelectedCompanyService', ['getSelectedAccountability']);

    mockCurrentSelectedCompanyService.getSelectedAccountability.and.returnValue(Observable.of({}));

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                HttpClientModule,
            ],
            declarations: [PeaDashboardComponent],
            providers: [
                {
                    provide: Principal,
                    useClass: MockPrincipal
                },
                {
                    provide: CompanyService,
                    useValue: mockCompanyService
                },
                {
                    provide: JobAdvertisementCancelDialogService,
                    useValue: mockJobPublicationCancelDialogService
                },
                {
                    provide: TranslateService,
                    useValue: mockTranslateService
                },
                {
                    provide: Store,
                    useValue: mockStore
                },
                {
                    provide: CurrentSelectedCompanyService,
                    useValue: mockCurrentSelectedCompanyService
                },
                JobAdvertisementService
            ]
        })
            .overrideTemplate(PeaDashboardComponent, '')
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(PeaDashboardComponent);
        component = fixture.componentInstance;
    });

    it('should be created', () => {
        component.jobAdvertisementFilter = {
            jobTitle: '',
            onlineSinceDays: 3
        };
        fixture.detectChanges();
        expect(component).toBeTruthy();
    });
});
