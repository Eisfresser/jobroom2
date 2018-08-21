import { Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import {
    Candidate,
    CandidateProfile,
    Degree,
    JobExperience
} from '../services/candidate';
import { Observable } from 'rxjs/Observable';
import {
    GenderAwareOccupationLabel,
    JobCenter,
    OccupationPresentationService,
    ReferenceService
} from '../../shared/reference-service';
import { CandidateService } from '../services/candidate.service';
import { TranslateService } from '@ngx-translate/core';
import { Store } from '@ngrx/store';
import {
    CandidateSearchFilter,
    CandidateSearchState,
    getCandidateProfileList,
    getSearchFilter,
    getSelectedCandidateProfile,
    getTotalCandidateCount
} from '../state-management/state/candidate-search.state';
import { Contact, Gender, Graduation, Principal } from '../../shared';
import {
    MailToOpenedAction,
    PhoneToOpenedAction,
    PrintCandidateAction
} from '../state-management/actions/candidate-search.actions';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { TOOLTIP_AUTO_HIDE_TIMEOUT } from '../../app.constants';
import { LanguageSkill } from '../../shared/job-advertisement/job-advertisement.model';
import { CandidateAnonymousContactDialogService } from '../dialog/candidate-anonymous-contact-dialog.service';
import { EmailContent } from '../services/mail.service';
import { CurrentSelectedCompanyService } from '../../shared/company/current-selected-company.service';

interface EnrichedJobExperience extends JobExperience {
    occupationLabels: {
        male: String,
        female: String
    }
}

@Component({
    selector: 'jr2-candidate-detail',
    templateUrl: './candidate-detail.component.html',
    styleUrls: ['./candidate-detail.component.scss']
})
export class CandidateDetailComponent implements OnInit {

    private readonly SWISS_CANTONS_NUMBER = 26;
    private readonly ABROAD_CODE = '99';
    private readonly SWISS_CODE = 'CH';

    candidateProfile$: Observable<CandidateProfile>;
    jobExperiences$: Observable<Array<EnrichedJobExperience>>;
    jobCenter$: Observable<JobCenter>;
    candidateProtectedData$: Observable<Candidate>;
    candidateProfiles$: Observable<Array<CandidateProfile>>;
    candidateProfileListTotalSize$: Observable<number>;
    relevantJobExperience$: Observable<JobExperience>;
    preferredWorkRegions$: Observable<Array<string>>;
    preferredWorkCantons$: Observable<Array<string>>;
    contact$: Observable<Contact>;
    languageSkills$: Observable<LanguageSkill[]>;
    emailContent$: Observable<EmailContent>;
    anonymousContactSuccess = false;

    @ViewChild(NgbTooltip)
    clipboardTooltip: NgbTooltip;

    @ViewChild('copyToClipboard')
    copyToClipboardElementRef: ElementRef;

    constructor(private referenceService: ReferenceService,
                private candidateService: CandidateService,
                private occupationPresentationService: OccupationPresentationService,
                private translateService: TranslateService,
                private principal: Principal,
                private currentSelectedCompanyService: CurrentSelectedCompanyService,
                private store: Store<CandidateSearchState>,
                private anonymousContactDialogService: CandidateAnonymousContactDialogService) {
    }

    ngOnInit() {
        this.candidateProfile$ = this.store.select(getSelectedCandidateProfile)
            .filter((candidateProfile) => !!candidateProfile)
            .map((candidateProfile) => {
                this.buildEmailContent(candidateProfile);
                return candidateProfile;
            });

        this.jobCenter$ = this.candidateProfile$
            .map((candidateProfile) => candidateProfile.jobCenterCode)
            .flatMap((jobCenterCode) => jobCenterCode
                ? this.referenceService.resolveJobCenter(jobCenterCode)
                : Observable.empty());

        this.candidateProtectedData$ = this.candidateProfile$
            .flatMap((candidateProfile) => this.candidateService.findCandidate(candidateProfile));

        this.candidateProfiles$ = this.store.select(getCandidateProfileList);
        this.candidateProfileListTotalSize$ = this.store.select(getTotalCandidateCount);

        this.jobExperiences$ = this.candidateProfile$
            .flatMap((profile) => {
                const wantedJobExperiences = profile.jobExperiences
                    .filter((experience) => experience.wanted);
                return Observable.combineLatest(wantedJobExperiences.map(this.enrichWithLabels.bind(this)))
                    .map((jobExperiences) => jobExperiences.map(this.formatOccupationLabel(profile.gender)))
            })
            .map((experiences) => experiences.sort((a, b) => +b.lastJob - +a.lastJob))
            .shareReplay();

        const occupationCodes$ = this.store.select(getSearchFilter)
            .map((searchFilter: CandidateSearchFilter) =>
                searchFilter.occupations
                    ? searchFilter.occupations.map((typeaheadMultiselectModel) => typeaheadMultiselectModel.code)
                    : []
            );

        this.relevantJobExperience$ = this.jobExperiences$
            .combineLatest(occupationCodes$, this.jobExperiences$)
            .map(([jobExperiences, occupationCodes]) =>
                this.candidateService.getRelevantJobExperience(occupationCodes, jobExperiences));
        this.populatePreferredWorkLocations();

        this.contact$ = Observable.combineLatest(this.candidateProfile$, this.jobCenter$)
            .map(([candidateProfile, jobCenter]) => {
                if (jobCenter && (jobCenter.code.startsWith('BEA') || jobCenter.code.startsWith('BSA'))) {
                    return { phone: jobCenter.phone, email: jobCenter.email };
                } else {
                    return candidateProfile.jobAdvisor;
                }
            });

        this.languageSkills$ = this.candidateProfile$
            .map(this.mapLanguages);
    }

    private mapLanguages(candidateProfile: CandidateProfile): LanguageSkill[] {
        return candidateProfile.languages
            .map((languageSkill) => ({
                languageIsoCode: languageSkill.code,
                spokenLevel: languageSkill.spokenLevel,
                writtenLevel: languageSkill.writtenLevel
            }));
    }

    private enrichWithLabels(jobExperience: JobExperience): Observable<EnrichedJobExperience> {
        const currentLanguage$ = this.translateService.onLangChange
            .map((langChange) => langChange.lang)
            .startWith(this.translateService.currentLang);

        return currentLanguage$
            .switchMap((language) =>
                this.occupationPresentationService.findOccupationLabelsByAvamCode(jobExperience.occupation.avamCode, language)
                    .map((occupationLabels: GenderAwareOccupationLabel) =>
                        Object.assign({}, jobExperience, { occupationLabels }))
            );
    }

    private formatOccupationLabel(gender: Gender): (jobExperience: EnrichedJobExperience) => EnrichedJobExperience {
        return (jobExperience: EnrichedJobExperience) => {
            const { male, female } = jobExperience.occupationLabels;
            const occupationLabel = (gender === Gender.FEMALE && female) ? female : male;
            return Object.assign({}, jobExperience, { occupationLabel });
        }
    }

    private populatePreferredWorkLocations(): void {
        this.preferredWorkRegions$ = this.candidateProfile$
            .flatMap((candidateProfile) => this.translateValues(candidateProfile.preferredWorkRegions,
                'global.reference.region.'));
        this.preferredWorkCantons$ = this.candidateProfile$
            .flatMap((candidateProfile) => {
                if (!(candidateProfile.preferredWorkCantons && candidateProfile.preferredWorkCantons.length)) {
                    return Observable.of([]);
                }
                const swissCantons = candidateProfile.preferredWorkCantons
                    .filter((canton) => canton !== this.ABROAD_CODE);
                if (swissCantons.length < this.SWISS_CANTONS_NUMBER) {
                    return this.translateValues(candidateProfile.preferredWorkCantons, 'global.reference.canton.');
                }

                const workAbroad = candidateProfile.preferredWorkCantons
                    .some((canton) => canton === this.ABROAD_CODE);
                if (workAbroad) {
                    return this.translateValues([this.SWISS_CODE, this.ABROAD_CODE], 'global.reference.canton.')
                }
                return this.translateValues([this.SWISS_CODE], 'global.reference.canton.')
            });
    }

    private translateValues(values: Array<string>, keyPrefix: string): Observable<Array<string>> {
        // TODO: implement translation pipe
        if (values && values.length) {
            const keys = values.map((value) => keyPrefix.concat(value));
            return this.translateService.stream(keys)
                .map((translations) => keys.map((key) => translations[key]));
        }
        return Observable.of([]);
    }

    private buildEmailContent(candidate: CandidateProfile): void {
        // todo: Clean up this!!!
        this.emailContent$ = this.candidateService.canSendAnonymousContactEmail(candidate)
            .filter((canSendEmail) => canSendEmail)
            .flatMap(() => {
                const selectedCompanyContactTemplate$ = this.currentSelectedCompanyService.getSelectedCompanyContactTemplate();
                const translations$ = this.translateService.stream(['candidate-detail.candidate-anonymous-contact.subject', 'candidate-detail.candidate-anonymous-contact.body']);
                return Observable.combineLatest(selectedCompanyContactTemplate$, translations$)
                    .map(([companyContactTemplateModel, translations]) => {
                        return {
                            candidateId: candidate.id,
                            subject: translations['candidate-detail.candidate-anonymous-contact.subject'],
                            body: translations['candidate-detail.candidate-anonymous-contact.body'],
                            companyName: companyContactTemplateModel.companyName,
                            phone: null,
                            email: companyContactTemplateModel.email,
                            company: {
                                name: companyContactTemplateModel.companyName,
                                contactPerson: companyContactTemplateModel.firstName + ' ' + companyContactTemplateModel.lastName,
                                street: companyContactTemplateModel.companyStreet,
                                houseNumber: companyContactTemplateModel.companyHouseNr,
                                zipCode: companyContactTemplateModel.companyZipCode,
                                city: companyContactTemplateModel.companyCity,
                                country: null
                            }
                        };
                    });
            });
    }

    printCandidateDetails(): void {
        this.store.dispatch(new PrintCandidateAction());
    }

    onSendLink(): void {
        this.store.dispatch(new MailToOpenedAction('sendLink'));
    }

    onMailToJobCenter(): void {
        this.store.dispatch(new MailToOpenedAction('jobCenter'));
    }

    onPhoneToJobCenter(): void {
        this.store.dispatch(new PhoneToOpenedAction('jobCenter'));
    }

    onMailToCandidate(): void {
        this.store.dispatch(new MailToOpenedAction('candidate'));
    }

    onPhoneToCandidate(): void {
        this.store.dispatch(new PhoneToOpenedAction('candidate'));
    }

    getCandidateUrl() {
        return window.location.href;
    }

    getEncodedCandidateUrl() {
        // todo: Review if we need a pipe for this.
        return encodeURIComponent(this.getCandidateUrl());
    }

    isDisplayGraduation(graduation: string): boolean {
        return graduation && graduation !== Graduation[Graduation.NONE];
    }

    isDisplayDegree(degree: string): boolean {
        return degree && Degree[degree] >= Degree.SEK_II_WEITERFUEHRENDE_SCHULE
            && Degree[degree] <= Degree.TER_DOKTORAT_UNIVERSITAET;
    }

    isAuthenticated(): boolean {
        return this.principal.isAuthenticated();
    }

    onCopyLink(event: Event): void {
        if (!this.clipboardTooltip.isOpen()) {
            this.clipboardTooltip.open();
            setTimeout(() => this.clipboardTooltip.close(), TOOLTIP_AUTO_HIDE_TIMEOUT);
        }
    }

    @HostListener('document:click', ['$event.target'])
    onClick(targetElement: HTMLElement): void {
        if (!targetElement) {
            return;
        }

        if (!this.copyToClipboardElementRef.nativeElement.contains(targetElement)) {
            this.clipboardTooltip.close();
        }
    }

    openAnonymousContactDialog(emailContent: EmailContent): void {
        this.anonymousContactDialogService.open(emailContent)
            .then(() => this.anonymousContactSuccess = true, () => {});
    }

    hideAnonymousContactSuccess(): void {
        this.anonymousContactSuccess = false;
    }
}
