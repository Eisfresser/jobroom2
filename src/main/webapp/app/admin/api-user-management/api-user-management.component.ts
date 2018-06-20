import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { ApiUser, ApiUserService } from './service/api-user.service';
import {
    ApiUserManagementFilter,
    ApiUserManagementState,
    getApiUserManagementFilter,
    getApiUserManagementPage,
    getApiUserManagementTotalCount,
    getApiUsers
} from './state-management/state/api-user-management.state';
import { Store } from '@ngrx/store';
import {
    CreateApiUserAction,
    FilterApiUsersAction,
    LoadNextApiUsersPageAction
} from './state-management/action/api-user-management.actions';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ApiUserDialogService } from './service/api-user-dialog.service';

@Component({
    selector: 'jr2-api-user-management',
    templateUrl: './api-user-management.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApiUserManagementComponent implements OnInit {

    apiUsersFilterForm: FormGroup;

    apiUsers$: Observable<ApiUser[]>;
    totalCount$: Observable<number>;
    page$: Observable<number>;
    searchFilter$: Observable<ApiUserManagementFilter>;

    constructor(private store: Store<ApiUserManagementState>,
                private fb: FormBuilder,
                private apiUserService: ApiUserService,
                private apiUserDialogService: ApiUserDialogService) {
    }

    ngOnInit(): void {
        this.apiUsersFilterForm = this.fb.group({
            filter: null
        });

        this.apiUsers$ = this.store.select(getApiUsers);
        this.totalCount$ = this.store.select(getApiUserManagementTotalCount);
        this.page$ = this.store.select(getApiUserManagementPage);
        this.searchFilter$ = this.store.select(getApiUserManagementFilter);

        this.store.dispatch(new LoadNextApiUsersPageAction({ page: 0 }));
    }

    onSortOrderChange(apiUserFilter: ApiUserManagementFilter): void {
        this.store.dispatch(new FilterApiUsersAction(apiUserFilter))
    }

    filter(apiUserFilter: ApiUserManagementFilter): void {
        this.store.dispatch(new FilterApiUsersAction(Object.assign({}, apiUserFilter, {
            query: this.apiUsersFilterForm.value
        })));
    }

    openCreateNewDialog(): void {
        const onSubmit = (apiUser: ApiUser) => this.store.dispatch(new CreateApiUserAction(apiUser));
        this.apiUserDialogService.open(onSubmit);
    }
}
