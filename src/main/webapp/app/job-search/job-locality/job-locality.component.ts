import { Component, Input } from '@angular/core';
import { Location } from '../../shared/job-advertisement/job-advertisement.model';

@Component({
    selector: 'jr2-job-locality',
    template: `
        <span *ngIf="location" class="badge badge-blue">
            {{ location.postalCode }} {{ location.remarks }}
            <ng-container *ngIf="location.cantonCode || location.countryIsoCode">
                ({{ location.cantonCode || location.countryIsoCode }})
            </ng-container>
        </span>
    `
})

export class JobLocalityComponent {
    @Input()
    location: Location;
}
