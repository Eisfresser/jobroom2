import { MarkdownEscapePipe } from '../../../../../../main/webapp/app/shared/pipes/markdown-escape.pipe';

describe('MarkdownEscapePipe', () => {
    let pipe: MarkdownEscapePipe;

    beforeEach(() => {
        pipe = new MarkdownEscapePipe();
    });

    it('should remove heading', () => {
        expect(pipe.transform('## Heading')).toEqual('Heading');
        expect(pipe.transform('### Heading')).toEqual('Heading');
        expect(pipe.transform('#### Heading')).toEqual('Heading');
        expect(pipe.transform('##### Heading')).toEqual('Heading');
        expect(pipe.transform('###### Heading')).toEqual('Heading');
    });

    it('should remove horizontal rules', () => {
        expect(pipe.transform('___')).toEqual('');
        expect(pipe.transform('---')).toEqual('');
        expect(pipe.transform('***')).toEqual('');
    });

    it('should remove blockquotes', () => {
        expect(pipe.transform('> Blockquotes')).toEqual('Blockquotes')
    });

    it('should remove text styles', () => {
        expect(pipe.transform('**This is bold text**')).toEqual('This is bold text');
        expect(pipe.transform('__This is bold text__')).toEqual('This is bold text');

        expect(pipe.transform('*This is italic text*')).toEqual('This is italic text');
        expect(pipe.transform('_This is italic text_')).toEqual('This is italic text');

        expect(pipe.transform('~~Strikethrough~~')).toEqual('Strikethrough');
    });

    describe('should remove lists', () => {
        it('ordered', () => {
            expect(pipe.transform('1. Lorem ipsum dolor sit amet\n2. Consectetur adipiscing elit'))
                .toEqual('Lorem ipsum dolor sit amet\nConsectetur adipiscing elit')
        });

        it('unordered', () => {
            expect(pipe.transform('+ First line\n- Second Line\n* Third line'))
                .toEqual('First line\nSecond Line\nThird line');
        });
    });

    describe('should remove code', () => {
        it('inline', () => {
            expect(pipe.transform('Inline `code`')).toEqual('Inline code');
        });

        it('code `fences`', () => {
            expect(pipe.transform('```Sample code```')).toEqual('Sample code')
        })
    });

    it('should remove links', () => {
        expect(pipe.transform('[github](http://github.com)')).toEqual('github');
    });

    it('should remove images', () => {
        expect(pipe.transform('![Logo](https://google.com/images/log.png)')).toEqual('');
    });

    it('should preserve HTML tags', () => {
        const transformedText = pipe.transform('Hello <em>World</em>');
        expect(transformedText).toEqual('Hello <em>World</em>');
    });
});
