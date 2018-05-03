import { createJobAdvertisementCancellationRequest } from '../../../../../../../main/webapp/app/dashboard/state-management/util/cancellation-request.mapper';
import { CancellationData } from '../../../../../../../main/webapp/app/dashboard/dialogs/cancellation-data';
import { CancellationReason } from '../../../../../../../main/webapp/app/shared/job-advertisement/job-advertisement.model';

describe('createJobPublicationCancelRequest', () => {

    it('should return NOT_OCCUPIED cancellationReason', () => {
        // GIVEN
        const cancellationData: CancellationData = {
            id: 'id',
            cancellationReason: {
                positionOccupied: false,
                occupiedWith: {
                    jobCenter: false,
                    privateAgency: false,
                    self: false
                }
            }
        };

        // WHEN
        const cancelRequest = createJobAdvertisementCancellationRequest(cancellationData);

        // THEN
        expect(cancelRequest.reasonCode).toEqual(CancellationReason[CancellationReason.NOT_OCCUPIED]);
    });

    it('should return OCCUPIED_SELF cancellationReason', () => {
        // GIVEN
        const cancellationData: CancellationData = {
            id: 'id',
            cancellationReason: {
                positionOccupied: true,
                occupiedWith: {
                    jobCenter: false,
                    privateAgency: false,
                    self: true
                }
            }
        };

        // WHEN
        const cancelRequest = createJobAdvertisementCancellationRequest(cancellationData);

        // THEN
        expect(cancelRequest.reasonCode).toEqual(CancellationReason[CancellationReason.OCCUPIED_SELF]);
    });

    it('should return OCCUPIED_BOTH cancellationReason', () => {
        // GIVEN
        const cancellationData: CancellationData = {
            id: 'id',
            cancellationReason: {
                positionOccupied: true,
                occupiedWith: {
                    jobCenter: true,
                    privateAgency: true,
                    self: false
                }
            }
        };

        // WHEN
        const cancelRequest = createJobAdvertisementCancellationRequest(cancellationData);

        // THEN
        expect(cancelRequest.reasonCode).toEqual(CancellationReason[CancellationReason.OCCUPIED_BOTH]);
    });

    it('should return OCCUPIED_JOB_CENTER cancellationReason', () => {
        // GIVEN
        const cancellationData: CancellationData = {
            id: 'id',
            cancellationReason: {
                positionOccupied: true,
                occupiedWith: {
                    jobCenter: true,
                    privateAgency: false,
                    self: false
                }
            }
        };

        // WHEN
        const cancelRequest = createJobAdvertisementCancellationRequest(cancellationData);

        // THEN
        expect(cancelRequest.reasonCode).toEqual(CancellationReason[CancellationReason.OCCUPIED_JOB_CENTER]);
    });

    it('should return OCCUPIED_PRIVATE_AGENCY cancellationReason as default', () => {
        // GIVEN
        const cancellationData: CancellationData = {
            id: 'id',
            cancellationReason: {
                positionOccupied: true,
                occupiedWith: {
                    jobCenter: false,
                    privateAgency: false,
                    self: false
                }
            }
        };

        // WHEN
        const cancelRequest = createJobAdvertisementCancellationRequest(cancellationData);

        // THEN
        expect(cancelRequest.reasonCode).toEqual(CancellationReason[CancellationReason.OCCUPIED_PRIVATE_AGENCY]);
    });
});
