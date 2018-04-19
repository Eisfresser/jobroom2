export class SystemNotification {
    id: number;
    title: string;
    type: string;
    startDate: string;
    endDate: string;
    isActive: boolean;

    constructor(
        id: number,
        title: string,
        type: string,
        startDate: string,
        endDate: string,
        isActive: boolean
    ) {
        this.id = id ? id : null;
        this.title = title ? title : null;
        this.type = type ? type : null;
        this.startDate = startDate ? startDate : null;
        this.endDate = endDate ? endDate : null;
        this.isActive = isActive ? isActive : null;
    }
}
