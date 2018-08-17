import { Pipe, PipeTransform } from '@angular/core';

const removeMd = require('remove-markdown');

@Pipe({
    name: 'jr2MarkdownEscape'
})
export class MarkdownEscapePipe implements PipeTransform {

    transform(value: any): any {
        if (value) {
            return removeMd(value)
        }
        return value;
    }

}
