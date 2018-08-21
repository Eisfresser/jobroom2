import { CandidateDetailComponent } from '../../../../../../main/webapp/app/candidate-search/candidate-detail/candidate-detail.component';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { JobroomTestModule } from '../../../test.module';
import { cold } from 'jasmine-marbles';
import {
    OccupationPresentationService,
    ReferenceService
} from '../../../../../../main/webapp/app/shared/reference-service';
import { CandidateService } from '../../../../../../main/webapp/app/candidate-search/services/candidate.service';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { Store, StoreModule } from '@ngrx/store';
import { candidateSearchReducer } from '../../../../../../main/webapp/app/candidate-search/state-management/reducers/candidate-search.reducers';
import { MockPrincipal } from '../../../helpers/mock-principal.service';
import { Principal } from '../../../../../../main/webapp/app/shared';
import { CandidateProfileDetailLoadedAction } from '../../../../../../main/webapp/app/candidate-search/state-management/actions/candidate-search.actions';
import { CandidateAnonymousContactDialogService } from '../../../../../../main/webapp/app/candidate-search/dialog/candidate-anonymous-contact-dialog.service';
import { CurrentSelectedCompanyService } from '../../../../../../main/webapp/app/shared/company/current-selected-company.service';

describe('CandidateDetailComponent', () => {
    let component: CandidateDetailComponent;
    let fixture: ComponentFixture<CandidateDetailComponent>;

    const candidateProfile: any = {
        id: '1111',
        jobExperiences: [
            {
                occupation: {
                    avamCode: 22222,
                    bfsCode: 22,
                    sbn3Code: 222,
                    sbn5Code: 22222
                },
                wanted: true
            }
        ]
    };
    const mockActivatedRoute: any = { data: Observable.of({ 'candidateProfile': candidateProfile }) };
    const mockReferenceService = jasmine.createSpyObj('mockReferenceService', ['resolveJobCenter']);
    const mockCandidateService = jasmine.createSpyObj('mockCandidateService', ['findCandidate', 'canSendAnonymousContactEmail']);
    const mockOccupationOccupationPresentationService = jasmine.createSpyObj('mockOccupationOccupationPresentationService', ['findOccupationLabelsByAvamCode']);
    const mockCandidateAnonymousContactDialogService = jasmine.createSpyObj('mockCandidateAnonymousContactDialogService', ['open']);
    const mockCurrentSelectedCompanyService = jasmine.createSpyObj('mockCurrentSelectedCompanyService', ['getSelectedCompanyContactTemplate']);

    mockCurrentSelectedCompanyService.getSelectedCompanyContactTemplate.and.returnValue(Observable.of({}));

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [JobroomTestModule, StoreModule.forRoot({ candidateSearch: candidateSearchReducer })],
            declarations: [CandidateDetailComponent],
            providers: [
                { provide: ActivatedRoute, useValue: mockActivatedRoute },
                { provide: ReferenceService, useValue: mockReferenceService },
                { provide: CandidateService, useValue: mockCandidateService },
                { provide: Principal, useClass: MockPrincipal },
                {
                    provide: OccupationPresentationService,
                    useValue: mockOccupationOccupationPresentationService
                },
                {
                    provide: TranslateService, useValue: {
                        currentLang: 'en',
                        onLangChange: Observable.never()
                    }
                },
                {
                    provide: CandidateAnonymousContactDialogService,
                    useValue: mockCandidateAnonymousContactDialogService
                },
                {
                    provide: CurrentSelectedCompanyService,
                    useValue: mockCurrentSelectedCompanyService
                }
            ]
        })
            .overrideTemplate(CandidateDetailComponent, '')
            .compileComponents();
        const store = TestBed.get(Store);
        store.dispatch(new CandidateProfileDetailLoadedAction(candidateProfile));
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(CandidateDetailComponent);
        component = fixture.componentInstance;
    });

    it('should be created', () => {
        expect(component).toBeTruthy();
    });

    it('should populate occupationLabels', () => {
        // GIVEN
        const occupation$ = cold('-a', {
            a: {
                male: 'Text-M',
                female: 'Text-F'
            }
        });
        mockOccupationOccupationPresentationService.findOccupationLabelsByAvamCode.and.returnValue(occupation$);

        const emailContent$ = cold('-a', {
            a: false
        });
        mockCandidateService.canSendAnonymousContactEmail.and.returnValue(emailContent$);

        // WHEN
        fixture.detectChanges();

        // THEN
        const expected = cold('-b', {
            b: [{
                occupation: {
                    avamCode: 22222,
                    bfsCode: 22,
                    sbn3Code: 222,
                    sbn5Code: 22222
                },
                occupationLabels: {
                    male: 'Text-M',
                    female: 'Text-F'
                },
                occupationLabel: 'Text-M',
                wanted: true
            }]
        });

        expect(component.jobExperiences$).toBeObservable(expected);
    });
});
