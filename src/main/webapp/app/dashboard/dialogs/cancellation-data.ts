import { CancellationReason } from '../../shared/job-advertisement/job-advertisement.model';

export interface CancellationData {
    id: string;
    token?: string;
    cancellationReason: CancellationReason
}
