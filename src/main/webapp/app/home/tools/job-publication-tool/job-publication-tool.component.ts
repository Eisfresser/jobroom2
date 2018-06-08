import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component, ElementRef,
    Input,
    OnDestroy,
    OnInit, ViewChild
} from '@angular/core';
import { Observable } from 'rxjs/Observable';
import {
    AbstractControl, FormArray,
    FormBuilder, FormGroup,
    Validators
} from '@angular/forms';
import { Subject } from 'rxjs/Subject';
import {
    DateUtils, Degree,
    EMAIL_REGEX,
    Gender,
    POSTBOX_NUMBER_REGEX,
    ResponseWrapper,
    URL_REGEX, WorkForm
} from '../../../shared';
import { LanguageSkillService } from '../../../candidate-search/services/language-skill.service';
import {
    FormatterFn,
    OccupationOption,
    OccupationPresentationService,
    SuggestionLoaderFn
} from '../../../shared/reference-service';
import { Translations } from './zip-code/zip-code.component';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import * as countries from 'i18n-iso-countries';
import { Subscriber } from 'rxjs/Subscriber';
import { JobPublicationMapper } from './job-publication-mapper';
import { Organization } from '../../../shared/organization/organization.model';
import { UserData } from './service/user-data-resolver.service';
import { JobAdvertisementService } from '../../../shared/job-advertisement/job-advertisement.service';
import { JobAdvertisement, Salutation, WorkExperience } from '../../../shared/job-advertisement/job-advertisement.model';
import { CompanyFormModel, JobPublicationForm } from './job-publication-form.model';
import { LanguageFilterService } from '../../../shared/input-components/language-filter/language-filter.service';
import { languages } from '../../../candidate-search/services/language-skill.service';
import { Store } from '@ngrx/store';
import { CoreState, getLanguage } from '../../../shared/state-management/state/core.state';

