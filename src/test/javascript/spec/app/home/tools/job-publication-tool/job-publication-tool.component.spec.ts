import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { JobPublicationToolComponent } from '../../../../../../../main/webapp/app/home/tools/job-publication-tool/job-publication-tool.component';
import { ReactiveFormsModule } from '@angular/forms';
import { LanguageSkillService } from '../../../../../../../main/webapp/app/candidate-search/services/language-skill.service';
import { OccupationPresentationService } from '../../../../../../../main/webapp/app/shared/reference-service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs/Observable';
import { JobAdvertisementService } from '../../../../../../../main/webapp/app/shared/job-advertisement/job-advertisement.service';
import { LanguageFilterService } from '../../../../../../../main/webapp/app/shared/input-components/language-filter/language-filter.service';
import { Store } from '@ngrx/store';
import { CurrentSelectedCompanyService } from '../../../../../../../main/webapp/app/shared/company/current-selected-company.service';

describe('JobPublicationToolComponent', () => {
    let component: JobPublicationToolComponent;
    let fixture: ComponentFixture<JobPublicationToolComponent>;
    const mockOccupationPresentationService = jasmine.createSpyObj('mockOccupationPresentationService',
        ['fetchOccupationSuggestions', 'occupationFormatter']);
    const mockLanguageSkillService = jasmine.createSpyObj('mockLanguageSkillService', ['getLanguages']);
    const mockJobAdvertisementService = jasmine.createSpyObj('mockJobAdvertisementService', ['findById', 'save']);
    const mockLanguageFilterService = jasmine.createSpyObj('LanguageFilterService', ['getSorterLanguageTranslations']);
    const mockStore = jasmine.createSpyObj('mockStore', ['select']);
    const mockCurrentSelectedCompanyService = jasmine.createSpyObj('mockCurrentSelectedCompanyService', ['getSelectedCompanyContactTemplate']);

    mockStore.select.and.returnValue(Observable.of([]));
    mockCurrentSelectedCompanyService.getSelectedCompanyContactTemplate.and.returnValue(Observable.of({}));

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [ReactiveFormsModule],
            declarations: [JobPublicationToolComponent],
            providers: [
                {
                    provide: OccupationPresentationService,
                    useValue: mockOccupationPresentationService
                },
                {
                    provide: JobAdvertisementService,
                    useValue: mockJobAdvertisementService
                },
                {
                    provide: LanguageFilterService,
                    useValue: mockLanguageFilterService
                },
                { provide: LanguageSkillService, useValue: mockLanguageSkillService },
                { provide: TranslateService, useValue: { currentLang: 'de', onLangChange: Observable.never() } },
                { provide: Store, useValue: mockStore },
                { provide: CurrentSelectedCompanyService, useValue: mockCurrentSelectedCompanyService }
            ]
        })
            .overrideTemplate(JobPublicationToolComponent, '')
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(JobPublicationToolComponent);
        component = fixture.componentInstance;
        // component.userData = {} as CurrentUser;
        fixture.detectChanges();
    });

    it('should be created', () => {
        expect(component).toBeTruthy();
    });
});
