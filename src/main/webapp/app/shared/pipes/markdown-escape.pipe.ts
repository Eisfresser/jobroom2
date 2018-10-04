import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'jr2MarkdownEscape'
})
export class MarkdownEscapePipe implements PipeTransform {

    transform(value: any): any {
        if (value) {
            return this.escapeMarkdown(value)
        }
        return value;
    }

    escapeMarkdown(value: any) {
        try {
            return value
                // Remove horizontal rules
                .replace(/^(-\s*?|\*\s*?|_\s*?){3,}\s*$/gm, '')
                // Strip list leaders
                .replace(/^([\s\t]*)([\*\-\+]|\d+\.)\s+/gm, '$1')
                // Header
                .replace(/\n={2,}/g, '\n')
                // Fenced codeblocks
                .replace(/~{3}.*\n/g, '')
                // Strikethrough
                .replace(/~~/g, '')
                // Fenced codeblocks
                .replace(/`{3}.*\n/g, '')
                // Remove setext-style headers
                .replace(/^[=\-]{2,}\s*$/g, '')
                // Remove footnotes?
                .replace(/\[\^.+?\](\: .*?$)?/g, '')
                .replace(/\s{0,2}\[.*?\]: .*?$/g, '')
                // Remove images
                .replace(/\!\[(.*?)\][\[\(].*?[\]\)]/g, '')
                // Remove inline links
                .replace(/\[(.*?)\][\[\(].*?[\]\)]/g, '$1')
                // Remove blockquotes
                .replace(/^\s{0,3}>\s?/g, '')
                // Remove reference-style links?
                .replace(/^\s{1,2}\[(.*?)\]: (\S+)( ".*?")?\s*$/g, '')
                // Remove atx-style headers
                .replace(/^(\n)?\s{0,}#{1,6}\s+| {0,}(\n)?\s{0,}#{0,} {0,}(\n)?\s{0,}$/gm, '$1$2$3')
                // Remove emphasis (repeat the line to remove double emphasis)
                .replace(/([\*_]{1,3})(\S.*?\S{0,1})\1/g, '$2')
                .replace(/([\*_]{1,3})(\S.*?\S{0,1})\1/g, '$2')
                // Remove code blocks
                .replace(/(`{3,})(.*?)\1/gm, '$2')
                // Remove inline code
                .replace(/`(.+?)`/g, '$1')
                // Replace two or more newlines with exactly two? Not entirely sure this belongs here...
                .replace(/\n{2,}/g, '\n\n');
        } catch (e) {
            return value;
        }
    }

}
