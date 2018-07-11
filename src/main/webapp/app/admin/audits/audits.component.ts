import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';

import { Audit } from './audit.model';
import { AuditsService } from './audits.service';
import { ITEMS_PER_PAGE } from '../../shared';

@Component({
    selector: 'jhi-audit',
    templateUrl: './audits.component.html'
})
export class AuditsComponent implements OnInit {
    audits: Audit[];
    fromDate: string;
    itemsPerPage: any;
    page: number;
    toDate: string;
    totalItems: number;
    datePipe: DatePipe;
    sortByField = 'auditEventDate';
    ascending: false;

    constructor(
        private auditsService: AuditsService
    ) {
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.page = 1;
        this.datePipe = new DatePipe('en');
    }

    loadPage(page: number) {
        this.page = page;
        this.loadAudits();
    }

    ngOnInit() {
        this.initToDate();
        this.initFromDate();
        this.loadAudits();
    }

    initToDate() {
        const dateFormat = 'yyyy-MM-dd';
        // Today + 1 day - needed if the current day must be included
        const today: Date = new Date();
        today.setDate(today.getDate() + 1);
        const date = new Date(today.getFullYear(), today.getMonth(), today.getDate());
        this.toDate = this.datePipe.transform(date, dateFormat);
    }

    initFromDate() {
        const dateFormat = 'yyyy-MM-dd';
        let fromDate: Date = new Date();

        if (fromDate.getMonth() === 0) {
            fromDate = new Date(fromDate.getFullYear() - 1, 11, fromDate.getDate());
        } else {
            fromDate = new Date(fromDate.getFullYear(), fromDate.getMonth() - 1, fromDate.getDate());
        }

        this.fromDate = this.datePipe.transform(fromDate, dateFormat);
    }

    loadAudits() {
        this.auditsService.query({
            page: this.page - 1,
            size: this.itemsPerPage,
            fromDate: this.fromDate,
            toDate: this.toDate,
            sort: `${this.sortByField},${this.ascending ? 'asc' : 'desc'}`
        }).subscribe((res) => {
            this.audits = res.body;
            this.totalItems = +res.headers.get('X-Total-Count');
        });
    }
}
