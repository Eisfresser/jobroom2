import { Injectable } from '@angular/core';
import { Actions, Effect } from '@ngrx/effects';
import { Observable } from 'rxjs/Observable';
import { Action, Store } from '@ngrx/store';
import { ROUTER_NAVIGATION, RouterNavigationAction } from '@ngrx/router-store';
import { SelectAgencyTabAction, SelectCompanyTabAction, SelectToolbarItemAction } from '../actions/layout.actions';
import { AgenciesTab, CompaniesTab, HomeLayoutState, ToolbarItem } from '../state/layout.state';
import { HomeState } from '../state/home.state';
import { getLayoutState } from '..';
import { Router } from '@angular/router';

@Injectable()
export class HomeRouterEffects {

    private readonly JOB_SEEKERS_PATH = '/jobseekers';
    private readonly COMPANIES_JOB_PUBLICATION_PATH = '/companies/jobpublication';
    private readonly COMPANIES_CANDIDATES_PATH = '/companies/candidates';
    private readonly AGENTS_CANDIDATES_PATH = '/agents/candidates';
    private readonly AGENTS_JOB_PUBLICATION_PATH = '/agents/jobpublication';

    @Effect({ dispatch: false })
    selectRoute$: Observable<Action> = this.actions$
        .ofType(ROUTER_NAVIGATION)
        .do((action: RouterNavigationAction) => {
            const url = action.payload.event.url;
            if (url.includes(this.JOB_SEEKERS_PATH)) {
                this.store.dispatch(new SelectToolbarItemAction(ToolbarItem.JOB_SEEKERS));
            } else if (url.includes(this.COMPANIES_JOB_PUBLICATION_PATH)) {
                this.store.dispatch(new SelectToolbarItemAction(ToolbarItem.COMPANIES));
                this.store.dispatch(new SelectCompanyTabAction(CompaniesTab.JOB_PUBLICATION));
            } else if (url.includes(this.COMPANIES_CANDIDATES_PATH)) {
                this.store.dispatch(new SelectToolbarItemAction(ToolbarItem.COMPANIES));
                this.store.dispatch(new SelectCompanyTabAction(CompaniesTab.CANDIDATE_SEARCH));
            } else if (url.includes(this.AGENTS_CANDIDATES_PATH)) {
                this.store.dispatch(new SelectToolbarItemAction(ToolbarItem.RECRUITMENT_AGENCIES));
                this.store.dispatch(new SelectAgencyTabAction(AgenciesTab.CANDIDATE_SEARCH));
            } else if (url.includes(this.AGENTS_JOB_PUBLICATION_PATH)) {
                this.store.dispatch(new SelectToolbarItemAction(ToolbarItem.RECRUITMENT_AGENCIES));
                this.store.dispatch(new SelectAgencyTabAction(AgenciesTab.JOB_PUBLICATION));
            }
        });

    @Effect({ dispatch: false })
    redirectHomeRoute$: Observable<Action> = this.actions$
        .ofType(ROUTER_NAVIGATION)
        .withLatestFrom(this.store.select(getLayoutState))
        .map(([action, state]: [RouterNavigationAction, HomeLayoutState]) => {
            if (!action.payload.event.url.includes('/home')) {
               return action;
            }

            if (state.activeToolbarItem === ToolbarItem.JOB_SEEKERS) {
                this.router.navigate([this.JOB_SEEKERS_PATH]);
            } else if (state.activeToolbarItem === ToolbarItem.COMPANIES) {
                if (state.activeCompanyTabId === CompaniesTab.JOB_PUBLICATION) {
                    this.router.navigate([this.COMPANIES_JOB_PUBLICATION_PATH]);
                } else {
                    this.router.navigate([this.COMPANIES_CANDIDATES_PATH]);
                }
            } else if (state.activeToolbarItem === ToolbarItem.RECRUITMENT_AGENCIES) {
                if (state.activeAgencyTabId === AgenciesTab.CANDIDATE_SEARCH) {
                    this.router.navigate([this.AGENTS_CANDIDATES_PATH]);
                } else {
                    this.router.navigate([this.AGENTS_JOB_PUBLICATION_PATH]);
                }
            }

            return action;
        });

    constructor(private actions$: Actions,
                private router: Router,
                private store: Store<HomeState>) {
    }
}
