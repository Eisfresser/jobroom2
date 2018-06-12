import { Pipe, PipeTransform } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { mapLangToLocale } from './pipes-util';

@Pipe({
    name: 'jr2Number'
})
export class LocaleAwareDecimalPipe implements PipeTransform {

    constructor(private translateService: TranslateService) {
    }

    transform(value: any, digits?: string): string | null {
        if (this.translateService.currentLang === 'en') {
            const wrapped = new DecimalPipe(mapLangToLocale(this.translateService.currentLang));
            return wrapped.transform(value, digits);
        }

        return value
            ? value.toString()
                .replace(/(\d+?)(?=(\d{3})+(?!\d)|$)/g, (num) => num.length >= 2 ? `${num} ` : num)
                .trim()
            : null;
    }
}
