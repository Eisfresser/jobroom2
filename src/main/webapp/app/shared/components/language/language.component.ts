import { Component, Input, OnInit } from '@angular/core';
import { LanguageSkill } from '../../job-advertisement/job-advertisement.model';

@Component({
    selector: 'jr2-languages',
    template: `
        <div *ngIf="languages?.length" [class]="containerClass">
            <h4 [class]="titleClass">{{ 'global.reference.languages' | translate }}</h4>

            <div [class]="itemClass" *ngFor="let language of languages">
                <strong>
                    {{ 'global.reference.language.' + language.languageIsoCode | lowercase | translate }}
                </strong>
                <span>
                    <ng-container *ngIf="language.spokenLevel">
                        {{ 'global.reference.language.spoken' | translate }}: {{ 'global.reference.language.level.' + language.spokenLevel | translate}}, </ng-container>
                    <ng-container *ngIf="language.writtenLevel">
                        {{ 'global.reference.language.written' | translate }}: {{ 'global.reference.language.level.' + language.writtenLevel | translate}}</ng-container>
                </span>
            </div>
        </div>
    `
})

export class LanguageComponent implements OnInit {
    @Input() languages: LanguageSkill[];
    @Input() containerClass = 'job-detail__content';
    @Input() titleClass = 'job-detail__content__title';
    @Input() itemClass = 'content__item';

    private static filterLanguagesWithInvalidCodes(languages: LanguageSkill[]): LanguageSkill[] {
        return languages
            .filter((language) => language.languageIsoCode !== '98' && language.languageIsoCode !== '99');
    }

    constructor() {
    }

    ngOnInit(): void {
        this.languages = LanguageComponent.filterLanguagesWithInvalidCodes(this.languages);
    }
}
