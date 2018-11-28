import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs/Observable';
import { LanguageFilterService } from '../../../../shared/input-components/language-filter/language-filter.service';
import { JobDescription } from '../../../../shared/job-advertisement/job-advertisement.model';
import { Subscription } from 'rxjs/Subscription';

@Component({
    selector: 'jr2-job-description',
    templateUrl: './job-description.component.html',
    styleUrls: ['./job-description.component.scss']
})
export class JobDescriptionComponent implements OnInit, OnDestroy {
    readonly JOB_DESCRIPTION_MAX_LENGTH = 10000;
    readonly TITLE_MAX_LENGTH = 255;
    readonly LANGUAGE_MAX_LENGTH = 5;
    private readonly LANGUAGES = [
        'de',
        'fr',
        'it',
        'en'
    ];

    @Input() group: FormGroup;
    @Input() controlName: string;

    languageOptionTranslations$: Observable<Array<{ key: string, value: string }>>;

    private subscription: Subscription;

    constructor(private fb: FormBuilder,
                private translateService: TranslateService,
                private languageFilterService: LanguageFilterService) {
    }

    ngOnInit() {
        let value = [this.createEmptyGroup(this.translateService.currentLang)];
        if (this.group.get(this.controlName)
            && this.group.get(this.controlName).value
            && this.group.get(this.controlName).value.length) {
            value = this.group.get(this.controlName).value
                .map((val) => this.createGroup(val));
        }
        this.group.removeControl(this.controlName);
        this.group.addControl(this.controlName, new FormArray(value));

        this.registerRemovingEmptyItems();

        this.languageOptionTranslations$ = this.languageFilterService
            .getSorterLanguageTranslations(this.LANGUAGES);
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    /*
        It's a trick. FormArray.reset() does not care about array length, so when we reset to an empty array
        (e.g.: formArray.reset([]))
        it just resets all its values, but array length remains the same :(
     */
    private registerRemovingEmptyItems() {
        this.subscription = this.group.get(this.controlName).valueChanges
            .filter((change: any[]) => change && change.length > 1)
            .filter((change: any[]) => change.every((el) => !el.languageIsoCode))
            .subscribe((change: any[]) => {
                this.removeByIndex(change.length - 1);
            });
    }

    get jobDescriptions(): FormArray {
        return this.group.get(this.controlName) as FormArray;
    }

    get selectedDescriptions(): Array<JobDescription> {
        return this.jobDescriptions.value as Array<JobDescription>;
    }

    getLanguageOptionTranslation(currentValue: string,
                                 languageOptionTranslations: Array<{ key: string, value: string }>): Array<{ key: string, value: string }> {
        const languages = this.selectedDescriptions.map((description) => description.languageIsoCode);
        return languageOptionTranslations
            .filter((translation) => currentValue && currentValue === translation.key
                ? true
                : !languages.includes(translation.key));
    }

    removeByIndex(idx: number) {
        this.jobDescriptions.removeAt(idx);
    }

    max(...values: number[]): number {
        return Math.max(...values);
    }

    isAddEnabled(): boolean {
        return this.jobDescriptions.length !== this.LANGUAGES.length;
    }

    addNewDescription(languageOptionTranslations: Array<{ key: string, value: string }>) {
        const languages = this.selectedDescriptions.map((description) => description.languageIsoCode);
        const nextLanguage = languageOptionTranslations
            .find((translation) => !languages.includes(translation.key));
        this.jobDescriptions.push(this.createEmptyGroup(nextLanguage.key));
    }

    private createEmptyGroup(language: string): FormGroup {
        return this.createGroup({
            languageIsoCode: language,
            title: '',
            description: ''
        });
    }

    private createGroup(value): FormGroup {
        return this.fb.group({
            languageIsoCode: [value.languageIsoCode, [Validators.maxLength(this.LANGUAGE_MAX_LENGTH)]],
            title: [value.title, [ Validators.required, Validators.maxLength(this.TITLE_MAX_LENGTH)]],
            description: [value.description, [Validators.required, Validators.maxLength(this.JOB_DESCRIPTION_MAX_LENGTH)]]
        });
    }
}
