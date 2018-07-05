export class AuditData {
    constructor(
        public remoteAddress: string,
        public sessionId: string,
        public type: string,
        public message: string,
        public details: string
    ) { }
}
