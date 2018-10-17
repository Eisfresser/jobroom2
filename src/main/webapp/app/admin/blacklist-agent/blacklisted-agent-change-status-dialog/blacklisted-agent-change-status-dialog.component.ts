import { Component } from '@angular/core';
import { ModalUtils } from '../../../shared/index';

@Component({
    selector: 'jr2-blacklisted-agent-change-status-dialog',
    templateUrl: './blacklisted-agent-change-status-dialog.component.html',
    styles: []
})
export class BlacklistedAgentChangeStatusDialogComponent {

    constructor(private modalUtils: ModalUtils) {
    }

    changeStatus() {
        this.modalUtils.closeActiveModal();
    }

    close() {
        this.modalUtils.dismissActiveModal();
    }
}
