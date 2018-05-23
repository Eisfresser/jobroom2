import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ApiUser } from '../service/api-user.service';
import {
    LoadNextApiUsersPageAction,
    ToggleStatusAction,
    UpdateApiUserAction
} from '../state-management/action/api-user-management.actions';
import { Store } from '@ngrx/store';
import { ApiUserManagementState } from '../state-management/state/api-user-management.state';
import { ITEMS_PER_PAGE } from '../../../shared';
import { ApiUserDialogService } from '../service/api-user-dialog.service';

@Component({
    selector: 'jr2-api-user-management-list',
    templateUrl: './api-user-management.list.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApiUserManagementListComponent {
    readonly PAGE_SIZE = ITEMS_PER_PAGE;

    @Input() apiUsers: ApiUser[];
    @Input() totalCount: number;
    @Input() page: number;

    constructor(private store: Store<ApiUserManagementState>,
                private apiUserDialogService: ApiUserDialogService) {
    }

    loadPage(page: number): void {
        this.store.dispatch(new LoadNextApiUsersPageAction({ page }));
    }

    setActive(apiUser: ApiUser, active: boolean) {
        this.store.dispatch(new ToggleStatusAction(Object.assign({}, apiUser, { active })));
    }

    openUpdateDialog(apiUser: ApiUser) {
        const onSubmit = (updatedApiUser: ApiUser) => {
            updatedApiUser = Object.assign({}, updatedApiUser, { id: apiUser.id });
            this.store.dispatch(new UpdateApiUserAction(updatedApiUser));
        };
        this.apiUserDialogService.open(onSubmit, apiUser);
    }
}
