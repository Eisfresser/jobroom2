import { Injectable } from '@angular/core';
import {
    ApiUsersLoadedAction,
    ApiUserUpdatedAction,
    CREATE_API_USER,
    CreateApiUserAction,
    FILTER_API_USERS,
    FilterApiUsersAction,
    LOAD_NEXT_API_USERS_PAGE,
    LoadNextApiUsersPageAction,
    TOGGLE_STATUS,
    ToggleStatusAction,
    UPDATE_API_USER,
    UPDATE_PASSWORD,
    UpdateApiUserAction,
    UpdatePasswordAction
} from '../action/api-user-management.actions';
import { Store } from '@ngrx/store';
import {
    ApiUserManagementFilter,
    ApiUserManagementState,
    getApiUserManagementState
} from '../state/api-user-management.state';
import { Actions, Effect } from '@ngrx/effects';
import { Observable } from 'rxjs/Observable';
import { ApiUser, ApiUserService } from '../../service/api-user.service';
import { ITEMS_PER_PAGE, ResponseWrapper } from '../../../../shared';

@Injectable()
export class ApiUserManagementEffects {

    @Effect()
    loadNextApiUsersPage$ = this.actions$
        .ofType(LOAD_NEXT_API_USERS_PAGE)
        .withLatestFrom(this.store.select(getApiUserManagementState))
        .flatMap(([action, state]: [LoadNextApiUsersPageAction, ApiUserManagementState]) => {
            return this.apiUserService.search(this.createSearchRequest(state.filter, action.payload.page))
                .map((response: ResponseWrapper) => this.toApiUsersLoadedAction(response, action.payload.page))
                .catch(() => Observable.empty());
        });

    @Effect()
    filterApiUsers$ = this.actions$
        .ofType(FILTER_API_USERS)
        .distinctUntilChanged()
        .withLatestFrom(this.store.select(getApiUserManagementState))
        .flatMap(([action, state]: [FilterApiUsersAction, ApiUserManagementState]) => {
            return this.apiUserService.search(this.createSearchRequest(state.filter, state.page))
                .map((response: ResponseWrapper) => this.toApiUsersLoadedAction(response))
                .catch(() => Observable.empty());
        });

    @Effect()
    createApiUser$ = this.actions$
        .ofType(CREATE_API_USER)
        .switchMap((action: CreateApiUserAction) => {
            return this.apiUserService.save(action.payload)
                .map(() => new LoadNextApiUsersPageAction({ page: 0 }))
                .catch(() => Observable.empty());
        });

    @Effect()
    updateApiUser$ = this.actions$
        .ofType(UPDATE_API_USER)
        .distinctUntilChanged()
        .flatMap((action: UpdateApiUserAction) => {
            return this.apiUserService.update(action.payload)
                .map((updatedApiAccessKey: ApiUser) => new ApiUserUpdatedAction(updatedApiAccessKey))
                .catch(() => Observable.empty());
        });

    @Effect()
    toggleStatus$ = this.actions$
        .ofType(TOGGLE_STATUS)
        .distinctUntilChanged()
        .flatMap((action: ToggleStatusAction) => {
            return this.apiUserService.toggleStatus(action.payload)
                .map(() => new ApiUserUpdatedAction(action.payload))
                .catch(() => Observable.empty());
        });

    @Effect({ dispatch: false })
    updatePassword$ = this.actions$
        .ofType(UPDATE_PASSWORD)
        .flatMap((action: UpdatePasswordAction) => {
            return this.apiUserService.updatePassword(action.payload.id, action.payload.password)
                .catch(() => Observable.empty());
        });

    private createSearchRequest(apiUserFilter: ApiUserManagementFilter, page: number) {
        const { query, sort } = apiUserFilter;
        return {
            query,
            sort,
            page,
            size: ITEMS_PER_PAGE
        }
    }

    private toApiUsersLoadedAction(response: ResponseWrapper, page = 0) {
        return new ApiUsersLoadedAction({
            apiUsers: response.json,
            totalCount: +response.headers.get('x-total-count'),
            page
        })
    }

    constructor(private actions$: Actions,
                private store: Store<ApiUserManagementState>,
                private apiUserService: ApiUserService) {
    }
}
