export interface CancellationData {
    id: string;
    cancellationReason: {
        positionOccupied: boolean;
        occupiedWith: {
            jobCenter: boolean;
            privateAgency: boolean;
            self: boolean
        }
    };
}
