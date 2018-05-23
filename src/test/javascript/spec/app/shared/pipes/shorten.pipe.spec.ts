import { ShortenPipe } from '../../../../../../main/webapp/app/shared/pipes/shorten.pipe';

describe('ShortenPipe', () => {
    let pipe: ShortenPipe;

    beforeEach(() => {
        pipe = new ShortenPipe();
    });

    it('should return original string', () => {
        // GIVEN
        const originalString = '1234';

        // WHEN
        const result = pipe.transform(originalString);

        // THEN
        expect(result).toEqual(originalString);
    });

    it('should not shorten short string', () => {
        // GIVEN
        const originalString = '1234';

        // WHEN
        const result = pipe.transform(originalString, 5);

        // THEN
        expect(result).toEqual(originalString);
    });

    it('should not shorten string of maxLength', () => {
        // GIVEN
        const originalString = '1234';

        // WHEN
        const result = pipe.transform(originalString, 4);

        // THEN
        expect(result).toEqual(originalString);
    });

    it('should shorten long string', () => {
        // GIVEN
        const originalString = '12345';

        // WHEN
        const result = pipe.transform(originalString, 4);

        // THEN
        expect(result).toEqual('1234...');
    });

});
