export interface OccupationMapping {
    value: number;
    type: string;
}

export class OccupationCode {

    static toString(occupationCode: OccupationCode) {
        let occupationCodeStr = `${occupationCode.type}:${occupationCode.value}`;
        if (occupationCode.classifier) {
            occupationCodeStr = `${occupationCodeStr}:${occupationCode.classifier}`;
        }
        if (occupationCode.mapping) {
            occupationCodeStr = `${occupationCodeStr},${occupationCode.mapping.type}:${occupationCode.mapping.value}`
        }
        return occupationCodeStr;
    }

    static fromString(codeAsString: string): OccupationCode {
        const codes = codeAsString.split(',');

        const codeArray = codes[0].split(':');
        const type = codeArray[0];
        const value = +codeArray[1];
        const classifier = codeArray[2];

        let mapping = null;
        if (codes.length > 1) {
            const mappingArray = codes[1].split(':');
            mapping = {
                type: mappingArray[0],
                value: +mappingArray[1]
            }
        }

        return new OccupationCode(value, type, classifier, mapping);
    }

    constructor(public value: number,
                public type: string,
                public classifier = null,
                public mapping: OccupationMapping = null) {
    }

    toString(): string {
        return OccupationCode.toString(this);
    }
}
