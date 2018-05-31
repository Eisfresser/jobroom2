import { AfterViewInit, Component, HostListener, Input, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import {
    getJobListScrollY,
    getSearchError,
    HideJobListErrorAction,
    JobSearchState,
    LoadNextPageAction,
    SaveScrollYAction
} from '../state-management';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';
import {
    JobAdvertisement,
    JobDescription
} from '../../shared/job-advertisement/job-advertisement.model';
import { TranslateService } from '@ngx-translate/core';
import { LocaleAwareDecimalPipe } from '../../shared/pipes/locale-aware-number.pipe';

@Component({
    selector: 'jr2-job-search-list',
    templateUrl: './job-search-list.component.html',
    styles: []
})
export class JobSearchListComponent implements OnDestroy, AfterViewInit {
    @Input() jobList: Array<JobAdvertisement>;
    @Input() jobDescriptions: Array<JobDescription>;
    @Input() totalCount: number;
    @Input() baseQueryString: string;
    @Input() localityQueryString: string;

    displayError$: Observable<boolean>;

    private subscription: Subscription;
    private scrollY = 0;

    @HostListener('window:scroll')
    private saveScrollY() {
        this.scrollY = window.scrollY;
    }

    constructor(private store: Store<JobSearchState>,
                private translateService: TranslateService,
                private localeAwareDecimalPipe: LocaleAwareDecimalPipe) {
        this.displayError$ = store.select(getSearchError);
        this.subscription = this.store
            .select(getJobListScrollY)
            .subscribe((scrollY: number) => {
                this.scrollY = scrollY;
            });
    }

    ngAfterViewInit(): void {
        window.scroll(0, this.scrollY);
    }

    ngOnDestroy(): void {
        this.store.dispatch(new SaveScrollYAction(this.scrollY));

        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

    closeAlert() {
        this.store.dispatch(new HideJobListErrorAction());
    }

    onScroll(event: any) {
        this.store.dispatch(new LoadNextPageAction());
    }

    getTitle() {
        return this.translateService.stream(this.getTitleKey(), {
            count: this.localeAwareDecimalPipe.transform(this.getMaxCount()),
            query: this.baseQueryString,
            locality: this.localityQueryString
        });
    }

    private getTitleKey() {
        let key = 'job-search.job-search-list.title';

        if (this.totalCount === 0) {
            key += '.none';
        } else if (this.totalCount === 1) {
            key += '.one';
        } else {
            key += '.many';
        }

        if (this.baseQueryString && this.baseQueryString.length > 0) {
            key += '.with-query';
        }

        if (this.localityQueryString && this.localityQueryString.length > 0) {
            key += '.with-locality';
        }

        return key;
    }

    getMaxCount() {
        return this.totalCount;
    }
}
