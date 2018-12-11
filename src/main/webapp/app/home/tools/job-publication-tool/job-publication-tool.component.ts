import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    ElementRef,
    Input,
    OnDestroy,
    OnInit,
    ViewChild
} from '@angular/core';
import { Observable } from 'rxjs/Observable';
import {
    AbstractControl,
    FormArray,
    FormBuilder,
    FormGroup,
    ValidationErrors,
    ValidatorFn,
    Validators
} from '@angular/forms';
import { Subject } from 'rxjs/Subject';
import {
    DateUtils,
    Degree,
    EMAIL_REGEX,
    ONE_TWO_DIGIT_INTEGER_REGEX,
    POSTBOX_NUMBER_REGEX,
    ResponseWrapper,
    URL_REGEX,
    WorkForm
} from '../../../shared';
import { LanguageSkillService } from '../../../candidate-search/services/language-skill.service';
import {
    FormatterFn,
    OccupationOption,
    OccupationPresentationService,
    SuggestionLoaderFn
} from '../../../shared/reference-service';
import { Translations, ZipCodeComponent } from './zip-code/zip-code.component';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import * as countries from 'i18n-iso-countries';
import { Subscriber } from 'rxjs/Subscriber';
import { JobPublicationMapper } from './job-publication-mapper';
import { JobAdvertisementService } from '../../../shared/job-advertisement/job-advertisement.service';
import {
    JobAdvertisement,
    Salutation,
    WorkExperience
} from '../../../shared/job-advertisement/job-advertisement.model';
import { CompanyFormModel, JobPublicationForm } from './job-publication-form.model';
import { LanguageFilterService } from '../../../shared/input-components/language-filter/language-filter.service';
import { Store } from '@ngrx/store';
import {
    CoreState,
    getLanguage
} from '../../../shared/state-management/state/core.state';
import { CurrentSelectedCompanyService } from '../../../shared/company/current-selected-company.service';

