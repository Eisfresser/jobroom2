import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'shorten'
})
export class ShortenPipe implements PipeTransform {
    transform(value: string, maxLength?: number): string {
        if (!maxLength || value.length <= maxLength) {
            return value;
        }

        return value.substr(0, maxLength) + '...';
    }
}
