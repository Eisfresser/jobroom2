package ch.admin.seco.jobroom.web.rest.errors;

public class SystemNotificationIdAlreadyUsedException extends BadRequestAlertException {

    public SystemNotificationIdAlreadyUsedException() {
        super(ErrorConstants.ID_ALREADY_USED, "ID already in use", "systemNotification", "systemnotificationalreadyexists");
    }
}