@Component({
    selector: 'jr2-job-publication-tool',
    templateUrl: './job-publication-tool.component.html',
    styleUrls: ['./job-publication-tool.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class JobPublicationToolComponent implements OnInit, OnDestroy {
    readonly APPLICATION_ELECTRONIC_EMAIL_MAX_LENGTH = 50;
    readonly APPLICATION_ELECTRONIC_URL_MAX_LENGTH = 247;
    readonly APPLICATION_PAPER_COUNTRY_ISO_CODE_MAX_LENGTH = 2;

    readonly APPLICATION_PAPER_APPLICATION_CITY_MAX_LENGTH = 100;
    readonly APPLICATION_PAPER_APPLICATION_ZIP_MAX_LENGTH = 10;
    readonly APPLICATION_PAPER_APPLICATION_HOUSE_NO_MAX_LENGTH = 10;
    readonly APPLICATION_PAPER_APPLICATION_STREET_MAX_LENGTH = 60;
    readonly APPLICATION_PAPER_APPLICATION_COMPANY_NAME_MAX_LENGTH = 255;

    readonly PUBLIC_CONTACT_MAIL_MAX_LENGTH = 50;
    readonly PUBLIC_CONTACT_LAST_NAME_MAX_LENGTH = 50;
    readonly PUBLIC_CONTACT_FIRST_NAME_MAX_LENGTH = 50;
    readonly PUBLIC_CONTACT_SALUTATION_MAX_LENGTH = 3;

    readonly CONTACT_EMAIL_MAX_LENGTH = 50;
    readonly CONTACT_LAST_NAME_MAX_LENGTH = 50;
    readonly CONTACT_FIRST_NAME_MAX_LENGTH = 50;
    readonly CONTACT_SALUTATION_MAX_LENGTH = 3;

    readonly COMPANY_STREET_MAX_LENGTH = 60;
    readonly COMPANY_COUNTRY_CODE_MAX_LENGTH = 2;
    readonly COMPANY_ZIP_CODE_MAX_LENGTH = 10;
    readonly COMPANY_HOUSE_NO_MAX_LENGTH = 10;
    readonly COMPANY_NAME_MAX_LENGTH = 255;
    readonly LOCATION_ADDITIONAL_DETAILS_MAX_LENGTH = 50;
    readonly EXPERIENCE_MAX_LENGTH = 64;

    readonly APPLICATION_ADDITIONAL_INFO_MAX_LENGTH = 255;
    readonly CONTACT_LANGUAGES = ['de', 'fr', 'it', 'en'];

    private readonly COUNTRY_ISO_CODE_SWITZERLAND = 'CH';

    @Input()
    jobAdvertisement: JobAdvertisement;

    @ViewChild('employmentStartDateEl')
    employmentStartDateElementRef: ElementRef;

    @ViewChild('employmentEndDateEl')
    employmentEndDateElementRef: ElementRef;

    @ViewChild('applicationPostAddressZip')
    applicationPostAddressZipElementRef: ZipCodeComponent;

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
    disableSubmit = false;
    private unsubscribe$ = new Subject<void>();

    constructor(private coreStore: Store<CoreState>,
                private occupationPresentationService: OccupationPresentationService,
                private fb: FormBuilder,
                private languageSkillService: LanguageSkillService,
                private translateService: TranslateService,
                private jobAdvertisementService: JobAdvertisementService,
                private languageFilterService: LanguageFilterService,
                private currentSelectedCompanyService: CurrentSelectedCompanyService,
                private cd: ChangeDetectorRef) {
    }

    get jobOccupation(): AbstractControl {
        return this.jobPublicationForm.get('occupation.occupationSuggestion');
    }

    get employmentWorkForms(): FormArray {
        return <FormArray>this.jobPublicationForm.get('employment.workForms');
    }

    ngOnInit(): void {
        this.fetchOccupationSuggestions = this.occupationPresentationService.fetchJobPublicationOccupationSuggestions;
        this.occupationFormatter = this.occupationPresentationService.occupationFormatter;
        this.updateOccupationOnLanguageChange();
        this.languageOptionTranslations$ = this.languageFilterService.getSorterLanguageTranslations(this.CONTACT_LANGUAGES);

        this.languageSkills$ = this.languageSkillService.getLanguages();
        this.setupCountries();

        let defaultFormValues: JobPublicationForm = this.createDefaultFormModel();
        if (this.jobAdvertisement) {
            defaultFormValues = JobPublicationMapper.mapJobPublicationToFormModel(defaultFormValues, this.jobAdvertisement);
        }

        this.jobPublicationForm = this.createJobPublicationForm(defaultFormValues);
        this.configureEmployerSection(defaultFormValues);
        this.configureApplicationSection(defaultFormValues);
        this.configureEmploymentSection();

        this.currentSelectedCompanyService.getSelectedCompanyContactTemplate()
            .subscribe((companyContactTemplateModel) => {
                if (companyContactTemplateModel == null) {
                    return;
                }
                this.jobPublicationForm.patchValue({
                    company: {
                        name: companyContactTemplateModel.companyName,
                        street: companyContactTemplateModel.companyStreet,
                        zipCode: {
                            zip: companyContactTemplateModel.companyZipCode,
                            city: companyContactTemplateModel.companyCity
                        },
                        houseNumber: companyContactTemplateModel.companyHouseNr
                    },
                    contact: {
                        salutation: companyContactTemplateModel.salutation,
                        email: companyContactTemplateModel.email,
                        phoneNumber: companyContactTemplateModel.phone,
                        firstName: companyContactTemplateModel.firstName,
                        lastName: companyContactTemplateModel.lastName
                    }
                })
            });
    }

    isEmployerEnabled(): boolean {
        return this.jobPublicationForm.get('company.surrogate').value;
    }

    copyFromContact() {
        const contact = this.jobPublicationForm.get('contact').value;
        this.jobPublicationForm.get('publicContact').patchValue(contact);
        return false;
    }

    copyPhoneNumberFromPublicContact() {
        const publicContactPhoneNumber = this.jobPublicationForm.get('publicContact.phoneNumber').value;
        this.jobPublicationForm.get('application.phoneNumber').setValue(publicContactPhoneNumber);
        return false;
    }

    copyEmailFromPublicContact() {
        const publicContactEmail = this.jobPublicationForm.get('publicContact.email').value;
        this.jobPublicationForm.get('application.electronicApplicationEmail').setValue(publicContactEmail);
        return false;
    }

    copyAddressFromCompany() {
        const company: CompanyFormModel = this.jobPublicationForm.get('company').value;
        const oldCountryCode = this.jobPublicationForm.get('application.postAddress.paperAppCountryCode').value;
        const newCountryCode = company.countryCode;
        const isSwitzerlandSelected = (newCountryCode === this.COUNTRY_ISO_CODE_SWITZERLAND);

        this.jobPublicationForm.get('application').patchValue({
            postAddress: {
                paperAppCompanyName: company.name,
                paperAppStreet: company.street,
                paperAppHouseNr: company.houseNumber,
                paperAppZip: this.copyAddressFromCompanyGetZip(),
                paperAppPostboxNr: company.postboxNumber,
                paperAppCountryCode: newCountryCode,
            }
        });

        if (oldCountryCode === newCountryCode) {
            if (isSwitzerlandSelected) {
                this.applicationPostAddressZipElementRef.zipAutocompleter.setValue(this.copyAddressFromCompanyGetZip());
            } else {
                this.applicationPostAddressZipElementRef.zipGroup.setValue(this.copyAddressFromCompanyGetZip());
            }
        } else {
            if (isSwitzerlandSelected) {
                this.applicationPostAddressZipElementRef.zipAutocompleter.valueChanges.take(1).subscribe(() => {
                    this.applicationPostAddressZipElementRef.zipAutocompleter.setValue(this.copyAddressFromCompanyGetZip());
                });
            } else {
                this.applicationPostAddressZipElementRef.zipGroup.valueChanges.take(1).subscribe(() => {
                    this.applicationPostAddressZipElementRef.zipGroup.setValue(this.copyAddressFromCompanyGetZip());
                });
            }
        }
        return false;
    }

    copyAddressFromCompanyGetZip() {
        const company: CompanyFormModel = this.jobPublicationForm.get('company').value;
        return {
            zip: company.zipCode.zip,
            city: company.zipCode.city,
        }
    }

    ngOnDestroy(): void {
        this.unsubscribe$.next();
        this.unsubscribe$.complete();
    }

    isSwitzerlandSelected(countryControl: AbstractControl): Observable<boolean> {
        return Observable.merge(
            Observable.of(countryControl.value),
            countryControl.valueChanges)
            .map((selectedCountry) => selectedCountry === this.COUNTRY_ISO_CODE_SWITZERLAND);
    }

    max(...values: number[]): number {
        return Math.max(...values);
    }

    onSubmit(): void {
        this.resetAlerts();
        if (this.jobPublicationForm.valid) {
            this.disableSubmit = true;
            const createJobAdvertisement = JobPublicationMapper.mapJobPublicationFormToCreateJobAdvertisement(this.jobPublicationForm.value);
            this.jobAdvertisementService.save(createJobAdvertisement)
                .finally(() => this.disableSubmit = false)
                .subscribe(this.createSaveSubscriber());
        }
    }

    resetForm(): void {
        this.resetAlerts();
        this.jobPublicationForm.reset(this.createDefaultFormModel());
    }

    private configureEmployerSection(formModel: JobPublicationForm) {
        this.jobPublicationForm.addControl('employer',
            this.fb.group({
                name: [formModel.employer.name, Validators.required,
                    Validators.maxLength(this.APPLICATION_PAPER_APPLICATION_COMPANY_NAME_MAX_LENGTH)],
                zipCode: [formModel.employer.zipCode],
                countryCode: [formModel.employer.countryCode, Validators.required],
            }));

        const companySurrogate = this.jobPublicationForm.get('company.surrogate');
        this.addSectionEnableListener(companySurrogate, 'employer');
    }

    private configureApplicationSection(formModel: JobPublicationForm) {
        this.jobPublicationForm.addControl('application',
            this.fb.group({
                // paperApplicationAddress: [formModel.application.paperApplicationAddress],
                selectElectronicApplicationUrl: formModel.application.selectElectronicApplicationUrl,
                selectElectronicApplicationEmail: formModel.application.selectElectronicApplicationEmail,
                selectPhoneNumber: formModel.application.selectPhoneNumber,
                selectPaperApp: formModel.application.selectPaperApp,
                postAddress: this.fb.group({
                    paperAppCompanyName: [formModel.application.postAddress.paperAppCompanyName, [
                        Validators.required,
                        Validators.maxLength(this.APPLICATION_PAPER_APPLICATION_COMPANY_NAME_MAX_LENGTH)]],
                    paperAppStreet: [formModel.application.postAddress.paperAppStreet, [
                        Validators.maxLength(this.APPLICATION_PAPER_APPLICATION_STREET_MAX_LENGTH)]],
                    paperAppHouseNr: [formModel.application.postAddress.paperAppHouseNr, [
                        Validators.maxLength(this.APPLICATION_PAPER_APPLICATION_HOUSE_NO_MAX_LENGTH)]],
                    paperAppPostboxNr: [formModel.application.postAddress.paperAppPostboxNr,
                        Validators.pattern(POSTBOX_NUMBER_REGEX)],
                    paperAppZip: this.fb.group({
                        zip: [formModel.application.postAddress.paperAppZip.zip, [
                            Validators.maxLength(this.APPLICATION_PAPER_APPLICATION_ZIP_MAX_LENGTH)]],
                        city: [formModel.application.postAddress.paperAppZip.city, [
                            Validators.maxLength(this.APPLICATION_PAPER_APPLICATION_CITY_MAX_LENGTH)]],
                    }),
                    paperAppCountryCode: [formModel.application.postAddress.paperAppCountryCode, [
                        Validators.maxLength(this.APPLICATION_PAPER_COUNTRY_ISO_CODE_MAX_LENGTH)]],
                }, {
                    validator: this.atLeastOneRequiredValidator(Validators.required,
                        ['paperAppStreet', 'paperAppPostboxNr'])
                }),
                electronicApplicationEmail: [formModel.application.electronicApplicationEmail, [
                    Validators.required,
                    Validators.pattern(EMAIL_REGEX),
                    Validators.maxLength(this.APPLICATION_ELECTRONIC_EMAIL_MAX_LENGTH)
                ]],
                electronicApplicationUrl: [formModel.application.electronicApplicationUrl, [
                    Validators.required,
                    Validators.pattern(URL_REGEX),
                    Validators.maxLength(this.APPLICATION_ELECTRONIC_URL_MAX_LENGTH)
                ]],
                phoneNumber: [formModel.application.phoneNumber, [Validators.required]],
                additionalInfo: [formModel.application.additionalInfo,
                    [Validators.maxLength(this.APPLICATION_ADDITIONAL_INFO_MAX_LENGTH)]],
            }, {
                validator: this.atLeastOneRequiredValidator(this.checkBoxSelectedValidator(),
                    ['selectElectronicApplicationUrl', 'selectElectronicApplicationEmail', 'selectPhoneNumber', 'selectPaperApp'])
            }));

        this.addSectionEnableListener(this.jobPublicationForm.get('application.selectElectronicApplicationUrl'), 'application.electronicApplicationUrl');
        this.addSectionEnableListener(this.jobPublicationForm.get('application.selectElectronicApplicationEmail'), 'application.electronicApplicationEmail');
        this.addSectionEnableListener(this.jobPublicationForm.get('application.selectPhoneNumber'), 'application.phoneNumber');
        this.addSectionEnableListener(this.jobPublicationForm.get('application.selectPaperApp'), 'application.postAddress');
    }

    private addSectionEnableListener(controlToObserve, sectionToApply) {
        controlToObserve.valueChanges
            .takeUntil(this.unsubscribe$)
            .startWith(controlToObserve.value)
            .subscribe((value) => {
                if (value) {
                    this.jobPublicationForm.get(sectionToApply).enable();
                } else {
                    this.jobPublicationForm.get(sectionToApply).disable();
                }
            });
    }

    private configureEmploymentSection() {
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
        this.jobPublicationForm.get('employment.shortEmployment').valueChanges
            .takeUntil(this.unsubscribe$)
            .subscribe((isShortEmployment: boolean) => {
                this.jobPublicationForm.get('employment.employmentEndDate.permanent').setValue(!isShortEmployment);

                const employmentEndDate = this.jobPublicationForm.get('employment.employmentEndDate.date');
                if (isShortEmployment) {
                    employmentEndDate.clearValidators();
                } else {
                    employmentEndDate.setValidators([Validators.required])
                }
                employmentEndDate.setValue(null, { emitEvent: false });
                employmentEndDate.updateValueAndValidity({ emitEvent: false });
            });
    }

    private atLeastOneRequiredValidator(validator: ValidatorFn, includedControls: string[]): ValidatorFn {
        return (group: FormGroup): ValidationErrors | null => {
            const hasAtLeastOne = group && group.controls && Object.keys(group.controls)
                .filter((controlName) => includedControls.includes(controlName))
                .some((controlName) => {
                    const control = group.controls[controlName];
                    return control.valid && !validator(control)
                });

            return hasAtLeastOne ? null : {
                atLeastOneRequired: true,
            };
        };
    }

    private checkBoxSelectedValidator(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            const isControlValid = control.value;

            return isControlValid ? null : {
                checkBoxNotSelected: true,
            };
        };
    }

    private createJobPublicationForm(formModel: JobPublicationForm): FormGroup {

        return this.fb.group({
            numberOfJobs: [formModel.numberOfJobs, [Validators.required, Validators.min(1), Validators.max(99), Validators.pattern(ONE_TWO_DIGIT_INTEGER_REGEX)]],
            jobDescriptions: [formModel.jobDescriptions],
            occupation: this.fb.group({
                occupationSuggestion: [formModel.occupation.occupationSuggestion, Validators.required],
                degree: [formModel.occupation.degree],
                experience: [formModel.occupation.experience, [Validators.maxLength(this.EXPERIENCE_MAX_LENGTH)]]
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
                zipCode:  this.fb.group({
                    zip: [formModel.location.zipCode.zip],
                    city: [formModel.location.zipCode.city, [Validators.maxLength(this.COMPANY_NAME_MAX_LENGTH)]]
                }),
                additionalDetails: [formModel.location.additionalDetails, [Validators.maxLength(this.LOCATION_ADDITIONAL_DETAILS_MAX_LENGTH)]]
            }),
            company: this.fb.group({
                name: [formModel.company.name, [Validators.required, Validators.maxLength(this.COMPANY_NAME_MAX_LENGTH)]],
                street: [formModel.company.street, [Validators.maxLength(this.COMPANY_STREET_MAX_LENGTH)]],
                houseNumber: [formModel.company.houseNumber, [Validators.maxLength(this.COMPANY_HOUSE_NO_MAX_LENGTH)]],
                zipCode: [formModel.company.zipCode, [Validators.maxLength(this.COMPANY_ZIP_CODE_MAX_LENGTH)]],
                postboxNumber: [formModel.company.postboxNumber, [Validators.pattern(POSTBOX_NUMBER_REGEX)]],
                countryCode: [formModel.company.countryCode, [Validators.required, Validators.maxLength(this.COMPANY_COUNTRY_CODE_MAX_LENGTH)]],
                surrogate: [formModel.company.surrogate]
            }, { validator: this.atLeastOneRequiredValidator(Validators.required, ['street', 'postboxNumber']) }),
            contact: this.fb.group({
                language: [formModel.contact.language, [Validators.required]],
                salutation: [formModel.contact.salutation, [Validators.required, Validators.maxLength(this.CONTACT_SALUTATION_MAX_LENGTH)]],
                firstName: [formModel.contact.firstName, [Validators.required, Validators.maxLength(this.CONTACT_FIRST_NAME_MAX_LENGTH)]],
                lastName: [formModel.contact.lastName, [Validators.required, Validators.maxLength(this.CONTACT_LAST_NAME_MAX_LENGTH)]],
                phoneNumber: [formModel.contact.phoneNumber, [Validators.required]],
                email: [formModel.contact.email, [Validators.required, Validators.pattern(EMAIL_REGEX), Validators.maxLength(this.CONTACT_EMAIL_MAX_LENGTH)]],
            }),
            publicContact: this.fb.group({
                salutation: [formModel.publicContact.salutation, [Validators.required, Validators.maxLength(this.PUBLIC_CONTACT_SALUTATION_MAX_LENGTH)]],
                firstName: [formModel.publicContact.firstName, [Validators.required, Validators.maxLength(this.PUBLIC_CONTACT_FIRST_NAME_MAX_LENGTH)]],
                lastName: [formModel.publicContact.lastName, [Validators.required, Validators.maxLength(this.PUBLIC_CONTACT_LAST_NAME_MAX_LENGTH)]],

                phoneNumber: [formModel.publicContact.phoneNumber],
                email: [formModel.publicContact.email, [
                    Validators.pattern(EMAIL_REGEX),
                    Validators.maxLength(this.PUBLIC_CONTACT_MAIL_MAX_LENGTH)]],
            }, { validator: this.atLeastOneRequiredValidator(Validators.required, ['phoneNumber', 'email']) } ),
            publication: this.fb.group({
                publicDisplay: [formModel.publication.publicDisplay],
                eures: [formModel.publication.eures],
            }),
            disclaimer: this.fb.control(false, [Validators.requiredTrue])
        });
    }

    private createDefaultFormModel(): JobPublicationForm {
        return {
            jobDescriptions: [],
            numberOfJobs: '1',
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
                countryCode: this.COUNTRY_ISO_CODE_SWITZERLAND,
                additionalDetails: '',
                zipCode: {
                    zip: '',
                    city: ''
                }
            },
            company: {
                name: null,
                street: null,
                zipCode: {
                    zip: null,
                    city: null,
                },
                houseNumber: '',
                postboxNumber: '',
                countryCode: this.COUNTRY_ISO_CODE_SWITZERLAND,
                surrogate: false
            },
            employer: {
                name: '',
                zipCode: {
                    zip: '',
                    city: ''
                },
                countryCode: this.COUNTRY_ISO_CODE_SWITZERLAND
            },
            contact: {
                language: this.translateService.currentLang,
                salutation: null,
                firstName: '',
                lastName: '',
                phoneNumber: '',
                email: ''
            },
            publicContact: {
                salutation: null,
                firstName: '',
                lastName: '',
                phoneNumber: '',
                email: ''
            },
            application: {
                selectElectronicApplicationUrl: false,
                selectElectronicApplicationEmail: false,
                selectPhoneNumber: false,
                selectPaperApp: false,
                postAddress: {
                    paperAppCompanyName: '',
                    paperAppStreet: '',
                    paperAppHouseNr: '',
                    paperAppPostboxNr: '',
                    paperAppZip: {
                        zip: '',
                        city: '',
                    },
                    paperAppCountryCode: this.COUNTRY_ISO_CODE_SWITZERLAND
                },
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

    private resetAlerts() {
        this.showSuccessSaveMessage = false;
        this.showErrorSaveMessage = false;
    }

    private createSaveSubscriber() {
        return Subscriber.create(
            (resp: ResponseWrapper) => {
                this.jobPublicationForm.get('disclaimer').reset(false);
                this.jobPublicationForm.reset(this.createDefaultFormModel());
                this.showAlert((show) => this.showSuccessSaveMessage = show);
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
                    .map((key) => ({ key, value: countryNames[key] } ));
            });
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
