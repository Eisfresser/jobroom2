export class SystemNotification {
    id: number;
    title: string;
    text: string;
    type: string;
    startDate: string;
    endDate: string;
    active: boolean;

    constructor(
        id: number,
        title: string,
        text: string,
        type: string,
        startDate: string,
        endDate: string,
        active: boolean
    ) {
        this.id = id ? id : null;
        this.title = title ? title : null;
        this.text = text ? text : null;
        this.type = type ? type : null;
        this.startDate = startDate ? startDate : null;
        this.endDate = endDate ? endDate : null;
        this.active = active ? active : null;
    }
}
