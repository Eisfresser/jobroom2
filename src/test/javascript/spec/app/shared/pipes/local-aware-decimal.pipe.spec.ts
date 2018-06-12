import { LocaleAwareDecimalPipe } from '../../../../../../main/webapp/app/shared/pipes/locale-aware-number.pipe';
import { TranslateService } from '@ngx-translate/core';

describe('LocaleAwareDecimalPipe', () => {
    let pipe: LocaleAwareDecimalPipe;
    let mockTranslateService;

    it('should group by 3 digits with "," separator for "en" locale', () => {
        // given
        mockTranslateService = {
            currentLang: 'en'
        } as TranslateService;
        pipe = new LocaleAwareDecimalPipe(mockTranslateService);

        // when
        const formattedNumber = pipe.transform(1234);

        // than
        expect(formattedNumber).toEqual('1,234');
    });

    describe('for "de" locale', () => {
        beforeAll(() => {
            mockTranslateService = {
                currentLang: 'de'
            } as TranslateService;
            pipe = new LocaleAwareDecimalPipe(mockTranslateService);
        });

        it('should return null for null value', () => {
            // when
            const formattedNumber = pipe.transform(null);

            // than
            expect(formattedNumber).toEqual(null);
        });

        it('should group by 3 digits with space separator', () => {
            // when
            const formattedNumber = pipe.transform(12345);

            // than
            expect(formattedNumber).toEqual('12 345');
        });

        it('should not group by 3 digits when group preceded by one digit', () => {
            // when
            const formattedNumber = pipe.transform(1234);

            // than
            expect(formattedNumber).toEqual('1234');
        });
    });
});
