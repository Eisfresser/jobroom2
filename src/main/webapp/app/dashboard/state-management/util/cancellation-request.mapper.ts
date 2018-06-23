import { CancellationData } from '../../dialogs/cancellation-data';
import { JobAdvertisementCancelRequest } from '../../../shared/job-advertisement/job-advertisement-cancel-request';
import { CancellationReason } from '../../../shared/job-advertisement/job-advertisement.model';

export function createJobAdvertisementCancellationRequest(cancellationData: CancellationData): JobAdvertisementCancelRequest {
    const { id, cancellationReason } = cancellationData;
    return {
        id,
        token: cancellationData.token,
        code: CancellationReason[cancellationReason]
    } as JobAdvertisementCancelRequest;
}
