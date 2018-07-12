export interface StatusNotificationState {
    messageKey: string;
    showMessage: boolean;
}

export const initialState: StatusNotificationState = {
    messageKey: null,
    showMessage: false
};
