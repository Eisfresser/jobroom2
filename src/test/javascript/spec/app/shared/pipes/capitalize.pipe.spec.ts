import { CapitalizePipe } from '../../../../../../main/webapp/app/shared/pipes/capitalize.pipe';

describe('CapitalizePipe', () => {
    let pipe: CapitalizePipe;

    beforeEach(() => {
        pipe = new CapitalizePipe();
    });

    it('should capitalize first letter and keep other characters unchanged', () => {
        // GIVEN
        const originalString = 'controleur systeme des TIC';

        // WHEN
        const result = pipe.transform(originalString);

        // THEN
        expect(result).toEqual('Controleur systeme des TIC');
    });

    it('should return null when source is null', () => {
        // GIVEN
        const originalString = null;

        // WHEN
        const result = pipe.transform(originalString);

        // THEN
        expect(result).toEqual(originalString);
    });

    it('should return empty string when source is empty', () => {
        // GIVEN
        const originalString = '';

        // WHEN
        const result = pipe.transform(originalString);

        // THEN
        expect(result).toEqual(originalString);
    })
});
