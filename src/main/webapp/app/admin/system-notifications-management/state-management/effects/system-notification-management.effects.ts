import { Injectable } from '@angular/core';
import { Actions, Effect } from '@ngrx/effects';
import {
    CREATE_SYSTEMNOTIFICATION,
    CreateSystemNotificationAction,
    CreateSystemNotificationFailedAction,
    CreateSystemNotificationSuccessAction,
    DELETE_SYSTEMNOTIFICATION,
    DeleteSystemNotificationAction,
    DeleteSystemNotificationFailedAction,
    DeleteSystemNotificationSuccessAction,
    GET_ALL_SYSTEMNOTIFICATIONS,
    GetAllSystemNotificationsFailedAction,
    GetAllSystemNotificationsSuccessAction,
    UPDATE_SYSTEMNOTIFICATION,
    UpdateSystemNotificationAction,
    UpdateSystemNotificationFailedAction,
    UpdateSystemNotificationSuccessAction,
} from '../actions/system-notification-management.actions';
import { SystemNotificationService } from '../../../../shared/system-notification/system.notification.service';
import { catchError, map, switchMap } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';

@Injectable()
export class SystemNotificationManagementEffects {

    @Effect()
    createSystemNotification$ = this.actions$.ofType(CREATE_SYSTEMNOTIFICATION).pipe(
        map((action: CreateSystemNotificationAction) => action.payload),
        switchMap((systemNotificationToCreate) => {
            return this.systemNotificationService
                .createSystemNotification(systemNotificationToCreate)
                .pipe(
                    map((systemNotification) => new CreateSystemNotificationSuccessAction(systemNotification)),
                    catchError((error) => of( new CreateSystemNotificationFailedAction(error)))
                );
        })
    );

    @Effect()
    loadSystemNotifications$ = this.actions$.ofType(GET_ALL_SYSTEMNOTIFICATIONS).pipe(
        switchMap(() => {
            return this.systemNotificationService.getAllSystemNotifications()
                .pipe(
                    map((systemNotifications) => new GetAllSystemNotificationsSuccessAction(systemNotifications)),
                    catchError((error) => of(new GetAllSystemNotificationsFailedAction(error)))
                );
        })
    );

    @Effect()
    updateSystemNotification$ = this.actions$.ofType(UPDATE_SYSTEMNOTIFICATION).pipe(
        map((action: UpdateSystemNotificationAction) => action.payload),
        switchMap((systemNotificationToUpdate) => {
            return this.systemNotificationService
                .updateSystemNotification(systemNotificationToUpdate)
                .pipe(
                    map(() => new UpdateSystemNotificationSuccessAction(systemNotificationToUpdate)),
                    catchError((error) => of( new UpdateSystemNotificationFailedAction(error)))
                );
        })
    );

    @Effect()
    deleteSystemNotification$ = this.actions$.ofType(DELETE_SYSTEMNOTIFICATION).pipe(
        map((action: DeleteSystemNotificationAction) => action.payload),
        switchMap((systemNotificationToDelete) => {
            return this.systemNotificationService
                .deleteSystemNotification(systemNotificationToDelete)
                .pipe(
                    map(() => new DeleteSystemNotificationSuccessAction(systemNotificationToDelete)),
                    catchError((error) => of( new DeleteSystemNotificationFailedAction(error)))
                );
        })
    );

    constructor(private actions$: Actions,
                private systemNotificationService: SystemNotificationService) {
    }
}
