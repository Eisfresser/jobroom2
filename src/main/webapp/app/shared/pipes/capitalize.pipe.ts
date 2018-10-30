import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'jr2Capitalize'
})
export class CapitalizePipe implements PipeTransform {
    transform(input: string) {
        if (input !== null) {
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        }
        return input;
    }
}
