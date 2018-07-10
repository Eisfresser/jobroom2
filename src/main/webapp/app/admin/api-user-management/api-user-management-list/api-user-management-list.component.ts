import {
    ChangeDetectionStrategy,
    Component,
    EventEmitter,
    Input,
    Output
} from '@angular/core';
import { ApiUser, ApiUserUpdatePasswordRequest } from '../service/api-user.service';
import {
    LoadNextApiUsersPageAction,
    ToggleStatusAction,
    UpdateApiUserAction,
    UpdatePasswordAction
} from '../state-management/action/api-user-management.actions';
import { Store } from '@ngrx/store';
import {
    ApiUserManagementFilter,
    ApiUserManagementState
} from '../state-management/state/api-user-management.state';
import { ITEMS_PER_PAGE } from '../../../shared';
import { ApiUserDialogService } from '../service/api-user-dialog.service';

@Component({
    selector: 'jr2-api-user-management-list',
    templateUrl: './api-user-management.list.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApiUserManagementListComponent {

    readonly PAGE_SIZE = ITEMS_PER_PAGE;
    readonly KEYWORD_FIELDS = ['username', 'createDate', 'lastAccessDate', 'active'];

    @Input() apiUsers: ApiUser[];
    @Input() totalCount: number;
    @Input() page: number;
    @Input() apiUserFilter: ApiUserManagementFilter;

    @Output() sortOrderEmitter = new EventEmitter<ApiUserManagementFilter>();

    sortByField = 'id.keyword';
    reverse: false;

    constructor(private store: Store<ApiUserManagementState>,
                private apiUserDialogService: ApiUserDialogService) {
    }

    sortApiUsers(): void {
        const sort = `${this.getSortPath()},${this.reverse ? 'asc' : 'desc'}`;
        this.sortOrderEmitter.emit(Object.assign({}, this.apiUserFilter, { sort }));
    }

    private getSortPath(): string {
        return this.KEYWORD_FIELDS.indexOf(this.sortByField) >= 0
            ? `apiUser.${this.sortByField}`
            : `apiUser.${this.sortByField}.keyword`;
    }

    loadPage(page: number): void {
        this.store.dispatch(new LoadNextApiUsersPageAction({ page }));
    }

    setActive(apiUser: ApiUser, active: boolean) {
        this.store.dispatch(new ToggleStatusAction(Object.assign({}, apiUser, { active })));
    }

    trackById(index, item: ApiUser) {
        return item.id;
    }

    openUpdateDialog(apiUser: ApiUser) {
        const onSubmit = (updatedApiUser: ApiUser) => {
            updatedApiUser = Object.assign({}, updatedApiUser, { id: apiUser.id });
            this.store.dispatch(new UpdateApiUserAction(updatedApiUser));
        };
        this.apiUserDialogService.open(onSubmit, apiUser);
    }

    openPasswordUpdateDialog(id: string) {
        const onSubmit = (password: ApiUserUpdatePasswordRequest) => {
            this.store.dispatch(new UpdatePasswordAction({ id, password }))
        };
        this.apiUserDialogService.openPasswordUpdateDialog(onSubmit);
    }
}