@Component({
    selector: 'jr2-job-publication-tool',
    templateUrl: './job-publication-tool.component.html',
    styleUrls: ['./job-publication-tool.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class JobPublicationToolComponent implements OnInit, OnDestroy {
    private readonly SWITZ_KEY = 'CH';
    readonly APPLICATION_ADDITIONAL_INFO_MAX_LENGTH = 240;

    @Input()
    jobAdvertisement: JobAdvertisement;
    @Input()
    userData: UserData;

    @ViewChild('employmentStartDateEl')
    employmentStartDateElementRef: ElementRef;

    @ViewChild('employmentEndDateEl')
    employmentEndDateElementRef: ElementRef;

    degrees = Degree;
    experiences = WorkExperience;
    salutations = Salutation;
    workForms = WorkForm;
    languageSkills$: Observable<Array<string>>;
    languageOptionTranslations$: Observable<Array<{ key: string, value: string }>>;

    jobPublicationForm: FormGroup;
    employmentStartDateByArrangement = true;
    employmentEndDateIsPermanent = true;
    employmentStartDateMin = DateUtils.mapDateToNgbDateStruct();
    employmentEndDateMin = DateUtils.mapDateToNgbDateStruct();

    fetchOccupationSuggestions: SuggestionLoaderFn<Array<OccupationOption>>;
    occupationFormatter: FormatterFn<OccupationOption>;

    countries$: Observable<{ key: string, value: string }[]>;

    showSuccessSaveMessage: boolean;
    showErrorSaveMessage: boolean;

    private unsubscribe$ = new Subject<void>();

    constructor(private coreStore: Store<CoreState>,
                private occupationPresentationService: OccupationPresentationService,
                private fb: FormBuilder,
                private languageSkillService: LanguageSkillService,
                private translateService: TranslateService,
                private jobAdvertisementService: JobAdvertisementService,
                private languageFilterService: LanguageFilterService,
                private cd: ChangeDetectorRef) {
    }

    ngOnInit(): void {
        this.fetchOccupationSuggestions = this.occupationPresentationService.fetchJobPublicationOccupationSuggestions;
        this.occupationFormatter = this.occupationPresentationService.occupationFormatter;
        this.updateOccupationOnLanguageChange();
        this.languageOptionTranslations$ = this.languageFilterService.getSorterLanguageTranslations(languages);

        this.languageSkills$ = this.languageSkillService.getLanguages();
        this.setupCountries();

        let formModel: JobPublicationForm = this.createDefaultFormModel();
        if (this.jobAdvertisement) {
            formModel = JobPublicationMapper.mapJobPublicationToFormModel(formModel, this.jobAdvertisement);
        }

        this.jobPublicationForm = this.createJobPublicationForm(formModel);
        this.configureEmployerSection(formModel);

        this.configureDateInput('employment.employmentStartDate.date', 'employment.employmentStartDate.immediate',
            (disabled) => {
                this.employmentStartDateByArrangement = disabled;
                if (!disabled) {
                    setTimeout(() => {
                        this.employmentStartDateElementRef.nativeElement.focus();
                    }, 0);
                }
            });
        this.configureDateInput('employment.employmentEndDate.date', 'employment.employmentEndDate.permanent',
            (disabled) => {
                this.employmentEndDateIsPermanent = disabled;
                if (!disabled) {
                    setTimeout(() => {
                        this.employmentEndDateElementRef.nativeElement.focus();
                    }, 0);
                }
            });
        this.updateEmploymentStartDateRelatedField();
        this.configurePublicContactSection();
    }

    private configurePublicContactSection() {
        const publicContactFieldValidators = {
            salutation: [],
            firstName: [],
            lastName: [],
            phoneNumber: [],
            email: [Validators.pattern(EMAIL_REGEX)]
        };
        const publicContactFieldNames = Object.keys(publicContactFieldValidators);

        const publicContact = this.jobPublicationForm.get('publicContact');

        const makeRequired = (name: string): void => {
            const field = publicContact.get(name);
            field.setValidators([Validators.required, ...publicContactFieldValidators[name]]);
            field.updateValueAndValidity({ emitEvent: false });
        };

        const makeFieldsRequired = (): void => {
            publicContactFieldNames.forEach((name) => {
                if (publicContact.get('phoneNumber').value && name === 'email'
                    || publicContact.get('email').value && name === 'phoneNumber') {
                    return;
                }

                makeRequired(name)
            });
        };

        const resetValidator = (name: string): void => {
            const field = publicContact.get(name);
            field.clearValidators();
            field.setValidators(publicContactFieldValidators[name]);
            field.updateValueAndValidity({ emitEvent: false });
        };

        const resetValidators = (): void => {
            publicContactFieldNames.forEach((name) => resetValidator(name));
        };

        const isFilled = (value) => publicContactFieldNames
            .map((name) => value[name])
            .some((fieldValue) => !!fieldValue);

        publicContact.valueChanges
            .takeUntil(this.unsubscribe$)
            .startWith(publicContact.value)
            .distinctUntilChanged()
            .subscribe((value) => {
                if (isFilled(value)) {
                    makeFieldsRequired();
                } else {
                    resetValidators();
                }
            });

        const resetRelatedFieldValidator = (fieldName: string, relatedFieldName: string): void => {
            publicContact.get(fieldName).valueChanges
                .takeUntil(this.unsubscribe$)
                .startWith(publicContact.get(fieldName).value)
                .distinctUntilChanged()
                .subscribe((value) => {
                    if (value) {
                        resetValidator(relatedFieldName);
                    }
                });
        };

        resetRelatedFieldValidator('phoneNumber', 'email');
        resetRelatedFieldValidator('email', 'phoneNumber');
    }

    private configureEmployerSection(formModel: JobPublicationForm) {
        const companySurrogate = this.jobPublicationForm.get('company.surrogate');
        companySurrogate.valueChanges
            .takeUntil(this.unsubscribe$)
            .startWith(companySurrogate.value)
            .subscribe((value) => {
                if (value) {
                    this.jobPublicationForm.addControl('employer',
                        this.fb.group({
                            name: [formModel.employer.name, Validators.required],
                            zipCode: [formModel.employer.zipCode],
                            countryCode: [formModel.employer.countryCode, Validators.required],
                        }));
                } else {
                    this.jobPublicationForm.removeControl('employer');
                }
            });
    }

    isEmployerEnabled(): boolean {
        return this.jobPublicationForm.get('company.surrogate').value;
    }

    private createJobPublicationForm(formModel: JobPublicationForm): FormGroup {
        return this.fb.group({
            jobDescriptions: [formModel.jobDescriptions],
            occupation: this.fb.group({
                occupationSuggestion: [formModel.occupation.occupationSuggestion, Validators.required],
                degree: [formModel.occupation.degree],
                experience: [formModel.occupation.experience]
            }),
            languageSkills: [formModel.languageSkills],
            employment: this.fb.group({
                workload: [formModel.employment.workload, Validators.required],
                employmentStartDate: this.fb.group({
                    immediate: formModel.employment.employmentStartDate.immediate,
                    date: [{
                        value: formModel.employment.employmentStartDate.date,
                        disabled: !formModel.employment.employmentStartDate.date
                    }, Validators.required]
                }),
                employmentEndDate: this.fb.group({
                    permanent: formModel.employment.employmentEndDate.permanent,
                    date: [{
                        value: formModel.employment.employmentEndDate.date,
                        disabled: !formModel.employment.employmentEndDate.date
                    }, Validators.required]
                }),
                shortEmployment: [formModel.employment.shortEmployment],
                workForms: this.fb.array([
                    formModel.employment.workForms[0],
                    formModel.employment.workForms[1],
                    formModel.employment.workForms[2],
                    formModel.employment.workForms[3]
                ])
            }),
            location: this.fb.group({
                countryCode: [formModel.location.countryCode, Validators.required],
                zipCode: [formModel.location.zipCode],
                additionalDetails: [formModel.location.additionalDetails]
            }),
            company: this.fb.group({
                name: [formModel.company.name, Validators.required],
                street: [formModel.company.street, Validators.required],
                houseNumber: [formModel.company.houseNumber],
                zipCode: [formModel.company.zipCode],
                postboxNumber: [formModel.company.postboxNumber, Validators.pattern(POSTBOX_NUMBER_REGEX)],
                postboxZipCode: [formModel.company.postboxZipCode],
                countryCode: [formModel.company.countryCode, Validators.required],
                surrogate: [formModel.company.surrogate]
            }),
            contact: this.fb.group({
                language: [formModel.contact.language, Validators.required],
                salutation: [formModel.contact.salutation, Validators.required],
                firstName: [formModel.contact.firstName, Validators.required],
                lastName: [formModel.contact.lastName, Validators.required],
                phoneNumber: [formModel.contact.phoneNumber,
                    [Validators.required]],
                email: [formModel.contact.email, [Validators.required, Validators.pattern(EMAIL_REGEX)]],
            }),
            publicContact: this.fb.group({
                salutation: [formModel.publicContact.salutation],
                firstName: [formModel.publicContact.firstName],
                lastName: [formModel.publicContact.lastName],
                phoneNumber: [formModel.publicContact.phoneNumber],
                email: [formModel.publicContact.email],
            }),
            application: this.fb.group({
                paperApplicationAddress: [formModel.application.paperApplicationAddress],
                electronicApplicationEmail: [formModel.application.electronicApplicationEmail, Validators.pattern(EMAIL_REGEX)],
                electronicApplicationUrl: [formModel.application.electronicApplicationUrl, Validators.pattern(URL_REGEX)],
                phoneNumber: [formModel.application.phoneNumber],
                additionalInfo: [formModel.application.additionalInfo,
                    [Validators.maxLength(this.APPLICATION_ADDITIONAL_INFO_MAX_LENGTH)]],
            }),
            publication: this.fb.group({
                publicDisplay: [formModel.publication.publicDisplay],
                eures: [formModel.publication.eures],
            }),
            disclaimer: this.fb.control(false, Validators.requiredTrue)
        });
    }

    private createDefaultFormModel(): JobPublicationForm {
        const userData: UserData = this.userData || {};
        const company: Organization = userData.organization || {};

        return {
            jobDescriptions: [],
            occupation: {
                occupationSuggestion: null,
                degree: null,
                experience: null
            },
            languageSkills: [],
            employment: {
                workload: [100, 100],
                employmentStartDate: {
                    immediate: true,
                    date: null
                },
                employmentEndDate: {
                    permanent: true,
                    date: null
                },
                shortEmployment: false,
                workForms: [
                    false,
                    false,
                    false,
                    false
                ]
            },
            location: {
                countryCode: this.SWITZ_KEY,
                additionalDetails: '',
                zipCode: {
                    zip: '',
                    city: ''
                }
            },
            company: {
                name: company.name,
                street: company.street,
                zipCode: {
                    zip: company.zipCode,
                    city: company.city
                },
                houseNumber: company.houseNumber,
                postboxNumber: '',
                postboxZipCode: {
                    zip: '',
                    city: ''
                },
                countryCode: this.SWITZ_KEY,
                surrogate: false
            },
            employer: {
                name: '',
                zipCode: {
                    zip: '',
                    city: ''
                },
                countryCode: this.SWITZ_KEY
            },
            contact: {
                language: this.translateService.currentLang,
                salutation: this.getSalutation(userData),
                firstName: userData.firstName,
                lastName: userData.lastName,
                phoneNumber: userData.phone,
                email: userData.email
            },
            publicContact: {
                salutation: null,
                firstName: '',
                lastName: '',
                phoneNumber: '',
                email: ''
            },
            application: {
                paperApplicationAddress: '',
                electronicApplicationEmail: '',
                electronicApplicationUrl: '',
                phoneNumber: '',
                additionalInfo: ''
            },
            publication: {
                publicDisplay: true,
                eures: false
            }
        };
    }

    copyFromContact() {
        const contact = this.jobPublicationForm.get('contact').value;
        this.jobPublicationForm.get('publicContact').patchValue(contact);
        return false;
    }

    copyAddressFromCompany() {
        const company: CompanyFormModel = this.jobPublicationForm.get('company').value;
        const addressParts = [company.name];
        if (company.postboxNumber) {
            addressParts.push('PO Box ' + company.postboxNumber);
            if (company.postboxZipCode) {
                addressParts.push([company.postboxZipCode.zip, company.postboxZipCode.city].join(' '));
            }
        } else {
            addressParts.push([company.street, company.houseNumber].join(' '));
            if (company.zipCode) {
                addressParts.push([company.zipCode.zip, company.zipCode.city].join(' '));
            }
        }
        const address = addressParts
            .filter((part) => !!part)
            .map((part) => part.replace(/^\s*$/, ''))
            .filter((part) => !!part)
            .join(', ');
        this.jobPublicationForm.get('application.paperApplicationAddress').setValue(address);
        return false;
    }

    private getSalutation(userData: UserData): string {
        if (!userData.gender) {
            return null;
        }

        return userData.gender === Gender.MALE ? Salutation[Salutation.MR] : Salutation[Salutation.MS];
    }

    ngOnDestroy(): void {
        this.unsubscribe$.next();
        this.unsubscribe$.complete();
    }

    getSwitzSelected(countryControl: AbstractControl): Observable<boolean> {
        return Observable.merge(
            Observable.of(countryControl.value),
            countryControl.valueChanges)
            .map((selectedCountry) => selectedCountry === this.SWITZ_KEY);
    }

    private configureDateInput(dateInputPath: string, radioButtonsPath: string, onChange: (disabled: boolean) => void) {
        const date = this.jobPublicationForm.get(dateInputPath);
        const radioButton = this.jobPublicationForm.get(radioButtonsPath);
        radioButton.valueChanges
            .takeUntil(this.unsubscribe$)
            .startWith(radioButton.value)
            .subscribe((value) => {
                if (!value) {
                    date.enable();
                    onChange(false);
                } else {
                    date.disable();
                    onChange(true);
                }
            });
    }

    private updateEmploymentStartDateRelatedField() {
        this.jobPublicationForm.get('employment.employmentStartDate.date').valueChanges
            .takeUntil(this.unsubscribe$)
            .subscribe((value) => {
                if (value) {
                    const employmentEndDateControl = this.jobPublicationForm.get('employment.employmentEndDate.date');
                    const employmentStartDate = DateUtils.mapNgbDateStructToDate(value);
                    const employmentEndDate = DateUtils.mapNgbDateStructToDate(
                        employmentEndDateControl.value ? employmentEndDateControl.value : this.employmentEndDateMin);

                    if (employmentStartDate > employmentEndDate) {
                        employmentEndDateControl.setValue(null);
                    }
                    this.employmentEndDateMin = DateUtils.mapDateToNgbDateStruct(employmentStartDate);
                }
            });
    }

    max(...values: number[]): number {
        return Math.max(...values);
    }

    getPoBoxZipCodeTranslations(): Translations {
        return {
            zipCode: 'home.tools.job-publication.company.postbox-zipcode',
            zip: 'home.tools.job-publication.company.postbox-zip',
            city: 'home.tools.job-publication.company.postbox-city'
        };
    }

    onSubmit(): void {
        this.resetAlerts();
        if (this.jobPublicationForm.valid) {
            this.jobPublicationForm.get('disclaimer').reset(false);
            const createJobAdvertisement = JobPublicationMapper.mapJobPublicationFormToCreateJobAdvertisement(this.jobPublicationForm.value);
            this.jobAdvertisementService.save(createJobAdvertisement)
                .subscribe(this.createSaveSubscriber());
        }
    }

    private resetAlerts() {
        this.showSuccessSaveMessage = false;
        this.showErrorSaveMessage = false;
    }

    private createSaveSubscriber() {
        return Subscriber.create(
            (resp: ResponseWrapper) => {
                const changeStatusCb = resp.status === 200
                    ? (show) => this.showSuccessSaveMessage = show
                    : (show) => this.showErrorSaveMessage = show;
                this.showAlert(changeStatusCb);
            },
            (_) => this.showAlert((show) => this.showErrorSaveMessage = show)
        );
    }

    private showAlert(changeAlertStatus: (show: boolean) => void): void {
        changeAlertStatus(true);
        this.cd.markForCheck();
        window.scroll(0, 0);
    }

    private setupCountries(): void {
        countries.registerLocale(require('i18n-iso-countries/langs/en.json'));
        countries.registerLocale(require('i18n-iso-countries/langs/fr.json'));
        countries.registerLocale(require('i18n-iso-countries/langs/de.json'));
        countries.registerLocale(require('i18n-iso-countries/langs/it.json'));

        this.countries$ = this.coreStore.select(getLanguage)
            .map((lang: string) => {
                const countryNames = countries.getNames(lang);
                return Object.keys(countryNames)
                    .map((key) => ({ key, value: countryNames[key] }));
            });
    }

    resetForm(): void {
        this.resetAlerts();
        this.jobPublicationForm.reset(this.createDefaultFormModel());
    }

    get jobOccupation(): AbstractControl {
        return this.jobPublicationForm.get('occupation.occupationSuggestion');
    }

    get employmentWorkForms(): FormArray {
        return <FormArray>this.jobPublicationForm.get('employment.workForms');
    }

    private updateOccupationOnLanguageChange() {
        this.translateService.onLangChange
            .takeUntil(this.unsubscribe$)
            .filter((_) => !!this.jobOccupation.value)
            .flatMap((e: LangChangeEvent) => {
                const occupation: OccupationOption = this.jobOccupation.value;
                return this.occupationPresentationService.findOccupationLabelsByCode(occupation.key, e.lang)
                    .map((label) => Object.assign({}, occupation, { label: label.default }));
            })
            .subscribe((occupation) =>
                this.jobOccupation.patchValue(occupation, { emitEvent: false }));
    }
}
