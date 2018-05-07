import { CancellationData } from '../../dialogs/cancellation-data';
import { JobAdvertisementCancelRequest } from '../../../shared/job-advertisement/job-advertisement-cancel-request';
import { CancellationReason } from '../../../shared/job-advertisement/job-advertisement.model';

interface CancellationReasonForm {
    positionOccupied: boolean;
    occupiedWith: {
        jobCenter: boolean;
        privateAgency: boolean;
        self: boolean
    }
}

export function createJobAdvertisementCancellationRequest(cancellationData: CancellationData): JobAdvertisementCancelRequest {
    const { id, cancellationReason } = cancellationData;
    return {
        id,
        reasonCode: CancellationReason[getCancellationReason(cancellationReason)]
    } as JobAdvertisementCancelRequest;
}

function getCancellationReason(cancellationReasonForm: CancellationReasonForm): CancellationReason {
    if (!cancellationReasonForm.positionOccupied) {
        return CancellationReason.NOT_OCCUPIED;
    }

    if (cancellationReasonForm.occupiedWith.self) {
        return CancellationReason.OCCUPIED_SELF;
    }

    if (cancellationReasonForm.occupiedWith.jobCenter
        && cancellationReasonForm.occupiedWith.privateAgency) {
        return CancellationReason.OCCUPIED_BOTH;
    }

    if (cancellationReasonForm.occupiedWith.jobCenter) {
        return CancellationReason.OCCUPIED_JOB_CENTER;
    }

    return CancellationReason.OCCUPIED_PRIVATE_AGENCY;
}
